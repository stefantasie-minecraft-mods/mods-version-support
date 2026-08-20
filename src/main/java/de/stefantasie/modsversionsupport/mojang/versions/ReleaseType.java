package de.stefantasie.modsversionsupport.mojang.versions;

import java.util.Locale;

public enum ReleaseType {
	RELEASE,
	SNAPSHOT,
	OLD_BETA,
	OLD_ALPHA;

	public static ReleaseType fromManifest(String type) {
		return switch (type.toLowerCase(Locale.ROOT)) {
			case "release" -> RELEASE;
			case "snapshot" -> SNAPSHOT;
			case "old_beta" -> OLD_BETA;
			default -> OLD_ALPHA;
		};
	}

	public boolean isRelease() {
		return this == RELEASE;
	}
}
