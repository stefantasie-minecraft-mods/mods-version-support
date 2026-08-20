package de.stefantasie.modsversionsupport.domain.mod;

import java.util.Optional;

/** A mod added through the Modrinth search, without a local file. */
public record ModrinthMod(String projectId, String displayName, Optional<String> iconUrl) implements TrackedMod {

	@Override
	public ModKey key() {
		return ModKey.ofModrinthProject(projectId);
	}

	@Override
	public Optional<String> fileName() {
		return Optional.empty();
	}

	@Override
	public Optional<String> modrinthProjectId() {
		return Optional.of(projectId);
	}
}
