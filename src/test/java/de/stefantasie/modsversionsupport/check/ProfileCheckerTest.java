package de.stefantasie.modsversionsupport.check;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import de.stefantasie.modsversionsupport.domain.mod.InstalledMod;
import de.stefantasie.modsversionsupport.domain.mod.ModrinthMod;
import de.stefantasie.modsversionsupport.domain.mod.TrackedMod;
import de.stefantasie.modsversionsupport.domain.profile.VersionProfile;
import de.stefantasie.modsversionsupport.domain.report.CheckProgress;
import de.stefantasie.modsversionsupport.domain.report.SupportReport;
import de.stefantasie.modsversionsupport.domain.report.SupportState;
import de.stefantasie.modsversionsupport.domain.selection.ModSelection;
import de.stefantasie.modsversionsupport.http.RecordingJsonHttpClient;
import de.stefantasie.modsversionsupport.http.RoutedJsonHttpClient;
import de.stefantasie.modsversionsupport.http.UnreachableJsonHttpClient;
import de.stefantasie.modsversionsupport.modrinth.cache.TimedCache;
import de.stefantasie.modsversionsupport.modrinth.hash.HashLookupGateway;
import de.stefantasie.modsversionsupport.modrinth.project.ModrinthVersion;
import de.stefantasie.modsversionsupport.modrinth.project.ProjectGateway;
import de.stefantasie.modsversionsupport.modrinth.project.ProjectVersionGateway;
import de.stefantasie.modsversionsupport.mojang.versions.GameVersion;
import de.stefantasie.modsversionsupport.mojang.versions.ReleaseType;
import de.stefantasie.modsversionsupport.mojang.versions.VersionCatalog;
import de.stefantasie.modsversionsupport.mojang.versions.VersionRanking;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class ProfileCheckerTest {

	private static final String FABRIC_API_SHA1 = "5fe5204ccc96a17340570db66a5b4127b8176246";
	private static final Instant FINISHED_AT = Instant.parse("2026-08-20T20:00:00Z");

	private final TrackedMod fabricApi = new InstalledMod("fabric-api", "Fabric API", "fabric-api.jar", Optional.of(FABRIC_API_SHA1));
	private final TrackedMod homemade = new InstalledMod("homemade", "Homemade", "homemade.jar", Optional.empty());
	private final TrackedMod added = new ModrinthMod("AANobbMI", "Sodium", Optional.empty());

	private final RecordingJsonHttpClient versionHttp = RecordingJsonHttpClient.answering("sodium-versions.json");
	private final TimedCache<List<ModrinthVersion>> versionCache = TimedCache.lasting(Duration.ofMinutes(10));

	private final VersionRanking ranking = VersionRanking.of(new VersionCatalog(List.of(
			new GameVersion("26.3", ReleaseType.RELEASE, Instant.parse("2026-09-01T09:00:00Z")),
			new GameVersion("26.2", ReleaseType.RELEASE, Instant.parse("2026-07-01T09:00:00Z")),
			new GameVersion("26.1.2", ReleaseType.RELEASE, Instant.parse("2026-05-12T10:00:00Z")))));

	private ProfileChecker checkerWith(String hashFixture) {
		return new ProfileChecker(
				new ModProjectResolver(new HashLookupGateway(RecordingJsonHttpClient.answering(hashFixture))),
				new ProjectVersionGateway(versionHttp, List.of("fabric", "quilt")),
				new ProjectGateway(RecordingJsonHttpClient.answering("project-sodium.json")),
				versionCache,
				TimedCache.lasting(Duration.ofMinutes(10)),
				() -> ranking,
				() -> FINISHED_AT);
	}

	@Test
	void reportsStatePerMod() {
		SupportReport report = checkerWith("version-files.json")
				.check(profileWith(fabricApi, homemade, added), progress -> {
				}, () -> false);

		assertEquals(SupportState.SUPPORTED_PRERELEASE, report.forMod(fabricApi.key()).orElseThrow().state());
		assertEquals(SupportState.NOT_ON_MODRINTH, report.forMod(homemade.key()).orElseThrow().state());
		assertEquals(SupportState.SUPPORTED_PRERELEASE, report.forMod(added.key()).orElseThrow().state());
	}

	@Test
	void unmatchedJarsCountAgainstThePercentage() {
		SupportReport report = checkerWith("no-matches.json")
				.check(profileWith(fabricApi, homemade), progress -> {
				}, () -> false);

		assertEquals(0, report.percent());
	}

	@Test
	void progressCountsEverySelectedMod() {
		List<CheckProgress> reported = new ArrayList<>();

		checkerWith("version-files.json").check(profileWith(fabricApi, added), reported::add, () -> false);

		assertEquals(new CheckProgress(0, 2), reported.getFirst());
		assertEquals(new CheckProgress(2, 2), reported.getLast());
	}

	@Test
	void cachedAnswersSpareTheSecondRequest() {
		checkerWith("version-files.json").check(profileWith(added), progress -> {
		}, () -> false);
		checkerWith("version-files.json").check(profileWith(added), progress -> {
		}, () -> false);

		assertEquals(1, versionHttp.requestedUris().size());
	}

	@Test
	void unsupportedModsReportTheNewestVersionTheyStillHave() {
		RoutedJsonHttpClient modrinth = new RoutedJsonHttpClient()
				.route("/version?", "no-versions.json")
				.route("/project/", "project-sodium.json");
		ProfileChecker checker = new ProfileChecker(
				new ModProjectResolver(new HashLookupGateway(RecordingJsonHttpClient.answering("no-matches.json"))),
				new ProjectVersionGateway(modrinth, List.of("fabric")),
				new ProjectGateway(modrinth),
				TimedCache.lasting(Duration.ofMinutes(10)),
				TimedCache.lasting(Duration.ofMinutes(10)),
				() -> ranking,
				() -> FINISHED_AT);

		SupportReport report = checker.check(
				VersionProfile.create("26.3", "26.3", ModSelection.allOf(List.of(added))), progress -> {
				}, () -> false);

		assertEquals(SupportState.UNSUPPORTED, report.results().getFirst().state());
		assertEquals("26.2", report.results().getFirst().newestSupportedGameVersion().orElseThrow());
	}

	@Test
	void anUnreachableApiMarksModsAsFailed() {
		ProfileChecker offline = new ProfileChecker(
				new ModProjectResolver(new HashLookupGateway(RecordingJsonHttpClient.answering("version-files.json"))),
				new ProjectVersionGateway(UnreachableJsonHttpClient.answering(503), List.of("fabric")),
				new ProjectGateway(UnreachableJsonHttpClient.answering(503)),
				TimedCache.lasting(Duration.ofMinutes(10)),
				TimedCache.lasting(Duration.ofMinutes(10)),
				() -> ranking,
				() -> FINISHED_AT);

		SupportReport report = offline.check(profileWith(fabricApi), progress -> {
		}, () -> false);

		assertEquals(SupportState.FAILED, report.results().getFirst().state());
	}

	@Test
	void cancellingStopsTheRun() {
		assertThrows(CheckCancelledException.class, () -> checkerWith("version-files.json")
				.check(profileWith(fabricApi), progress -> {
				}, () -> true));
	}

	private VersionProfile profileWith(TrackedMod... mods) {
		return VersionProfile.create("26.1.2", "26.1.2", ModSelection.allOf(List.of(mods)));
	}
}
