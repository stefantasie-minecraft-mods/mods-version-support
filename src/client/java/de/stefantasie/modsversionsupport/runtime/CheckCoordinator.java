package de.stefantasie.modsversionsupport.runtime;

import de.stefantasie.modsversionsupport.check.CheckService;
import de.stefantasie.modsversionsupport.check.RunningCheck;
import de.stefantasie.modsversionsupport.domain.profile.ProfileId;
import de.stefantasie.modsversionsupport.domain.profile.VersionProfile;
import de.stefantasie.modsversionsupport.domain.report.SupportReport;
import java.util.Optional;

/** Starts checks and hands their results back to the stored profiles. */
public final class CheckCoordinator {

	private final CheckService checks;
	private final ProfileRepository repository;

	public CheckCoordinator(CheckService checks, ProfileRepository repository) {
		this.checks = checks;
		this.repository = repository;
	}

	public void checkAll() {
		repository.all().forEach(this::check);
	}

	public void check(VersionProfile profile) {
		RunningCheck check = checks.start(profile);
		check.whenFinished().thenAccept(report -> storeReport(profile.id(), report));
	}

	public void forget(ProfileId profile) {
		checks.cancel(profile);
	}

	public CheckStatus statusOf(VersionProfile profile) {
		Optional<RunningCheck> check = checks.of(profile.id());
		if (check.isEmpty()) {
			return CheckStatus.idle(profile.lastReport());
		}
		RunningCheck running = check.get();
		Optional<SupportReport> report = running.finishedReport().or(profile::lastReport);
		return new CheckStatus(running.isRunning(), running.progress(), report);
	}

	private void storeReport(ProfileId id, SupportReport report) {
		repository.find(id).ifPresent(stored -> repository.replace(stored.withReport(report)));
	}
}
