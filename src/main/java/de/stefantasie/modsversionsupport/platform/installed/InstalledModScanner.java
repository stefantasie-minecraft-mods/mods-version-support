package de.stefantasie.modsversionsupport.platform.installed;

import de.stefantasie.modsversionsupport.domain.mod.InstalledMod;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.ModOrigin;

/** Lists the mods of the running instance that are worth checking against Modrinth. */
public final class InstalledModScanner {

	private static final List<String> PLATFORM_IDS = List.of("java", "minecraft", "fabricloader", "fabric-language-kotlin");

	private final Collection<ModContainer> containers;

	public InstalledModScanner(Collection<ModContainer> containers) {
		this.containers = containers;
	}

	public static InstalledModScanner ofRunningInstance() {
		return new InstalledModScanner(FabricLoader.getInstance().getAllMods());
	}

	public List<InstalledMod> scan() {
		return containers.stream()
				.filter(InstalledModScanner::isStandaloneJar)
				.filter(container -> !PLATFORM_IDS.contains(container.getMetadata().getId()))
				.map(InstalledModScanner::toInstalledMod)
				.flatMap(Optional::stream)
				.sorted(Comparator.comparing(InstalledMod::displayName, String.CASE_INSENSITIVE_ORDER))
				.toList();
	}

	private static boolean isStandaloneJar(ModContainer container) {
		return container.getContainingMod().isEmpty()
				&& container.getOrigin().getKind() == ModOrigin.Kind.PATH
				&& jarOf(container).isPresent();
	}

	private static Optional<InstalledMod> toInstalledMod(ModContainer container) {
		return jarOf(container).map(jar -> new InstalledMod(
				container.getMetadata().getId(),
				container.getMetadata().getName(),
				jar.getFileName().toString(),
				JarHash.sha1Of(jar)));
	}

	private static Optional<Path> jarOf(ModContainer container) {
		return container.getOrigin().getPaths().stream()
				.filter(path -> path.getFileName().toString().endsWith(".jar"))
				.findFirst();
	}
}
