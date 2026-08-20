package de.stefantasie.modsversionsupport.modrinth.http;

import com.google.gson.JsonElement;
import de.stefantasie.modsversionsupport.http.HttpFailure;
import de.stefantasie.modsversionsupport.http.JsonHttpClient;
import java.net.URI;
import java.time.Duration;
import java.util.function.Supplier;

/** Wraps a client with the rate limit and a backoff for the answer Modrinth gives when it is exceeded. */
public final class ThrottledModrinthHttp implements JsonHttpClient {

	private static final int ATTEMPTS = 3;
	private static final Duration BACKOFF = Duration.ofSeconds(5);

	private final JsonHttpClient delegate;
	private final RateLimiter limiter;
	private final Sleeper sleeper;

	public ThrottledModrinthHttp(JsonHttpClient delegate, RateLimiter limiter, Sleeper sleeper) {
		this.delegate = delegate;
		this.limiter = limiter;
		this.sleeper = sleeper;
	}

	@Override
	public JsonElement get(URI uri) {
		return withBackoff(() -> delegate.get(uri));
	}

	@Override
	public JsonElement post(URI uri, JsonElement body) {
		return withBackoff(() -> delegate.post(uri, body));
	}

	private JsonElement withBackoff(Supplier<JsonElement> request) {
		HttpFailure lastFailure = null;
		for (int attempt = 1; attempt <= ATTEMPTS; attempt++) {
			limiter.acquire();
			try {
				return request.get();
			} catch (HttpFailure failure) {
				if (!failure.isRateLimited()) {
					throw failure;
				}
				lastFailure = failure;
				sleeper.sleep(BACKOFF.multipliedBy(attempt));
			}
		}
		throw lastFailure;
	}
}
