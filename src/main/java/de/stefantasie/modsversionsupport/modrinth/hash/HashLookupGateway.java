package de.stefantasie.modsversionsupport.modrinth.hash;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.stefantasie.modsversionsupport.http.JsonHttpClient;
import de.stefantasie.modsversionsupport.modrinth.ModrinthEndpoints;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Resolves installed jars to Modrinth projects in a single request. */
public final class HashLookupGateway {

	private static final int BATCH_SIZE = 100;

	private final JsonHttpClient http;

	public HashLookupGateway(JsonHttpClient http) {
		this.http = http;
	}

	public Map<String, HashMatch> lookup(Collection<String> sha1Hashes) {
		Map<String, HashMatch> matches = new LinkedHashMap<>();
		for (List<String> batch : batches(List.copyOf(sha1Hashes))) {
			matches.putAll(lookupBatch(batch));
		}
		return Map.copyOf(matches);
	}

	private Map<String, HashMatch> lookupBatch(List<String> sha1Hashes) {
		JsonArray hashes = new JsonArray();
		sha1Hashes.forEach(hashes::add);

		JsonObject body = new JsonObject();
		body.add("hashes", hashes);
		body.addProperty("algorithm", "sha1");

		JsonElement answer = http.post(ModrinthEndpoints.versionFiles(), body);
		return HashLookupParser.parse(answer);
	}

	private static List<List<String>> batches(List<String> hashes) {
		List<List<String>> batches = new ArrayList<>();
		for (int start = 0; start < hashes.size(); start += BATCH_SIZE) {
			batches.add(hashes.subList(start, Math.min(start + BATCH_SIZE, hashes.size())));
		}
		return batches;
	}
}
