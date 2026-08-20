package de.stefantasie.modsversionsupport.http;

/** Modrinth requires a User-Agent that identifies the caller. */
public final class UserAgent {

	private static final String PROJECT = "stefantasie/mods-version-support";

	private UserAgent() {
	}

	public static String of(String modVersion, String contact) {
		return contact.isBlank()
				? PROJECT + "/" + modVersion
				: PROJECT + "/" + modVersion + " (" + contact + ")";
	}
}
