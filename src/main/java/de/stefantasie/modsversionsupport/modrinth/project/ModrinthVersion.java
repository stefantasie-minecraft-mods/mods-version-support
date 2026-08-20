package de.stefantasie.modsversionsupport.modrinth.project;

import de.stefantasie.modsversionsupport.domain.report.ReleaseChannel;
import java.util.List;

public record ModrinthVersion(String versionNumber, ReleaseChannel channel, List<String> loaders) {

	public ModrinthVersion {
		loaders = List.copyOf(loaders);
	}

	public boolean supportsFabric() {
		return loaders.contains("fabric");
	}
}
