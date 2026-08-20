package de.stefantasie.modsversionsupport.http;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/** Answers with fixtures recorded from the live API and remembers what was asked. */
public final class RecordingJsonHttpClient implements JsonHttpClient {

	private final JsonElement answer;
	private final List<URI> requestedUris = new ArrayList<>();
	private final List<JsonElement> postedBodies = new ArrayList<>();

	public RecordingJsonHttpClient(JsonElement answer) {
		this.answer = answer;
	}

	public static RecordingJsonHttpClient answering(String fixtureName) {
		return new RecordingJsonHttpClient(JsonParser.parseString(fixture(fixtureName)));
	}

	public static String fixture(String fixtureName) {
		try (var stream = RecordingJsonHttpClient.class.getResourceAsStream("/modrinth/" + fixtureName)) {
			return new String(stream.readAllBytes(), StandardCharsets.UTF_8);
		} catch (IOException cause) {
			throw new UncheckedIOException("Missing fixture " + fixtureName, cause);
		}
	}

	public List<URI> requestedUris() {
		return List.copyOf(requestedUris);
	}

	public List<JsonElement> postedBodies() {
		return List.copyOf(postedBodies);
	}

	@Override
	public JsonElement get(URI uri) {
		requestedUris.add(uri);
		return answer;
	}

	@Override
	public JsonElement post(URI uri, JsonElement body) {
		requestedUris.add(uri);
		postedBodies.add(body);
		return answer;
	}
}
