package de.stefantasie.modsversionsupport.modrinth.cache;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/** Remembers answers for a while so profiles sharing a target version cost one request. */
public final class TimedCache<V> {

	private record Entry<V>(V value, Instant storedAt) {
	}

	private final Map<String, Entry<V>> entries = new ConcurrentHashMap<>();
	private final Duration lifetime;
	private final Supplier<Instant> clock;

	public TimedCache(Duration lifetime, Supplier<Instant> clock) {
		this.lifetime = lifetime;
		this.clock = clock;
	}

	public static <V> TimedCache<V> lasting(Duration lifetime) {
		return new TimedCache<>(lifetime, Instant::now);
	}

	public Optional<V> get(String key) {
		Entry<V> entry = entries.get(key);
		if (entry == null) {
			return Optional.empty();
		}
		if (Duration.between(entry.storedAt(), clock.get()).compareTo(lifetime) > 0) {
			entries.remove(key);
			return Optional.empty();
		}
		return Optional.of(entry.value());
	}

	public void put(String key, V value) {
		entries.put(key, new Entry<>(value, clock.get()));
	}
}
