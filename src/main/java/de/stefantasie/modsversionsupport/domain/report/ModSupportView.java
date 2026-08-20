package de.stefantasie.modsversionsupport.domain.report;

import de.stefantasie.modsversionsupport.domain.mod.ModKey;
import de.stefantasie.modsversionsupport.domain.mod.TrackedMod;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/** A mod together with what the last check found for it. */
public record ModSupportView(TrackedMod mod, ModSupport support) {

	public static List<ModSupportView> of(List<TrackedMod> mods, Optional<SupportReport> report) {
		Map<ModKey, ModSupport> byMod = report
				.map(SupportReport::byMod)
				.orElse(Map.of());
		return mods.stream()
				.map(mod -> new ModSupportView(mod, byMod.getOrDefault(mod.key(), ModSupport.pending(mod.key()))))
				.toList();
	}

	public SupportState state() {
		return support.state();
	}
}
