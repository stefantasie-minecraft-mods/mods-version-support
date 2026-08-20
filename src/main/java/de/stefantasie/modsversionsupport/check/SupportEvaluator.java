package de.stefantasie.modsversionsupport.check;

import de.stefantasie.modsversionsupport.domain.mod.ModKey;
import de.stefantasie.modsversionsupport.domain.report.ModSupport;
import de.stefantasie.modsversionsupport.modrinth.project.ModrinthVersion;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/** Turns the versions Modrinth lists for a target version into a single verdict. */
public final class SupportEvaluator {

	private SupportEvaluator() {
	}

	public static ModSupport evaluate(ModKey mod, List<ModrinthVersion> versions, String targetVersion) {
		Optional<ModrinthVersion> forFabric = best(versions.stream().filter(ModrinthVersion::supportsFabric).toList());
		if (forFabric.isPresent()) {
			return supported(mod, forFabric.get(), targetVersion, false);
		}
		return best(versions)
				.map(quiltOnly -> supported(mod, quiltOnly, targetVersion, true))
				.orElseGet(() -> ModSupport.unsupported(mod, Optional.empty()));
	}

	private static Optional<ModrinthVersion> best(List<ModrinthVersion> versions) {
		return versions.stream().min(Comparator.comparing(ModrinthVersion::channel));
	}

	private static ModSupport supported(ModKey mod, ModrinthVersion version, String targetVersion, boolean quiltOnly) {
		return ModSupport.supported(mod, version.versionNumber(), version.channel(), targetVersion, quiltOnly);
	}
}
