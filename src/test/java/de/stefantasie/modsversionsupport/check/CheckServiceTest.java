package de.stefantasie.modsversionsupport.check;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import de.stefantasie.modsversionsupport.domain.mod.ModrinthMod;
import de.stefantasie.modsversionsupport.domain.mod.TrackedMod;
import de.stefantasie.modsversionsupport.domain.profile.VersionProfile;
import de.stefantasie.modsversionsupport.domain.report.SupportReport;
import de.stefantasie.modsversionsupport.domain.selection.ModSelection;
import de.stefantasie.modsversionsupport.http.RecordingJsonHttpClient;
import de.stefantasie.modsversionsupport.modrinth.cache.TimedCache;
import de.stefantasie.modsversionsupport.modrinth.hash.HashLookupGateway;
import de.stefantasie.modsversionsupport.modrinth.project.ProjectGateway;
import de.stefantasie.modsversionsupport.modrinth.project.ProjectVersionGateway;
import de.stefantasie.modsversionsupport.mojang.versions.VersionCatalog;
import de.stefantasie.modsversionsupport.mojang.versions.VersionRanking;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;

class CheckServiceTest {

	private final TrackedMod sodium = new ModrinthMod("AANobbMI", "Sodium", Optional.empty());

	private final ProfileChecker checker = new ProfileChecker(
			new ModProjectResolver(new HashLookupGateway(RecordingJsonHttpClient.answering("no-matches.json"))),
			new ProjectVersionGateway(RecordingJsonHttpClient.answering("sodium-versions.json"), List.of("fabric")),
			new ProjectGateway(RecordingJsonHttpClient.answering("project-sodium.json")),
			TimedCache.lasting(Duration.ofMinutes(5)),
			TimedCache.lasting(Duration.ofMinutes(5)),
			() -> VersionRanking.of(VersionCatalog.empty()),
			Instant::now);

	@Test
	void deliversTheReportOfABackgroundRun() throws Exception {
		try (CheckService service = new CheckService(checker, 2)) {
			VersionProfile profile = VersionProfile.create("26.1.2", "26.1.2", ModSelection.allOf(List.of(sodium)));

			SupportReport report = service.start(profile).whenFinished().get(10, TimeUnit.SECONDS);

			assertEquals(100, report.percent());
		}
	}

	@Test
	void tracksRunsPerProfile() throws Exception {
		try (CheckService service = new CheckService(checker, 2)) {
			VersionProfile first = VersionProfile.create("26.1.2", "26.1.2", ModSelection.allOf(List.of(sodium)));
			VersionProfile second = VersionProfile.create("26.2", "26.2", ModSelection.allOf(List.of(sodium)));

			service.start(first).whenFinished().get(10, TimeUnit.SECONDS);
			service.start(second).whenFinished().get(10, TimeUnit.SECONDS);

			assertTrue(service.of(first.id()).orElseThrow().finishedReport().isPresent());
			assertEquals(second.id(), service.of(second.id()).orElseThrow().profile());
		}
	}

	@Test
	void cancellingForgetsTheRun() {
		try (CheckService service = new CheckService(checker, 1)) {
			VersionProfile profile = VersionProfile.create("26.1.2", "26.1.2", ModSelection.allOf(List.of(sodium)));
			service.start(profile);

			service.cancel(profile.id());

			assertTrue(service.of(profile.id()).isEmpty());
		}
	}
}
