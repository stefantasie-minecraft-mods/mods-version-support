package de.stefantasie.modsversionsupport.modrinth.cache;

import static org.junit.jupiter.api.Assertions.assertTrue;

import de.stefantasie.modsversionsupport.domain.report.ReleaseChannel;
import de.stefantasie.modsversionsupport.modrinth.project.ModrinthVersion;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.Test;

class VersionSupportCacheTest {

	private final AtomicReference<Instant> now = new AtomicReference<>(Instant.parse("2026-08-20T20:00:00Z"));
	private final VersionSupportCache cache = new VersionSupportCache(Duration.ofMinutes(10), now::get);
	private final List<ModrinthVersion> versions = List.of(new ModrinthVersion("1.0", ReleaseChannel.RELEASE, List.of("fabric")));

	@Test
	void servesAStoredAnswerWithinItsLifetime() {
		cache.put("sodium", "26.2", versions);
		now.set(now.get().plus(Duration.ofMinutes(9)));

		assertTrue(cache.get("sodium", "26.2").isPresent());
	}

	@Test
	void forgetsAnswersThatGrewTooOld() {
		cache.put("sodium", "26.2", versions);
		now.set(now.get().plus(Duration.ofMinutes(11)));

		assertTrue(cache.get("sodium", "26.2").isEmpty());
	}

	@Test
	void keepsVersionsApartPerTargetVersion() {
		cache.put("sodium", "26.2", versions);

		assertTrue(cache.get("sodium", "26.3").isEmpty());
	}
}
