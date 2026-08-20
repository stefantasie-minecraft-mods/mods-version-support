package de.stefantasie.modsversionsupport.mojang.versions;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/** Orders Minecraft versions by release date, newest first, so "at most this version" has a meaning. */
public final class VersionRanking {

	private final Map<String, Integer> rankById;

	private VersionRanking(Map<String, Integer> rankById) {
		this.rankById = Map.copyOf(rankById);
	}

	public static VersionRanking of(VersionCatalog catalog) {
		Map<String, Integer> ranks = new HashMap<>();
		for (int rank = 0; rank < catalog.versions().size(); rank++) {
			ranks.put(catalog.versions().get(rank).id(), rank);
		}
		return new VersionRanking(ranks);
	}

	public boolean knows(String versionId) {
		return rankById.containsKey(versionId);
	}

	public Optional<String> newestAtMost(String target, Collection<String> candidates) {
		Integer targetRank = rankById.get(target);
		if (targetRank == null) {
			return Optional.empty();
		}
		return candidates.stream()
				.filter(rankById::containsKey)
				.filter(candidate -> rankById.get(candidate) >= targetRank)
				.min((first, second) -> Integer.compare(rankById.get(first), rankById.get(second)));
	}
}
