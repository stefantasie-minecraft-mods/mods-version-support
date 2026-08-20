package de.stefantasie.modsversionsupport.storage.codec;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import de.stefantasie.modsversionsupport.domain.profile.ProfileList;
import de.stefantasie.modsversionsupport.domain.profile.VersionProfile;
import java.util.ArrayList;
import java.util.List;

/** Root of the profile file. The schema version is written so later formats can migrate. */
public final class ProfileDocumentCodec {

	public static final int SCHEMA_VERSION = 1;

	private ProfileDocumentCodec() {
	}

	public static JsonObject write(ProfileList profiles) {
		JsonArray entries = new JsonArray();
		profiles.asList().forEach(profile -> entries.add(VersionProfileCodec.write(profile)));

		JsonObject json = new JsonObject();
		json.addProperty("schemaVersion", SCHEMA_VERSION);
		json.add("profiles", entries);
		return json;
	}

	public static ProfileList read(JsonObject json) {
		int schemaVersion = json.get("schemaVersion").getAsInt();
		if (schemaVersion != SCHEMA_VERSION) {
			throw new IllegalArgumentException("Unsupported profile schema version: " + schemaVersion);
		}

		List<VersionProfile> profiles = new ArrayList<>();
		json.getAsJsonArray("profiles")
				.forEach(element -> profiles.add(VersionProfileCodec.read(element.getAsJsonObject())));
		return ProfileList.of(profiles);
	}
}
