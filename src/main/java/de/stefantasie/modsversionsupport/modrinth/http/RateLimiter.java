package de.stefantasie.modsversionsupport.modrinth.http;

import java.time.Duration;
import java.util.ArrayDeque;
import java.util.Deque;

/** Keeps request volume below what Modrinth allows per minute. */
public final class RateLimiter {

	private static final Duration WINDOW = Duration.ofMinutes(1);

	private final int permitsPerWindow;
	private final Sleeper sleeper;
	private final Deque<Long> issuedAt = new ArrayDeque<>();

	public RateLimiter(int permitsPerWindow, Sleeper sleeper) {
		this.permitsPerWindow = permitsPerWindow;
		this.sleeper = sleeper;
	}

	public synchronized void acquire() {
		long now = System.nanoTime();
		forgetOlderThanWindow(now);
		if (issuedAt.size() >= permitsPerWindow) {
			long oldest = issuedAt.peekFirst();
			Duration wait = WINDOW.minusNanos(now - oldest);
			if (!wait.isNegative() && !wait.isZero()) {
				sleeper.sleep(wait);
			}
			forgetOlderThanWindow(System.nanoTime());
		}
		issuedAt.addLast(System.nanoTime());
	}

	private void forgetOlderThanWindow(long now) {
		while (!issuedAt.isEmpty() && now - issuedAt.peekFirst() > WINDOW.toNanos()) {
			issuedAt.removeFirst();
		}
	}
}
