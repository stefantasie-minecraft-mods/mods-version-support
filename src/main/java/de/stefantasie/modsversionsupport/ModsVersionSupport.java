package de.stefantasie.modsversionsupport;

/** Identity of this mod, used for resources, logging and translation keys. */
public final class ModsVersionSupport {

	public static final String MOD_ID = "mods-version-support";

	private ModsVersionSupport() {
	}

	public static String translationKey(String suffix) {
		return MOD_ID + "." + suffix;
	}
}
