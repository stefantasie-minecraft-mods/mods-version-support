package de.stefantasie.modsversionsupport.mojang.versions;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** Keeps the last known version list so the picker also works without a connection. */
public final class VersionCatalogStore {

	private final Path file;

	public VersionCatalogStore(Path file) {
		this.file = file;
	}

	public Optional<VersionCatalog> read() {
		if (!Files.exists(file)) {
			return Optional.empty();
		}
		try {
			JsonObject json = JsonParser.parseString(Files.readString(file, StandardCharsets.UTF_8)).getAsJsonObject();
			List<GameVersion> versions = new ArrayList<>();
			for (var element : json.getAsJsonArray("versions")) {
				JsonObject entry = element.getAsJsonObject();
				versions.add(new GameVersion(
						entry.get("id").getAsString(),
						ReleaseType.valueOf(entry.get("type").getAsString()),
						Instant.parse(entry.get("releasedAt").getAsString())));
			}
			return Optional.of(new VersionCatalog(versions));
		} catch (IOException | RuntimeException ignored) {
			return Optional.empty();
		}
	}

	public void write(VersionCatalog catalog) {
		JsonArray versions = new JsonArray();
		for (GameVersion version : catalog.versions()) {
			JsonObject entry = new JsonObject();
			entry.addProperty("id", version.id());
			entry.addProperty("type", version.type().name());
			entry.addProperty("releasedAt", version.releasedAt().toString());
			versions.add(entry);
		}

		JsonObject json = new JsonObject();
		json.addProperty("writtenAt", Instant.now().toString());
		json.add("versions", versions);
		try {
			Files.createDirectories(file.getParent());
			Files.writeString(file, json.toString(), StandardCharsets.UTF_8);
		} catch (IOException ignored) {
			// A missing cache only costs a request on the next start.
		}
	}
}
