package de.stefantasie.modsversionsupport.ui.widget.autocomplete;

import java.util.Optional;
import net.minecraft.resources.Identifier;

/** Where a suggestion popup gets the icon for one entry, if there is one. */
@FunctionalInterface
public interface SuggestionIcons {

	SuggestionIcons NONE = suggestion -> Optional.empty();

	Optional<Identifier> iconFor(Suggestion suggestion);
}
