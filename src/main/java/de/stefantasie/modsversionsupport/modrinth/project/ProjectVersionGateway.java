package de.stefantasie.modsversionsupport.modrinth.project;

import de.stefantasie.modsversionsupport.http.JsonHttpClient;
import de.stefantasie.modsversionsupport.modrinth.ModrinthEndpoints;
import java.util.List;

/** Asks Modrinth which versions of a project exist for one Minecraft version. */
public final class ProjectVersionGateway {

	private final JsonHttpClient http;
	private final List<String> loaders;

	public ProjectVersionGateway(JsonHttpClient http, List<String> loaders) {
		this.http = http;
		this.loaders = List.copyOf(loaders);
	}

	public List<ModrinthVersion> versionsFor(String projectId, String gameVersion) {
		return ProjectVersionParser.parse(http.get(ModrinthEndpoints.projectVersions(projectId, loaders, gameVersion)));
	}
}
