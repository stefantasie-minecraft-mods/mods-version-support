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
import de.stefantasie.modsversionsupport.http.UnreachableJsonHttpClient;
import de.stefantasie.modsversionsupport.modrinth.cache.VersionSupportCache;
import de.stefantasie.modsversionsupport.modrinth.hash.HashLookupGateway;
import de.stefantasie.modsversionsupport.modrinth.project.ProjectVersionGateway;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class ProfileCheckerTest {

	private static final String FABRIC_API_SHA1 = "5fe5204ccc96a17340570db66a5b4127b8176246";

	private final TrackedMod fabricApi = new InstalledMod("fabric-api", "Fabric API", "fabric-api.jar", Optional.of(FABRIC_API_SHA1));
	private final TrackedMod homemade = new InstalledMod("homemade", "Homemade", "homemade.jar", Optional.empty());
	private final TrackedMod added = new ModrinthMod("AANobbMI", "Sodium", Optional.empty());

	private final RecordingJsonHttpClient versionHttp = RecordingJsonHttpClient.answering("sodium-versions.json");
	private final VersionSupportCache cache = VersionSupportCache.lasting(Duration.ofMinutes(10));

	private ProfileChecker checkerWith(String hashFixture) {
		return new ProfileChecker(
				new ModProjectResolver(new HashLookupGateway(RecordingJsonHttpClient.answering(hashFixture))),
				new ProjectVersionGateway(versionHttp, List.of("fabric", "quilt")),
				cache,
				() -> Instant.parse("2026-08-20T20:00:00Z"));
	}

	@Test
	void reportsStatePerMod() {
		VersionProfile profile = profileWith(fabricApi, homemade, added);

		SupportReport report = checkerWith("version-files.json").check(profile, progress -> {
		}, () -> false);

		assertEquals(SupportState.SUPPORTED_PRERELEASE, report.forMod(fabricApi.key()).orElseThrow().state());
		assertEquals(SupportState.NOT_ON_MODRINTH, report.forMod(homemade.key()).orElseThrow().state());
		assertEquals(SupportState.SUPPORTED_PRERELEASE, report.forMod(added.key()).orElseThrow().state());
	}

	@Test
	void unmatchedJarsCountAgainstThePercentage() {
		VersionProfile profile = profileWith(fabricApi, homemade);

		SupportReport report = checkerWith("no-matches.json").check(profile, progress -> {
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
	void anUnreachableApiMarksModsAsFailed() {
		ProfileChecker offline = new ProfileChecker(
				new ModProjectResolver(new HashLookupGateway(RecordingJsonHttpClient.answering("version-files.json"))),
				new ProjectVersionGateway(UnreachableJsonHttpClient.answering(503), List.of("fabric")),
				VersionSupportCache.lasting(Duration.ofMinutes(10)),
				() -> Instant.parse("2026-08-20T20:00:00Z"));

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
