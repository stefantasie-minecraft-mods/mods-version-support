package de.stefantasie.modsversionsupport.mojang.versions;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/** The versions offered in the picker, newest first. */
public record VersionCatalog(List<GameVersion> versions) {

	public VersionCatalog {
		versions = versions.stream()
				.sorted(Comparator.comparing(GameVersion::releasedAt).reversed())
				.toList();
	}

	public static VersionCatalog empty() {
		return new VersionCatalog(List.of());
	}

	public List<GameVersion> releases() {
		return versions.stream().filter(version -> version.type().isRelease()).toList();
	}

	public List<GameVersion> visible(boolean includeSnapshots) {
		return includeSnapshots ? versions : releases();
	}

	public List<GameVersion> matching(String typed, boolean includeSnapshots) {
		String needle = typed.trim().toLowerCase(Locale.ROOT);
		List<GameVersion> candidates = visible(includeSnapshots);
		if (needle.isEmpty()) {
			return candidates;
		}
		List<GameVersion> startingWith = candidates.stream()
				.filter(version -> version.id().toLowerCase(Locale.ROOT).startsWith(needle))
				.toList();
		List<GameVersion> containing = candidates.stream()
				.filter(version -> !version.id().toLowerCase(Locale.ROOT).startsWith(needle))
				.filter(version -> version.id().toLowerCase(Locale.ROOT).contains(needle))
				.toList();
		return List.copyOf(concat(startingWith, containing));
	}

	public boolean contains(String versionId) {
		return versions.stream().anyMatch(version -> version.id().equals(versionId));
	}

	private static List<GameVersion> concat(List<GameVersion> first, List<GameVersion> second) {
		List<GameVersion> joined = new ArrayList<>(first);
		joined.addAll(second);
		return joined;
	}
}
