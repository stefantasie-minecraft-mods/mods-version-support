package de.stefantasie.modsversionsupport.check;

import de.stefantasie.modsversionsupport.domain.mod.TrackedMod;
import de.stefantasie.modsversionsupport.domain.profile.VersionProfile;
import de.stefantasie.modsversionsupport.domain.report.CheckProgress;
import de.stefantasie.modsversionsupport.domain.report.ModSupport;
import de.stefantasie.modsversionsupport.domain.report.SupportReport;
import de.stefantasie.modsversionsupport.modrinth.cache.TimedCache;
import de.stefantasie.modsversionsupport.modrinth.project.ModrinthVersion;
import de.stefantasie.modsversionsupport.modrinth.project.ProjectGateway;
import de.stefantasie.modsversionsupport.modrinth.project.ProjectVersionGateway;
import de.stefantasie.modsversionsupport.mojang.versions.VersionRanking;
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
	private final ProjectGateway projects;
	private final TimedCache<List<ModrinthVersion>> versionCache;
	private final TimedCache<List<String>> gameVersionCache;
	private final Supplier<VersionRanking> ranking;
	private final Supplier<Instant> clock;

	public ProfileChecker(
			ModProjectResolver resolver,
			ProjectVersionGateway versions,
			ProjectGateway projects,
			TimedCache<List<ModrinthVersion>> versionCache,
			TimedCache<List<String>> gameVersionCache,
			Supplier<VersionRanking> ranking,
			Supplier<Instant> clock) {
		this.resolver = resolver;
		this.versions = versions;
		this.projects = projects;
		this.versionCache = versionCache;
		this.gameVersionCache = gameVersionCache;
		this.ranking = ranking;
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
			ModSupport verdict = SupportEvaluator.evaluate(mod.key(), versionsFor(projectId.get(), targetVersion), targetVersion);
			return verdict.state().countsAsSupported()
					? verdict
					: ModSupport.unsupported(mod.key(), newestSupported(projectId.get(), targetVersion));
		} catch (RuntimeException failure) {
			return ModSupport.failed(mod.key());
		}
	}

	private List<ModrinthVersion> versionsFor(String projectId, String targetVersion) {
		String key = projectId + "@" + targetVersion;
		return versionCache.get(key).orElseGet(() -> {
			List<ModrinthVersion> fetched = versions.versionsFor(projectId, targetVersion);
			versionCache.put(key, fetched);
			return fetched;
		});
	}

	private Optional<String> newestSupported(String projectId, String targetVersion) {
		List<String> supported = gameVersionCache.get(projectId).orElseGet(() -> {
			List<String> fetched = projects.supportedGameVersions(projectId);
			gameVersionCache.put(projectId, fetched);
			return fetched;
		});
		return ranking.get().newestAtMost(targetVersion, supported);
	}

	private void stopWhenCancelled(BooleanSupplier cancelled) {
		if (cancelled.getAsBoolean()) {
			throw new CheckCancelledException();
		}
	}
}
