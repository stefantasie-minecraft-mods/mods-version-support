package de.stefantasie.modsversionsupport.storage.codec;

import com.google.gson.JsonObject;
import de.stefantasie.modsversionsupport.domain.mod.InstalledMod;
import de.stefantasie.modsversionsupport.domain.mod.ModrinthMod;
import de.stefantasie.modsversionsupport.domain.mod.TrackedMod;

public final class TrackedModCodec {

	private static final String SOURCE = "source";
	private static final String INSTALLED = "installed";
	private static final String MODRINTH = "modrinth";

	private TrackedModCodec() {
	}

	public static JsonObject write(TrackedMod mod) {
		JsonObject json = new JsonObject();
		switch (mod) {
			case InstalledMod installed -> {
				json.addProperty(SOURCE, INSTALLED);
				json.addProperty("modId", installed.modId());
				json.addProperty("displayName", installed.displayName());
				json.addProperty("jarFileName", installed.jarFileName());
				installed.sha1().ifPresent(sha1 -> json.addProperty("sha1", sha1));
			}
			case ModrinthMod modrinth -> {
				json.addProperty(SOURCE, MODRINTH);
				json.addProperty("projectId", modrinth.projectId());
				json.addProperty("displayName", modrinth.displayName());
				modrinth.iconUrl().ifPresent(url -> json.addProperty("iconUrl", url));
			}
		}
		return json;
	}

	public static TrackedMod read(JsonObject json) {
		String source = json.get(SOURCE).getAsString();
		if (INSTALLED.equals(source)) {
			return new InstalledMod(
					json.get("modId").getAsString(),
					json.get("displayName").getAsString(),
					json.get("jarFileName").getAsString(),
					JsonValues.optionalString(json, "sha1"));
		}
		if (MODRINTH.equals(source)) {
			return new ModrinthMod(
					json.get("projectId").getAsString(),
					json.get("displayName").getAsString(),
					JsonValues.optionalString(json, "iconUrl"));
		}
		throw new IllegalArgumentException("Unknown mod source: " + source);
	}
}
