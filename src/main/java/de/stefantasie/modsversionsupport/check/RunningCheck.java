package de.stefantasie.modsversionsupport.check;

import de.stefantasie.modsversionsupport.domain.profile.ProfileId;
import de.stefantasie.modsversionsupport.domain.report.CheckProgress;
import de.stefantasie.modsversionsupport.domain.report.SupportReport;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/** Handle on one profile check while it runs. */
public final class RunningCheck {

	private final ProfileId profile;
	private final AtomicReference<CheckProgress> progress = new AtomicReference<>(CheckProgress.IDLE);
	private final AtomicBoolean cancelled = new AtomicBoolean();
	private final CompletableFuture<SupportReport> report = new CompletableFuture<>();

	RunningCheck(ProfileId profile) {
		this.profile = profile;
	}

	public ProfileId profile() {
		return profile;
	}

	public CheckProgress progress() {
		return progress.get();
	}

	public boolean isRunning() {
		return !report.isDone();
	}

	public void cancel() {
		cancelled.set(true);
	}

	public Optional<SupportReport> finishedReport() {
		return report.isCompletedExceptionally() || !report.isDone()
				? Optional.empty()
				: Optional.of(report.join());
	}

	public CompletableFuture<SupportReport> whenFinished() {
		return report;
	}

	void publish(CheckProgress current) {
		progress.set(current);
	}

	void succeed(SupportReport finished) {
		report.complete(finished);
	}

	void fail(Throwable cause) {
		report.completeExceptionally(cause);
	}

	boolean cancelledFlag() {
		return cancelled.get();
	}
}
