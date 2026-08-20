package de.stefantasie.modsversionsupport.modrinth.project;

import com.google.gson.JsonElement;
import de.stefantasie.modsversionsupport.http.JsonHttpClient;
import de.stefantasie.modsversionsupport.modrinth.ModrinthEndpoints;
import java.util.ArrayList;
import java.util.List;

/** Asks which Minecraft versions a project supports at all, which is a far smaller answer than its version list. */
public final class ProjectGateway {

	private final JsonHttpClient http;

	public ProjectGateway(JsonHttpClient http) {
		this.http = http;
	}

	public List<String> supportedGameVersions(String projectId) {
		JsonElement project = http.get(ModrinthEndpoints.project(projectId));
		List<String> versions = new ArrayList<>();
		project.getAsJsonObject().getAsJsonArray("game_versions").forEach(version -> versions.add(version.getAsString()));
		return List.copyOf(versions);
	}
}
