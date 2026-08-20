package de.stefantasie.modsversionsupport.modrinth.http;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class RateLimiterTest {

	private final List<Duration> waits = new ArrayList<>();
	private final RateLimiter limiter = new RateLimiter(3, waits::add);

	@Test
	void letsRequestsPassWhileBelowTheLimit() {
		limiter.acquire();
		limiter.acquire();
		limiter.acquire();

		assertTrue(waits.isEmpty());
	}

	@Test
	void waitsOnceTheWindowIsFull() {
		limiter.acquire();
		limiter.acquire();
		limiter.acquire();
		limiter.acquire();

		assertEquals(1, waits.size());
		assertTrue(waits.getFirst().compareTo(Duration.ofMinutes(1)) <= 0);
	}
}
