package de.stefantasie.modsversionsupport.ui.widget.autocomplete;

import java.util.List;
import java.util.function.Consumer;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.EditBox;

/** Ties a text field to its suggestion source and the popup below it. */
public final class AutocompleteBinding {

	private final EditBox field;
	private final SuggestionSource source;
	private final SuggestionOverlay overlay;
	private final Consumer<Suggestion> onPick;
	private boolean open;
	private List<Suggestion> pinned = List.of();

	public AutocompleteBinding(EditBox field, SuggestionSource source, SuggestionOverlay overlay, Consumer<Suggestion> onPick) {
		this.field = field;
		this.source = source;
		this.overlay = overlay;
		this.onPick = onPick;
	}

	public void openForTyping() {
		open = true;
		pinned = List.of();
	}

	public void openWith(List<Suggestion> suggestions) {
		open = true;
		pinned = suggestions;
	}

	public void close() {
		open = false;
		pinned = List.of();
		overlay.hide();
	}

	/** Late answers from a search only reach the popup because it refreshes while it is open. */
	public void draw(GuiGraphicsExtractor extractor, int mouseX, int mouseY) {
		if (!open || !field.isFocused()) {
			overlay.hide();
			return;
		}
		List<Suggestion> suggestions = pinned.isEmpty() ? source.suggest(field.getValue()) : pinned;
		overlay.showBelow(field.getX(), field.getY(), field.getWidth(), field.getHeight(), suggestions);
		overlay.draw(extractor, mouseX, mouseY);
	}

	public boolean pickAt(double mouseX, double mouseY) {
		if (!open) {
			return false;
		}
		return overlay.clickedAt(mouseX, mouseY).map(suggestion -> {
			onPick.accept(suggestion);
			close();
			return true;
		}).orElse(false);
	}
}
