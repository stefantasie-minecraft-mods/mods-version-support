package de.stefantasie.modsversionsupport.modrinth.cache;

import de.stefantasie.modsversionsupport.modrinth.project.ModrinthVersion;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/** Remembers Modrinth answers so profiles sharing a target version cost one request. */
public final class VersionSupportCache {

	private record Entry(List<ModrinthVersion> versions, Instant storedAt) {
	}

	private final Map<String, Entry> entries = new ConcurrentHashMap<>();
	private final Duration lifetime;
	private final Supplier<Instant> clock;

	public VersionSupportCache(Duration lifetime, Supplier<Instant> clock) {
		this.lifetime = lifetime;
		this.clock = clock;
	}

	public static VersionSupportCache lasting(Duration lifetime) {
		return new VersionSupportCache(lifetime, Instant::now);
	}

	public Optional<List<ModrinthVersion>> get(String projectId, String gameVersion) {
		Entry entry = entries.get(keyOf(projectId, gameVersion));
		if (entry == null) {
			return Optional.empty();
		}
		if (Duration.between(entry.storedAt(), clock.get()).compareTo(lifetime) > 0) {
			entries.remove(keyOf(projectId, gameVersion));
			return Optional.empty();
		}
		return Optional.of(entry.versions());
	}

	public void put(String projectId, String gameVersion, List<ModrinthVersion> versions) {
		entries.put(keyOf(projectId, gameVersion), new Entry(versions, clock.get()));
	}

	public void clear() {
		entries.clear();
	}

	private static String keyOf(String projectId, String gameVersion) {
		return projectId + "@" + gameVersion;
	}
}
