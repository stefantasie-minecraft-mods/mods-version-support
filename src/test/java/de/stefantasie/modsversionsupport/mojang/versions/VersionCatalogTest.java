package de.stefantasie.modsversionsupport.mojang.versions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.gson.JsonParser;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;

class VersionCatalogTest {

	private static final String MANIFEST = """
			{
			  "versions": [
			    {"id": "26.1.2", "type": "release", "releaseTime": "2026-05-12T10:00:00+00:00"},
			    {"id": "26.3-snapshot-9", "type": "snapshot", "releaseTime": "2026-08-17T11:46:16+00:00"},
			    {"id": "26.2", "type": "release", "releaseTime": "2026-07-01T09:00:00+00:00"},
			    {"id": "1.21.11", "type": "release", "releaseTime": "2025-11-04T08:00:00+00:00"}
			  ]
			}
			""";

	private final VersionCatalog catalog = new VersionCatalog(VersionManifestParser.parse(JsonParser.parseString(MANIFEST)));

	@Test
	void ordersNewestFirst() {
		assertEquals(List.of("26.3-snapshot-9", "26.2", "26.1.2", "1.21.11"), idsOf(catalog.versions()));
	}

	@Test
	void hidesSnapshotsUnlessAsked() {
		assertEquals(List.of("26.2", "26.1.2", "1.21.11"), idsOf(catalog.visible(false)));
		assertEquals(4, catalog.visible(true).size());
	}

	@Test
	void prefixMatchesComeBeforeContainedOnes() {
		assertEquals(List.of("26.3-snapshot-9", "26.2"), idsOf(catalog.matching("26.", true)).subList(0, 2));
		assertEquals(List.of("1.21.11"), idsOf(catalog.matching("1.21", true)));
	}

	@Test
	void olderVersionsStaySearchable() {
		assertTrue(catalog.contains("1.21.11"));
		assertEquals(List.of("1.21.11"), idsOf(catalog.matching("1.21.11", false)));
	}

	@Test
	void parsesReleaseTimes() {
		assertEquals(Instant.parse("2026-07-01T09:00:00Z"), catalog.versions().get(1).releasedAt());
	}

	private List<String> idsOf(List<GameVersion> versions) {
		return versions.stream().map(GameVersion::id).toList();
	}
}
