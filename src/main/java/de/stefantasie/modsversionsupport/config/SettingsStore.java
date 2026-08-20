package de.stefantasie.modsversionsupport.config;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public final class SettingsStore {

	private final Path file;

	public SettingsStore(Path file) {
		this.file = file;
	}

	public Settings load() {
		if (!Files.exists(file)) {
			return Settings.defaults();
		}
		try {
			JsonObject json = JsonParser.parseString(Files.readString(file, StandardCharsets.UTF_8)).getAsJsonObject();
			Settings defaults = Settings.defaults();
			return new Settings(
					read(json, "includeSnapshots", defaults.includeSnapshots()),
					read(json, "parallelProfiles", defaults.parallelProfiles()),
					read(json, "cacheLifetimeMinutes", defaults.cacheLifetimeMinutes()),
					json.has("contact") ? json.get("contact").getAsString() : defaults.contact());
		} catch (IOException | RuntimeException unreadable) {
			return Settings.defaults();
		}
	}

	public void save(Settings settings) {
		JsonObject json = new JsonObject();
		json.addProperty("includeSnapshots", settings.includeSnapshots());
		json.addProperty("parallelProfiles", settings.parallelProfiles());
		json.addProperty("cacheLifetimeMinutes", settings.cacheLifetimeMinutes());
		json.addProperty("contact", settings.contact());
		try {
			Files.createDirectories(file.getParent());
			Files.writeString(file, new GsonBuilder().setPrettyPrinting().create().toJson(json), StandardCharsets.UTF_8);
		} catch (IOException ignored) {
			// Settings then fall back to their defaults on the next start.
		}
	}

	private static boolean read(JsonObject json, String field, boolean fallback) {
		return json.has(field) ? json.get(field).getAsBoolean() : fallback;
	}

	private static int read(JsonObject json, String field, int fallback) {
		return json.has(field) ? json.get(field).getAsInt() : fallback;
	}
}
