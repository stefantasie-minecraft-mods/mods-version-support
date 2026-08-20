package de.stefantasie.modsversionsupport.mojang.versions;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

/** Serves the version catalog, falling back to the stored copy when the manifest is unreachable. */
public final class VersionCatalogProvider {

	private final VersionManifestGateway gateway;
	private final VersionCatalogStore store;
	private final AtomicReference<VersionCatalog> current = new AtomicReference<>();

	public VersionCatalogProvider(VersionManifestGateway gateway, VersionCatalogStore store) {
		this.gateway = gateway;
		this.store = store;
	}

	public VersionCatalog get() {
		VersionCatalog cached = current.get();
		if (cached != null) {
			return cached;
		}
		VersionCatalog loaded = load();
		current.set(loaded);
		return loaded;
	}

	public VersionCatalog refresh() {
		current.set(null);
		return get();
	}

	private VersionCatalog load() {
		try {
			VersionCatalog fetched = gateway.fetch();
			store.write(fetched);
			return fetched;
		} catch (RuntimeException unreachable) {
			return fallback();
		}
	}

	private VersionCatalog fallback() {
		Optional<VersionCatalog> stored = store.read();
		return stored.orElseGet(VersionCatalog::empty);
	}
}
