package de.stefantasie.modsversionsupport.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import de.stefantasie.modsversionsupport.domain.mod.InstalledMod;
import de.stefantasie.modsversionsupport.domain.mod.ModKey;
import de.stefantasie.modsversionsupport.domain.mod.ModrinthMod;
import de.stefantasie.modsversionsupport.domain.mod.TrackedMod;
import de.stefantasie.modsversionsupport.domain.profile.ProfileList;
import de.stefantasie.modsversionsupport.domain.profile.VersionProfile;
import de.stefantasie.modsversionsupport.domain.report.ModSupport;
import de.stefantasie.modsversionsupport.domain.report.ReleaseChannel;
import de.stefantasie.modsversionsupport.domain.report.SupportReport;
import de.stefantasie.modsversionsupport.domain.selection.ModSelection;
import java.nio.file.Path;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class ProfileStoreTest {

	private final TrackedMod sodium = new InstalledMod("sodium", "Sodium", "sodium.jar", Optional.of("abc123"));
	private final TrackedMod extra = new ModrinthMod("PtjYWJkn", "Sodium Extra", Optional.of("https://example.invalid/icon.png"));

	@Test
	void writesAndReadsProfilesUnchanged(@TempDir Path directory) {
		ProfileStore store = new ProfileStore(directory.resolve("profiles.json"));
		VersionProfile profile = VersionProfile
				.create("26.2", "26.2", ModSelection.allOf(List.of(sodium, extra)).withToggled(extra.key()))
				.withReport(new SupportReport(
						List.of(ModSupport.supported(sodium.key(), "mc26.2-0.9.3", ReleaseChannel.BETA, true)),
						Instant.parse("2026-08-20T20:00:00Z")));
		store.save(ProfileList.of(List.of(profile)));

		ProfileList loaded = store.load();

		VersionProfile restored = loaded.asList().getFirst();
		assertEquals(profile.id(), restored.id());
		assertEquals("26.2", restored.displayName());
		assertEquals(List.of(sodium, extra), restored.selection().mods());
		assertEquals(List.of(sodium), restored.selection().selectedMods());
		assertTrue(restored.lastReport().isPresent());
		assertEquals(ReleaseChannel.BETA, restored.lastReport().get().results().getFirst().channel().orElseThrow());
		assertTrue(restored.lastReport().get().results().getFirst().quiltOnly());
	}

	@Test
	void missingFileYieldsAnEmptyList(@TempDir Path directory) {
		assertTrue(new ProfileStore(directory.resolve("absent.json")).load().isEmpty());
	}

	@Test
	void modKeysSurviveTheRoundTrip() {
		assertEquals(ModKey.ofModrinthProject("AANobbMI"), ModKey.parse(ModKey.ofModrinthProject("AANobbMI").stored()));
	}
}
