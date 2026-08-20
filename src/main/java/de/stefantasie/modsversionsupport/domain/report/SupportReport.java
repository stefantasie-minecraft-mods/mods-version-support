package de.stefantasie.modsversionsupport.domain.report;

import de.stefantasie.modsversionsupport.domain.mod.ModKey;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/** Result of one profile run. Every selected mod counts towards the percentage. */
public record SupportReport(List<ModSupport> results, Instant finishedAt) {

	public SupportReport {
		results = List.copyOf(results);
	}

	public static SupportReport of(List<ModSupport> results, Instant finishedAt) {
		return new SupportReport(results, finishedAt);
	}

	public int total() {
		return results.size();
	}

	public int supportedCount() {
		return (int) results.stream().filter(result -> result.state().countsAsSupported()).count();
	}

	public double supportedRatio() {
		return total() == 0 ? 0 : (double) supportedCount() / total();
	}

	public int percent() {
		return (int) Math.round(supportedRatio() * 100);
	}

	public Optional<ModSupport> forMod(ModKey mod) {
		return results.stream().filter(result -> result.mod().equals(mod)).findFirst();
	}

	public Map<ModKey, ModSupport> byMod() {
		Map<ModKey, ModSupport> byMod = new LinkedHashMap<>();
		results.forEach(result -> byMod.put(result.mod(), result));
		return Map.copyOf(byMod);
	}
}
