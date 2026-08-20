package de.stefantasie.modsversionsupport.storage;

import de.stefantasie.modsversionsupport.ModsVersionSupport;
import java.nio.file.Path;
import net.fabricmc.loader.api.FabricLoader;

/** Where this mod keeps its files inside the instance. */
public final class StorageLayout {

	private StorageLayout() {
	}

	public static Path directory() {
		return FabricLoader.getInstance().getConfigDir().resolve(ModsVersionSupport.MOD_ID);
	}

	public static Path profileFile() {
		return directory().resolve("profiles.json");
	}

	public static Path iconCache() {
		return directory().resolve("icons");
	}

	public static Path responseCache() {
		return directory().resolve("modrinth-cache.json");
	}
}
