package de.stefantasie.modsversionsupport.domain.mod;

import java.util.Optional;

/** A mod a profile checks, either found in the instance or added from Modrinth. */
public sealed interface TrackedMod permits InstalledMod, ModrinthMod {

	ModKey key();

	String displayName();

	Optional<String> fileName();

	Optional<String> modrinthProjectId();
}
