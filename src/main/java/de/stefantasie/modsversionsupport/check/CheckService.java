package de.stefantasie.modsversionsupport.check;

import de.stefantasie.modsversionsupport.domain.profile.ProfileId;
import de.stefantasie.modsversionsupport.domain.profile.VersionProfile;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/** Runs profile checks in the background, one job per profile. */
public final class CheckService implements AutoCloseable {

	private final ProfileChecker checker;
	private final ExecutorService executor;
	private final Map<ProfileId, RunningCheck> checks = new ConcurrentHashMap<>();

	public CheckService(ProfileChecker checker, int parallelProfiles) {
		this.checker = checker;
		this.executor = Executors.newFixedThreadPool(parallelProfiles, checkThreads());
	}

	public RunningCheck start(VersionProfile profile) {
		cancel(profile.id());
		RunningCheck check = new RunningCheck(profile.id());
		checks.put(profile.id(), check);
		executor.execute(() -> run(profile, check));
		return check;
	}

	public Optional<RunningCheck> of(ProfileId profile) {
		return Optional.ofNullable(checks.get(profile));
	}

	public void cancel(ProfileId profile) {
		Optional.ofNullable(checks.remove(profile)).ifPresent(RunningCheck::cancel);
	}

	public void cancelAll() {
		checks.keySet().forEach(this::cancel);
	}

	@Override
	public void close() {
		cancelAll();
		executor.shutdownNow();
	}

	private void run(VersionProfile profile, RunningCheck check) {
		try {
			check.succeed(checker.check(profile, check::publish, check::cancelledFlag));
		} catch (RuntimeException failure) {
			check.fail(failure);
		}
	}

	private static ThreadFactory checkThreads() {
		AtomicInteger counter = new AtomicInteger();
		return runnable -> {
			Thread thread = new Thread(runnable, "mods-version-support-check-" + counter.incrementAndGet());
			thread.setDaemon(true);
			return thread;
		};
	}
}
