package de.stefantasie.modsversionsupport.ui.widget.autocomplete;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

/** Keeps the last answer on screen while a slower one is on its way. */
public final class AsyncSuggestionSource implements SuggestionSource {

	private final Function<String, List<Suggestion>> lookup;
	private final Executor executor;
	private final AtomicReference<String> pendingQuery = new AtomicReference<>("");
	private final AtomicReference<List<Suggestion>> latest = new AtomicReference<>(List.of());

	public AsyncSuggestionSource(Function<String, List<Suggestion>> lookup) {
		this.lookup = lookup;
		this.executor = Executors.newSingleThreadExecutor(runnable -> {
			Thread thread = new Thread(runnable, "mods-version-support-suggestions");
			thread.setDaemon(true);
			return thread;
		});
	}

	@Override
	public List<Suggestion> suggest(String typed) {
		if (!typed.equals(pendingQuery.getAndSet(typed))) {
			CompletableFuture.supplyAsync(() -> lookup.apply(typed), executor)
					.thenAccept(found -> acceptWhenStillWanted(typed, found))
					.exceptionally(failure -> null);
		}
		return latest.get();
	}

	private void acceptWhenStillWanted(String query, List<Suggestion> found) {
		if (query.equals(pendingQuery.get())) {
			latest.set(found);
		}
	}
}
