package de.stefantasie.modsversionsupport.modrinth;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

public final class ModrinthEndpoints {

	private static final String BASE = "https://api.modrinth.com/v2";

	private ModrinthEndpoints() {
	}

	public static URI versionFiles() {
		return URI.create(BASE + "/version_files");
	}

	public static URI projectVersions(String projectId, List<String> loaders, String gameVersion) {
		return URI.create(BASE + "/project/" + encode(projectId) + "/version"
				+ "?loaders=" + encode(jsonArray(loaders))
				+ "&game_versions=" + encode(jsonArray(List.of(gameVersion))));
	}

	public static URI search(String query, int limit) {
		return URI.create(BASE + "/search"
				+ "?query=" + encode(query)
				+ "&limit=" + limit
				+ "&facets=" + encode("[[\"project_type:mod\"]]"));
	}

	private static String jsonArray(List<String> values) {
		return values.stream().map(value -> "\"" + value + "\"").collect(Collectors.joining(",", "[", "]"));
	}

	private static String encode(String value) {
		return URLEncoder.encode(value, StandardCharsets.UTF_8);
	}
}
