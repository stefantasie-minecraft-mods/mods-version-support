package de.stefantasie.modsversionsupport.ui.widget.autocomplete;

import java.util.List;

@FunctionalInterface
public interface SuggestionSource {

	List<Suggestion> suggest(String typed);
}
