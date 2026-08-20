package de.stefantasie.modsversionsupport.mojang.versions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class VersionRankingTest {

	private final VersionRanking ranking = VersionRanking.of(new VersionCatalog(List.of(
			new GameVersion("26.2", ReleaseType.RELEASE, Instant.parse("2026-07-01T09:00:00Z")),
			new GameVersion("26.1.2", ReleaseType.RELEASE, Instant.parse("2026-05-12T10:00:00Z")),
			new GameVersion("26.1", ReleaseType.RELEASE, Instant.parse("2026-03-24T10:00:00Z")),
			new GameVersion("1.21.11", ReleaseType.RELEASE, Instant.parse("2025-11-04T08:00:00Z")))));

	@Test
	void picksTheNewestVersionThatIsNotAheadOfTheTarget() {
		assertEquals(Optional.of("26.1.2"), ranking.newestAtMost("26.2", List.of("1.21.11", "26.1", "26.1.2")));
	}

	@Test
	void theTargetItselfCounts() {
		assertEquals(Optional.of("26.2"), ranking.newestAtMost("26.2", List.of("26.1", "26.2")));
	}

	@Test
	void ignoresVersionsNewerThanTheTarget() {
		assertEquals(Optional.of("1.21.11"), ranking.newestAtMost("1.21.11", List.of("26.2", "1.21.11")));
	}

	@Test
	void withoutAnyOlderVersionThereIsNoAnswer() {
		assertTrue(ranking.newestAtMost("1.21.11", List.of("26.2")).isEmpty());
	}

	@Test
	void anUnknownTargetYieldsNothing() {
		assertTrue(ranking.newestAtMost("42.0", List.of("26.2")).isEmpty());
	}
}
