package de.stefantasie.modsversionsupport.mojang.versions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import de.stefantasie.modsversionsupport.http.JsonHttpClient;
import de.stefantasie.modsversionsupport.http.UnreachableJsonHttpClient;
import java.nio.file.Path;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class VersionCatalogProviderTest {

	private static final JsonHttpClient UNREACHABLE = UnreachableJsonHttpClient.offline();

	private final VersionCatalog stored = new VersionCatalog(List.of(
			new GameVersion("26.1.2", ReleaseType.RELEASE, Instant.parse("2026-05-12T10:00:00Z"))));

	@Test
	void fallsBackToTheStoredListWhenMojangIsUnreachable(@TempDir Path directory) {
		VersionCatalogStore store = new VersionCatalogStore(directory.resolve("versions.json"));
		store.write(stored);

		VersionCatalog served = new VersionCatalogProvider(new VersionManifestGateway(UNREACHABLE), store).get();

		assertEquals(List.of("26.1.2"), served.versions().stream().map(GameVersion::id).toList());
	}

	@Test
	void withoutAnyCopyThePickerStaysEmpty(@TempDir Path directory) {
		VersionCatalogStore store = new VersionCatalogStore(directory.resolve("versions.json"));

		assertTrue(new VersionCatalogProvider(new VersionManifestGateway(UNREACHABLE), store).get().versions().isEmpty());
	}
}
