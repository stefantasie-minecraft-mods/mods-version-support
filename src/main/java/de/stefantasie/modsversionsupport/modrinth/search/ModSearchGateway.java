package de.stefantasie.modsversionsupport.modrinth.search;

import de.stefantasie.modsversionsupport.http.JsonHttpClient;
import de.stefantasie.modsversionsupport.modrinth.ModrinthEndpoints;
import java.util.List;

public final class ModSearchGateway {

	private static final int SUGGESTION_LIMIT = 12;

	private final JsonHttpClient http;

	public ModSearchGateway(JsonHttpClient http) {
		this.http = http;
	}

	public List<SearchHit> search(String query) {
		if (query.isBlank()) {
			return List.of();
		}
		return SearchResultParser.parse(http.get(ModrinthEndpoints.search(query, SUGGESTION_LIMIT)));
	}
}
