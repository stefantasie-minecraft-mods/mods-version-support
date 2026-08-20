package de.stefantasie.modsversionsupport.domain.mod;

import java.util.Optional;

/** A mod found in the running instance, identified towards Modrinth by its file hash. */
public record InstalledMod(String modId, String displayName, String fileName, Optional<String> sha1)
		implements TrackedMod {

	@Override
	public ModKey key() {
		return ModKey.ofInstalled(modId);
	}

	@Override
	public Optional<String> fileName() {
		return Optional.of(fileName);
	}

	@Override
	public Optional<String> modrinthProjectId() {
		return Optional.empty();
	}
}
