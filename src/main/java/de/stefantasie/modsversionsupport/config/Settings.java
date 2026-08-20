package de.stefantasie.modsversionsupport.config;

import java.time.Duration;

/** What the settings screen can change. */
public record Settings(boolean includeSnapshots, int parallelProfiles, int cacheLifetimeMinutes, String contact) {

	public static final int MIN_PARALLEL_PROFILES = 1;
	public static final int MAX_PARALLEL_PROFILES = 8;
	public static final int MIN_CACHE_MINUTES = 1;
	public static final int MAX_CACHE_MINUTES = 720;

	public static Settings defaults() {
		return new Settings(false, 4, 30, "");
	}

	public Settings {
		parallelProfiles = Math.clamp(parallelProfiles, MIN_PARALLEL_PROFILES, MAX_PARALLEL_PROFILES);
		cacheLifetimeMinutes = Math.clamp(cacheLifetimeMinutes, MIN_CACHE_MINUTES, MAX_CACHE_MINUTES);
		contact = contact == null ? "" : contact.trim();
	}

	public Duration cacheLifetime() {
		return Duration.ofMinutes(cacheLifetimeMinutes);
	}
}
