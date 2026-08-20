package de.stefantasie.modsversionsupport.ui.widget.autocomplete;

import com.mojang.blaze3d.platform.InputConstants;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.input.KeyEvent;

/** Ties a text field to its suggestion source, the popup below it and the keys that drive both. */
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

	public boolean isOpen() {
		return open && overlay.isVisible();
	}

	/** Late answers from a search only reach the popup because it refreshes while it is open. */
	public void draw(GuiGraphicsExtractor extractor, int mouseX, int mouseY) {
		if (!open) {
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
			pick(suggestion);
			return true;
		}).orElse(false);
	}

	public boolean handleKey(KeyEvent event) {
		if (!isOpen()) {
			return false;
		}
		int key = event.key();
		if (key == InputConstants.KEY_DOWN) {
			return moveHighlight(1);
		}
		if (key == InputConstants.KEY_UP) {
			return moveHighlight(-1);
		}
		if (key == InputConstants.KEY_RETURN || key == InputConstants.KEY_NUMPADENTER) {
			return pickHighlighted();
		}
		if (key == InputConstants.KEY_ESCAPE) {
			close();
			return true;
		}
		return false;
	}

	private boolean moveHighlight(int steps) {
		overlay.moveHighlight(steps);
		return true;
	}

	private boolean pickHighlighted() {
		return overlay.highlightedSuggestion().map(suggestion -> {
			pick(suggestion);
			return true;
		}).orElse(false);
	}

	private void pick(Suggestion suggestion) {
		onPick.accept(suggestion);
		close();
	}
}
