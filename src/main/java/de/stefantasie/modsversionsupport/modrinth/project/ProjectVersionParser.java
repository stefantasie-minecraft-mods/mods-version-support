package de.stefantasie.modsversionsupport.modrinth.project;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.stefantasie.modsversionsupport.domain.report.ReleaseChannel;
import java.util.ArrayList;
import java.util.List;

public final class ProjectVersionParser {

	private ProjectVersionParser() {
	}

	public static List<ModrinthVersion> parse(JsonElement answer) {
		List<ModrinthVersion> versions = new ArrayList<>();
		for (JsonElement element : answer.getAsJsonArray()) {
			JsonObject version = element.getAsJsonObject();
			List<String> loaders = new ArrayList<>();
			version.getAsJsonArray("loaders").forEach(loader -> loaders.add(loader.getAsString()));
			versions.add(new ModrinthVersion(
					version.get("version_number").getAsString(),
					ReleaseChannel.fromModrinthVersionType(version.get("version_type").getAsString()),
					loaders));
		}
		return List.copyOf(versions);
	}
}
