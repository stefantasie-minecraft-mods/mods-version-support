package de.stefantasie.modsversionsupport.http;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public final class JavaNetJsonHttpClient implements JsonHttpClient {

	private static final Duration TIMEOUT = Duration.ofSeconds(20);

	private final HttpClient client;
	private final String userAgent;

	public JavaNetJsonHttpClient(String userAgent) {
		this.userAgent = userAgent;
		this.client = HttpClient.newBuilder().connectTimeout(TIMEOUT).followRedirects(HttpClient.Redirect.NORMAL).build();
	}

	@Override
	public JsonElement get(URI uri) {
		return send(requestBuilder(uri).GET().build());
	}

	@Override
	public JsonElement post(URI uri, JsonElement body) {
		HttpRequest request = requestBuilder(uri)
				.header("Content-Type", "application/json")
				.POST(HttpRequest.BodyPublishers.ofString(body.toString()))
				.build();
		return send(request);
	}

	private HttpRequest.Builder requestBuilder(URI uri) {
		return HttpRequest.newBuilder(uri)
				.timeout(TIMEOUT)
				.header("User-Agent", userAgent)
				.header("Accept", "application/json");
	}

	private JsonElement send(HttpRequest request) {
		try {
			HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
			if (response.statusCode() >= 400) {
				throw new HttpFailure(request.uri() + " answered " + response.statusCode(), response.statusCode());
			}
			return JsonParser.parseString(response.body());
		} catch (IOException cause) {
			throw new HttpFailure("Request to " + request.uri() + " failed", cause);
		} catch (InterruptedException cause) {
			Thread.currentThread().interrupt();
			throw new HttpFailure("Request to " + request.uri() + " was interrupted", cause);
		}
	}
}
