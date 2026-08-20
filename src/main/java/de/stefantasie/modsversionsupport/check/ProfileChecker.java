package de.stefantasie.modsversionsupport.check;

import de.stefantasie.modsversionsupport.domain.mod.TrackedMod;
import de.stefantasie.modsversionsupport.domain.profile.VersionProfile;
import de.stefantasie.modsversionsupport.domain.report.CheckProgress;
import de.stefantasie.modsversionsupport.domain.report.ModSupport;
import de.stefantasie.modsversionsupport.domain.report.SupportReport;
import de.stefantasie.modsversionsupport.modrinth.cache.VersionSupportCache;
import de.stefantasie.modsversionsupport.modrinth.project.ModrinthVersion;
import de.stefantasie.modsversionsupport.modrinth.project.ProjectVersionGateway;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Supplier;

/** Checks one profile, mod by mod, reporting progress as it goes. */
public final class ProfileChecker {

	private final ModProjectResolver resolver;
	private final ProjectVersionGateway versions;
	private final VersionSupportCache cache;
	private final Supplier<Instant> clock;

	public ProfileChecker(
			ModProjectResolver resolver,
			ProjectVersionGateway versions,
			VersionSupportCache cache,
			Supplier<Instant> clock) {
		this.resolver = resolver;
		this.versions = versions;
		this.cache = cache;
		this.clock = clock;
	}

	public SupportReport check(VersionProfile profile, Consumer<CheckProgress> onProgress, BooleanSupplier cancelled) {
		List<TrackedMod> mods = profile.selection().selectedMods();
		onProgress.accept(new CheckProgress(0, mods.size()));
		resolveProjects(mods, cancelled);

		List<ModSupport> results = new ArrayList<>();
		for (TrackedMod mod : mods) {
			stopWhenCancelled(cancelled);
			results.add(checkSingle(mod, profile.targetVersion()));
			onProgress.accept(new CheckProgress(results.size(), mods.size()));
		}
		return new SupportReport(results, clock.get());
	}

	private void resolveProjects(List<TrackedMod> mods, BooleanSupplier cancelled) {
		stopWhenCancelled(cancelled);
		try {
			resolver.resolveAll(mods);
		} catch (RuntimeException unreachable) {
			// Every mod then reports as failed, which the caller shows per row.
		}
	}

	private ModSupport checkSingle(TrackedMod mod, String targetVersion) {
		Optional<String> projectId = resolver.projectIdOf(mod);
		if (projectId.isEmpty()) {
			return ModSupport.unknownProject(mod.key());
		}
		try {
			return SupportEvaluator.evaluate(mod.key(), versionsFor(projectId.get(), targetVersion));
		} catch (RuntimeException failure) {
			return ModSupport.failed(mod.key());
		}
	}

	private List<ModrinthVersion> versionsFor(String projectId, String targetVersion) {
		Optional<List<ModrinthVersion>> cached = cache.get(projectId, targetVersion);
		if (cached.isPresent()) {
			return cached.get();
		}
		List<ModrinthVersion> fetched = versions.versionsFor(projectId, targetVersion);
		cache.put(projectId, targetVersion, fetched);
		return fetched;
	}

	private void stopWhenCancelled(BooleanSupplier cancelled) {
		if (cancelled.getAsBoolean()) {
			throw new CheckCancelledException();
		}
	}
}
