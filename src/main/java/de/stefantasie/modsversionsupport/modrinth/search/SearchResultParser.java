package de.stefantasie.modsversionsupport.modrinth.search;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class SearchResultParser {

	private SearchResultParser() {
	}

	public static List<SearchHit> parse(JsonElement answer) {
		List<SearchHit> hits = new ArrayList<>();
		for (JsonElement element : answer.getAsJsonObject().getAsJsonArray("hits")) {
			JsonObject hit = element.getAsJsonObject();
			hits.add(new SearchHit(
					hit.get("project_id").getAsString(),
					hit.get("slug").getAsString(),
					hit.get("title").getAsString(),
					iconUrl(hit)));
		}
		return List.copyOf(hits);
	}

	private static Optional<String> iconUrl(JsonObject hit) {
		return hit.has("icon_url") && !hit.get("icon_url").isJsonNull() && !hit.get("icon_url").getAsString().isBlank()
				? Optional.of(hit.get("icon_url").getAsString())
				: Optional.empty();
	}
}
