package de.stefantasie.modsversionsupport.storage.codec;

import com.google.gson.JsonObject;
import java.util.Optional;

final class JsonValues {

	private JsonValues() {
	}

	static Optional<String> optionalString(JsonObject json, String field) {
		return json.has(field) && !json.get(field).isJsonNull()
				? Optional.of(json.get(field).getAsString())
				: Optional.empty();
	}
}
