package de.stefantasie.modsversionsupport.domain.mod;

import java.util.Objects;

/** Stable identity of a tracked mod, used as a map key and in the profile file. */
public record ModKey(ModSource source, String value) {

	private static final String SEPARATOR = ":";

	public ModKey {
		Objects.requireNonNull(source);
		if (value == null || value.isBlank()) {
			throw new IllegalArgumentException("Mod key needs a value");
		}
	}

	public static ModKey ofInstalled(String modId) {
		return new ModKey(ModSource.INSTALLED, modId);
	}

	public static ModKey ofModrinthProject(String projectId) {
		return new ModKey(ModSource.MODRINTH, projectId);
	}

	public static ModKey parse(String stored) {
		int separator = stored.indexOf(SEPARATOR);
		if (separator < 0) {
			throw new IllegalArgumentException("Malformed mod key: " + stored);
		}
		ModSource source = ModSource.valueOf(stored.substring(0, separator).toUpperCase());
		return new ModKey(source, stored.substring(separator + 1));
	}

	public String stored() {
		return source.name().toLowerCase() + SEPARATOR + value;
	}
}
