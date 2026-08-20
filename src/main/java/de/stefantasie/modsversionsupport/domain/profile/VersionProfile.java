package de.stefantasie.modsversionsupport.domain.profile;

import de.stefantasie.modsversionsupport.domain.report.SupportReport;
import de.stefantasie.modsversionsupport.domain.selection.ModSelection;
import java.util.Optional;

/** One entry of the overview: a target Minecraft version plus the mods to check against it. */
public record VersionProfile(
		ProfileId id,
		String displayName,
		String targetVersion,
		ModSelection selection,
		Optional<SupportReport> lastReport) {

	public static VersionProfile create(String displayName, String targetVersion, ModSelection selection) {
		return new VersionProfile(ProfileId.random(), displayName, targetVersion, selection, Optional.empty());
	}

	public VersionProfile withDisplayName(String name) {
		return new VersionProfile(id, name, targetVersion, selection, lastReport);
	}

	public VersionProfile withTargetVersion(String version) {
		return new VersionProfile(id, displayName, version, selection, lastReport);
	}

	public VersionProfile withSelection(ModSelection newSelection) {
		return new VersionProfile(id, displayName, targetVersion, newSelection, lastReport);
	}

	public VersionProfile withReport(SupportReport report) {
		return new VersionProfile(id, displayName, targetVersion, selection, Optional.of(report));
	}
}
