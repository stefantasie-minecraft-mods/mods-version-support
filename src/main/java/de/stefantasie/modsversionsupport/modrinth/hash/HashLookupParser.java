package de.stefantasie.modsversionsupport.modrinth.hash;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.LinkedHashMap;
import java.util.Map;

public final class HashLookupParser {

	private HashLookupParser() {
	}

	public static Map<String, HashMatch> parse(JsonElement answer) {
		Map<String, HashMatch> matches = new LinkedHashMap<>();
		JsonObject byHash = answer.getAsJsonObject();
		for (String sha1 : byHash.keySet()) {
			JsonObject version = byHash.getAsJsonObject(sha1);
			matches.put(sha1, new HashMatch(sha1, version.get("project_id").getAsString()));
		}
		return matches;
	}
}
