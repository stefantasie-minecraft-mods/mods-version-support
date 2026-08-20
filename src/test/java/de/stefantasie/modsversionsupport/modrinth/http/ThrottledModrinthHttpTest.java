package de.stefantasie.modsversionsupport.modrinth.http;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import de.stefantasie.modsversionsupport.http.HttpFailure;
import de.stefantasie.modsversionsupport.http.JsonHttpClient;
import java.net.URI;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class ThrottledModrinthHttpTest {

	private static final URI ANY = URI.create("https://api.modrinth.com/v2/search");

	private final List<Duration> waits = new ArrayList<>();
	private final RateLimiter limiter = new RateLimiter(300, waits::add);

	@Test
	void retriesAfterTheRateLimitAnswer() {
		FailingTimes client = new FailingTimes(2, 429);

		JsonElement answer = new ThrottledModrinthHttp(client, limiter, waits::add).get(ANY);

		assertEquals(3, client.attempts);
		assertTrue(answer.isJsonObject());
		assertEquals(2, waits.size());
	}

	@Test
	void otherFailuresReachTheCaller() {
		FailingTimes client = new FailingTimes(1, 500);

		assertThrows(HttpFailure.class, () -> new ThrottledModrinthHttp(client, limiter, waits::add).get(ANY));
		assertEquals(1, client.attempts);
	}

	private static final class FailingTimes implements JsonHttpClient {

		private final int failures;
		private final int statusCode;
		private int attempts;

		private FailingTimes(int failures, int statusCode) {
			this.failures = failures;
			this.statusCode = statusCode;
		}

		@Override
		public JsonElement get(URI uri) {
			attempts++;
			if (attempts <= failures) {
				throw new HttpFailure("simulated", statusCode);
			}
			return JsonParser.parseString("{\"hits\": []}");
		}

		@Override
		public JsonElement post(URI uri, JsonElement body) {
			return get(uri);
		}
	}
}
