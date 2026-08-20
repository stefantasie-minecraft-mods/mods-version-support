package de.stefantasie.modsversionsupport.modrinth.http;

import java.time.Duration;

@FunctionalInterface
public interface Sleeper {

	Sleeper REAL = duration -> {
		try {
			Thread.sleep(duration.toMillis());
		} catch (InterruptedException interrupted) {
			Thread.currentThread().interrupt();
		}
	};

	void sleep(Duration duration);
}
