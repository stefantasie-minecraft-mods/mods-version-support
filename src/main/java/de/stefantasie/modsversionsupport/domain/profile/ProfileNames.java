package de.stefantasie.modsversionsupport.domain.profile;

import java.util.Collection;

/** Default naming for new profiles: the version itself, counted up while the name is taken. */
public final class ProfileNames {

	private ProfileNames() {
	}

	public static String defaultNameFor(String targetVersion, Collection<String> namesInUse) {
		if (!namesInUse.contains(targetVersion)) {
			return targetVersion;
		}
		int counter = 2;
		while (namesInUse.contains(targetVersion + " (" + counter + ")")) {
			counter++;
		}
		return targetVersion + " (" + counter + ")";
	}
}
