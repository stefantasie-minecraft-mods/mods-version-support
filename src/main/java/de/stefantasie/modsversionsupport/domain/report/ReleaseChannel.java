package de.stefantasie.modsversionsupport.domain.report;

import java.util.Locale;

public enum ReleaseChannel {
	RELEASE,
	BETA,
	ALPHA;

	public static ReleaseChannel fromModrinthVersionType(String versionType) {
		return switch (versionType.toLowerCase(Locale.ROOT)) {
			case "release" -> RELEASE;
			case "beta" -> BETA;
			default -> ALPHA;
		};
	}

	public boolean isPrerelease() {
		return this != RELEASE;
	}
}
