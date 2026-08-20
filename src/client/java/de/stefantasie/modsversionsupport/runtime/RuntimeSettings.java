package de.stefantasie.modsversionsupport.runtime;

import java.time.Duration;

/** Values the settings screen can change while the game runs. */
public record RuntimeSettings(boolean includeSnapshots, int parallelProfiles, Duration cacheLifetime, String contact) {

	public static RuntimeSettings defaults() {
		return new RuntimeSettings(false, 4, Duration.ofMinutes(30), "");
	}
}
