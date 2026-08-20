package de.stefantasie.modsversionsupport.runtime;

import de.stefantasie.modsversionsupport.ModsVersionSupport;
import de.stefantasie.modsversionsupport.check.CheckService;
import de.stefantasie.modsversionsupport.check.ModProjectResolver;
import de.stefantasie.modsversionsupport.check.ProfileChecker;
import de.stefantasie.modsversionsupport.config.Settings;
import de.stefantasie.modsversionsupport.config.SettingsStore;
import de.stefantasie.modsversionsupport.domain.mod.InstalledMod;
import de.stefantasie.modsversionsupport.http.JavaNetJsonHttpClient;
import de.stefantasie.modsversionsupport.http.JsonHttpClient;
import de.stefantasie.modsversionsupport.http.UserAgent;
import de.stefantasie.modsversionsupport.modrinth.cache.TimedCache;
import de.stefantasie.modsversionsupport.modrinth.hash.HashLookupGateway;
import de.stefantasie.modsversionsupport.modrinth.http.RateLimiter;
import de.stefantasie.modsversionsupport.modrinth.http.Sleeper;
import de.stefantasie.modsversionsupport.modrinth.http.ThrottledModrinthHttp;
import de.stefantasie.modsversionsupport.modrinth.project.ProjectGateway;
import de.stefantasie.modsversionsupport.modrinth.project.ProjectVersionGateway;
import de.stefantasie.modsversionsupport.modrinth.search.ModSearchGateway;
import de.stefantasie.modsversionsupport.mojang.versions.VersionCatalogProvider;
import de.stefantasie.modsversionsupport.mojang.versions.VersionCatalogStore;
import de.stefantasie.modsversionsupport.mojang.versions.VersionManifestGateway;
import de.stefantasie.modsversionsupport.mojang.versions.VersionRanking;
import de.stefantasie.modsversionsupport.platform.installed.InstalledModScanner;
import de.stefantasie.modsversionsupport.storage.ProfileStore;
import de.stefantasie.modsversionsupport.storage.StorageLayout;
import de.stefantasie.modsversionsupport.ui.icon.IconCache;
import de.stefantasie.modsversionsupport.ui.icon.IconDownloader;
import de.stefantasie.modsversionsupport.ui.icon.ModIconTextures;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import net.fabricmc.loader.api.FabricLoader;

/** Wires the parts of the mod together for one game session. */
public final class ClientRuntime implements AutoCloseable {

	private static final int REQUESTS_PER_MINUTE = 250;
	private static final List<String> LOADERS = List.of("fabric", "quilt");

	private final ProfileRepository profiles;
	private final CheckService checks;
	private final CheckCoordinator coordinator;
	private final VersionCatalogProvider versions;
	private final ModSearchGateway search;
	private final SettingsStore settingsStore;
	private final AtomicReference<Settings> settings;
	private final List<InstalledMod> installedMods;
	private final ModIconTextures icons;

	private ClientRuntime(SettingsStore settingsStore) {
		this.settingsStore = settingsStore;
		Settings initialSettings = settingsStore.load();
		this.settings = new AtomicReference<>(initialSettings);
		JsonHttpClient plainHttp = new JavaNetJsonHttpClient(userAgent(initialSettings.contact()));
		JsonHttpClient modrinthHttp = new ThrottledModrinthHttp(
				plainHttp, new RateLimiter(REQUESTS_PER_MINUTE, Sleeper.REAL), Sleeper.REAL);

		this.profiles = new ProfileRepository(new ProfileStore(StorageLayout.profileFile()));
		this.versions = new VersionCatalogProvider(
				new VersionManifestGateway(plainHttp), new VersionCatalogStore(StorageLayout.versionCache()));
		this.search = new ModSearchGateway(modrinthHttp);
		this.installedMods = InstalledModScanner.ofRunningInstance().scan();

		ProfileChecker checker = new ProfileChecker(
				new ModProjectResolver(new HashLookupGateway(modrinthHttp)),
				new ProjectVersionGateway(modrinthHttp, LOADERS),
				new ProjectGateway(modrinthHttp),
				TimedCache.lasting(initialSettings.cacheLifetime()),
				TimedCache.lasting(initialSettings.cacheLifetime()),
				() -> VersionRanking.of(versions.get()),
				Instant::now);
		this.icons = new ModIconTextures(new IconDownloader(new IconCache(StorageLayout.iconCache()), userAgent(initialSettings.contact())));
		this.checks = new CheckService(checker, initialSettings.parallelProfiles());
		this.coordinator = new CheckCoordinator(checks, profiles);
	}

	public static ClientRuntime start() {
		return new ClientRuntime(new SettingsStore(StorageLayout.settingsFile()));
	}

	public ProfileRepository profiles() {
		return profiles;
	}

	public CheckCoordinator checks() {
		return coordinator;
	}

	public VersionCatalogProvider versions() {
		return versions;
	}

	public ModSearchGateway search() {
		return search;
	}

	public List<InstalledMod> installedMods() {
		return installedMods;
	}

	public ModIconTextures icons() {
		return icons;
	}

	public Settings settings() {
		return settings.get();
	}

	/** Thread count and cache lifetime are fixed when the runtime starts, so they apply after a restart. */
	public void applySettings(Settings updated) {
		settings.set(updated);
		settingsStore.save(updated);
	}

	@Override
	public void close() {
		checks.close();
		icons.close();
	}

	private static String userAgent(String contact) {
		String version = FabricLoader.getInstance().getModContainer(ModsVersionSupport.MOD_ID)
				.map(container -> container.getMetadata().getVersion().getFriendlyString())
				.orElse("dev");
		return UserAgent.of(version, contact);
	}
}
