package de.stefantasie.modsversionsupport.ui.widget.autocomplete;

import java.util.Optional;
import net.minecraft.network.chat.Component;

/** One entry of a suggestion popup. */
public record Suggestion(String value, Component label, Optional<Component> detail) {

	public static Suggestion of(String value) {
		return new Suggestion(value, Component.literal(value), Optional.empty());
	}

	public static Suggestion of(String value, Component label, Component detail) {
		return new Suggestion(value, label, Optional.of(detail));
	}
}
