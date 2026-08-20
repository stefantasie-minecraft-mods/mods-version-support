package de.stefantasie.modsversionsupport.http;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Answers with a different recorded fixture depending on what the URI contains. */
public final class RoutedJsonHttpClient implements JsonHttpClient {

	private final Map<String, String> fixtureByUriPart = new LinkedHashMap<>();
	private final List<URI> requestedUris = new ArrayList<>();

	public RoutedJsonHttpClient route(String uriPart, String fixtureName) {
		fixtureByUriPart.put(uriPart, fixtureName);
		return this;
	}

	public List<URI> requestedUris() {
		return List.copyOf(requestedUris);
	}

	@Override
	public JsonElement get(URI uri) {
		requestedUris.add(uri);
		return JsonParser.parseString(RecordingJsonHttpClient.fixture(fixtureFor(uri)));
	}

	@Override
	public JsonElement post(URI uri, JsonElement body) {
		return get(uri);
	}

	private String fixtureFor(URI uri) {
		return fixtureByUriPart.entrySet().stream()
				.filter(entry -> uri.toString().contains(entry.getKey()))
				.map(Map.Entry::getValue)
				.findFirst()
				.orElseThrow(() -> new IllegalStateException("No fixture routed for " + uri));
	}
}
