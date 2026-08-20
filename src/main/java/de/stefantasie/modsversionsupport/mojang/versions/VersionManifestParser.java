package de.stefantasie.modsversionsupport.mojang.versions;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public final class VersionManifestParser {

	private VersionManifestParser() {
	}

	public static List<GameVersion> parse(JsonElement manifest) {
		List<GameVersion> versions = new ArrayList<>();
		for (JsonElement element : manifest.getAsJsonObject().getAsJsonArray("versions")) {
			JsonObject entry = element.getAsJsonObject();
			versions.add(new GameVersion(
					entry.get("id").getAsString(),
					ReleaseType.fromManifest(entry.get("type").getAsString()),
					Instant.parse(entry.get("releaseTime").getAsString())));
		}
		return List.copyOf(versions);
	}
}
