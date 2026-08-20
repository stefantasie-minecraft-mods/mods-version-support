package de.stefantasie.modsversionsupport.check;

import de.stefantasie.modsversionsupport.domain.mod.InstalledMod;
import de.stefantasie.modsversionsupport.domain.mod.ModrinthMod;
import de.stefantasie.modsversionsupport.domain.mod.TrackedMod;
import de.stefantasie.modsversionsupport.modrinth.hash.HashLookupGateway;
import de.stefantasie.modsversionsupport.modrinth.hash.HashMatch;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/** Finds the Modrinth project behind each tracked mod, asking for all installed jars at once. */
public final class ModProjectResolver {

	private final HashLookupGateway hashLookup;
	private final Map<String, String> projectIdBySha1 = new ConcurrentHashMap<>();

	public ModProjectResolver(HashLookupGateway hashLookup) {
		this.hashLookup = hashLookup;
	}

	public void resolveAll(List<TrackedMod> mods) {
		List<String> unknownHashes = mods.stream()
				.filter(InstalledMod.class::isInstance)
				.map(InstalledMod.class::cast)
				.map(InstalledMod::sha1)
				.flatMap(Optional::stream)
				.filter(sha1 -> !projectIdBySha1.containsKey(sha1))
				.distinct()
				.toList();
		if (unknownHashes.isEmpty()) {
			return;
		}
		Map<String, HashMatch> matches = hashLookup.lookup(unknownHashes);
		matches.forEach((sha1, match) -> projectIdBySha1.put(sha1, match.projectId()));
	}

	public Optional<String> projectIdOf(TrackedMod mod) {
		return switch (mod) {
			case ModrinthMod modrinth -> Optional.of(modrinth.projectId());
			case InstalledMod installed -> installed.sha1().map(projectIdBySha1::get);
		};
	}
}
