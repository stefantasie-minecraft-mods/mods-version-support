package de.stefantasie.modsversionsupport.ui.icon;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;

/** Reads the icon a mod ships inside its own jar. */
public final class JarIcons {

	private static final int PREFERRED_SIZE = 64;

	private JarIcons() {
	}

	public static Optional<byte[]> of(String modId) {
		return FabricLoader.getInstance().getModContainer(modId)
				.flatMap(JarIcons::iconPathOf)
				.flatMap(JarIcons::readAll);
	}

	private static Optional<Path> iconPathOf(ModContainer container) {
		return container.getMetadata().getIconPath(PREFERRED_SIZE).flatMap(container::findPath);
	}

	private static Optional<byte[]> readAll(Path path) {
		try {
			return Optional.of(Files.readAllBytes(path));
		} catch (IOException unreadable) {
			return Optional.empty();
		}
	}
}
