package de.stefantasie.modsversionsupport.domain.report;

import de.stefantasie.modsversionsupport.domain.mod.ModKey;
import java.util.Optional;

/** What Modrinth answered for one mod and one target Minecraft version. */
public record ModSupport(
		ModKey mod,
		SupportState state,
		Optional<String> versionNumber,
		Optional<ReleaseChannel> channel,
		boolean quiltOnly) {

	public static ModSupport pending(ModKey mod) {
		return new ModSupport(mod, SupportState.PENDING, Optional.empty(), Optional.empty(), false);
	}

	public static ModSupport unsupported(ModKey mod) {
		return new ModSupport(mod, SupportState.UNSUPPORTED, Optional.empty(), Optional.empty(), false);
	}

	public static ModSupport unknownProject(ModKey mod) {
		return new ModSupport(mod, SupportState.NOT_ON_MODRINTH, Optional.empty(), Optional.empty(), false);
	}

	public static ModSupport failed(ModKey mod) {
		return new ModSupport(mod, SupportState.FAILED, Optional.empty(), Optional.empty(), false);
	}

	public static ModSupport supported(ModKey mod, String versionNumber, ReleaseChannel channel, boolean quiltOnly) {
		SupportState state = channel.isPrerelease() ? SupportState.SUPPORTED_PRERELEASE : SupportState.SUPPORTED;
		return new ModSupport(mod, state, Optional.of(versionNumber), Optional.of(channel), quiltOnly);
	}
}
