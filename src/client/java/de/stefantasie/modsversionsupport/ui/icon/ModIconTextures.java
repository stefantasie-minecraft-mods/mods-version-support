package de.stefantasie.modsversionsupport.ui.icon;

import com.mojang.blaze3d.platform.NativeImage;
import de.stefantasie.modsversionsupport.ModsVersionSupport;
import de.stefantasie.modsversionsupport.domain.mod.InstalledMod;
import de.stefantasie.modsversionsupport.domain.mod.ModKey;
import de.stefantasie.modsversionsupport.domain.mod.ModrinthMod;
import de.stefantasie.modsversionsupport.domain.mod.TrackedMod;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.Identifier;

/** Loads mod icons in the background and hands the finished textures to the screens. */
public final class ModIconTextures implements AutoCloseable {

	private final IconDownloader downloader;
	private final ExecutorService loader;
	private final Map<ModKey, Identifier> ready = new ConcurrentHashMap<>();
	private final Set<ModKey> started = ConcurrentHashMap.newKeySet();

	public ModIconTextures(IconDownloader downloader) {
		this.downloader = downloader;
		this.loader = Executors.newFixedThreadPool(3, runnable -> {
			Thread thread = new Thread(runnable, "mods-version-support-icons");
			thread.setDaemon(true);
			return thread;
		});
	}

	public Optional<Identifier> iconFor(TrackedMod mod) {
		Identifier texture = ready.get(mod.key());
		if (texture != null) {
			return Optional.of(texture);
		}
		if (started.add(mod.key())) {
			loader.execute(() -> load(mod));
		}
		return Optional.empty();
	}

	@Override
	public void close() {
		loader.shutdownNow();
		Minecraft client = Minecraft.getInstance();
		ready.values().forEach(identifier -> client.getTextureManager().release(identifier));
		ready.clear();
	}

	private void load(TrackedMod mod) {
		bytesOf(mod).flatMap(IconImage::decode).ifPresent(image -> upload(mod.key(), image));
	}

	private Optional<byte[]> bytesOf(TrackedMod mod) {
		return switch (mod) {
			case InstalledMod installed -> JarIcons.of(installed.modId());
			case ModrinthMod modrinth -> modrinth.iconUrl().flatMap(downloader::fetch);
		};
	}

	private void upload(ModKey key, NativeImage image) {
		Minecraft client = Minecraft.getInstance();
		client.execute(() -> {
			Identifier identifier = identifierFor(key);
			client.getTextureManager().register(identifier, new DynamicTexture(identifier::toString, image));
			ready.put(key, identifier);
		});
	}

	private static Identifier identifierFor(ModKey key) {
		String path = "icon/" + key.stored().toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9_.-]", "_");
		return Identifier.fromNamespaceAndPath(ModsVersionSupport.MOD_ID, path);
	}
}
