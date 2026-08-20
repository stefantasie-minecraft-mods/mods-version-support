package de.stefantasie.modsversionsupport.ui.screen.editor;

import de.stefantasie.modsversionsupport.domain.mod.ModrinthMod;
import de.stefantasie.modsversionsupport.domain.mod.TrackedMod;
import de.stefantasie.modsversionsupport.modrinth.search.ModSearchGateway;
import de.stefantasie.modsversionsupport.modrinth.search.SearchHit;
import de.stefantasie.modsversionsupport.ui.widget.autocomplete.AsyncSuggestionSource;
import de.stefantasie.modsversionsupport.ui.widget.autocomplete.Suggestion;
import de.stefantasie.modsversionsupport.ui.widget.autocomplete.SuggestionSource;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.network.chat.Component;

/** Modrinth search results as suggestions, remembering which project each one stands for. */
public final class ModSuggestions implements SuggestionSource {

	private final Map<String, SearchHit> hitsByProjectId = new ConcurrentHashMap<>();
	private final AsyncSuggestionSource source;

	public ModSuggestions(ModSearchGateway search) {
		this.source = new AsyncSuggestionSource(query -> toSuggestions(search.search(query)));
	}

	@Override
	public List<Suggestion> suggest(String typed) {
		return typed.isBlank() ? List.of() : source.suggest(typed);
	}

	public Optional<TrackedMod> modFor(Suggestion suggestion) {
		return Optional.ofNullable(hitsByProjectId.get(suggestion.value()))
				.map(hit -> new ModrinthMod(hit.projectId(), hit.title(), hit.iconUrl()));
	}

	private List<Suggestion> toSuggestions(List<SearchHit> hits) {
		hits.forEach(hit -> hitsByProjectId.put(hit.projectId(), hit));
		return hits.stream()
				.map(hit -> Suggestion.of(hit.projectId(), Component.literal(hit.title()), Component.literal(hit.slug())))
				.toList();
	}
}
