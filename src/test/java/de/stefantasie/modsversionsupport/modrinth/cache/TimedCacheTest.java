package de.stefantasie.modsversionsupport.modrinth.cache;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.Test;

class TimedCacheTest {

	private final AtomicReference<Instant> now = new AtomicReference<>(Instant.parse("2026-08-20T20:00:00Z"));
	private final TimedCache<List<String>> cache = new TimedCache<>(Duration.ofMinutes(10), now::get);

	@Test
	void servesAStoredAnswerWithinItsLifetime() {
		cache.put("sodium@26.2", List.of("26.2"));
		now.set(now.get().plus(Duration.ofMinutes(9)));

		assertTrue(cache.get("sodium@26.2").isPresent());
	}

	@Test
	void forgetsAnswersThatGrewTooOld() {
		cache.put("sodium@26.2", List.of("26.2"));
		now.set(now.get().plus(Duration.ofMinutes(11)));

		assertTrue(cache.get("sodium@26.2").isEmpty());
	}

	@Test
	void keepsKeysApart() {
		cache.put("sodium@26.2", List.of("26.2"));

		assertTrue(cache.get("sodium@26.3").isEmpty());
	}
}
