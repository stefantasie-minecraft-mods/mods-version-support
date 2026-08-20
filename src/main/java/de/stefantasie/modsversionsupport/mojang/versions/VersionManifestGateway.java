package de.stefantasie.modsversionsupport.mojang.versions;

import de.stefantasie.modsversionsupport.http.JsonHttpClient;
import java.net.URI;

/** Reads the Minecraft version list from the same manifest a launcher uses. */
public final class VersionManifestGateway {

	private static final URI MANIFEST = URI.create("https://piston-meta.mojang.com/mc/game/version_manifest_v2.json");

	private final JsonHttpClient http;

	public VersionManifestGateway(JsonHttpClient http) {
		this.http = http;
	}

	public VersionCatalog fetch() {
		return new VersionCatalog(VersionManifestParser.parse(http.get(MANIFEST)));
	}
}
