package de.stefantasie.modsversionsupport.ui.widget.autocomplete;

import de.stefantasie.modsversionsupport.ui.theme.Palette;
import de.stefantasie.modsversionsupport.ui.widget.icon.ModIcon;
import java.util.List;
import java.util.Optional;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.resources.Identifier;

/** The popup under a text field. The screen draws it last so it covers the widgets below. */
public final class SuggestionOverlay {

	private static final int ENTRY_HEIGHT = 18;
	private static final int MAX_ENTRIES = 7;
	private static final int BACKGROUND = 0xFF16121A;

	private final Font font;
	private final SuggestionIcons icons;
	private List<Suggestion> suggestions = List.of();
	private int x;
	private int y;
	private int width;
	private boolean visible;

	public SuggestionOverlay(Font font, SuggestionIcons icons) {
		this.font = font;
		this.icons = icons;
	}

	public void showBelow(int fieldX, int fieldY, int fieldWidth, int fieldHeight, List<Suggestion> found) {
		this.x = fieldX;
		this.y = fieldY + fieldHeight + 1;
		this.width = fieldWidth;
		this.suggestions = found.size() > MAX_ENTRIES ? found.subList(0, MAX_ENTRIES) : found;
		this.visible = !this.suggestions.isEmpty();
	}

	public void hide() {
		visible = false;
		suggestions = List.of();
	}

	public boolean isVisible() {
		return visible;
	}

	public void draw(GuiGraphicsExtractor extractor, int mouseX, int mouseY) {
		if (!visible) {
			return;
		}
		int height = suggestions.size() * ENTRY_HEIGHT;
		extractor.fill(x, y, x + width, y + height, BACKGROUND);
		extractor.outline(x, y, width, height, Palette.ROW_BORDER);

		for (int index = 0; index < suggestions.size(); index++) {
			int entryTop = y + index * ENTRY_HEIGHT;
			if (isOver(mouseX, mouseY, entryTop)) {
				extractor.fill(x + 1, entryTop, x + width - 1, entryTop + ENTRY_HEIGHT, Palette.ROW_BACKGROUND);
			}
			Suggestion suggestion = suggestions.get(index);
			Optional<Identifier> icon = icons.iconFor(suggestion);
			int labelLeft = x + 4;
			if (icon.isPresent()) {
				ModIcon.draw(extractor, font, icon, suggestion.label().getString(), labelLeft, entryTop - 1);
				labelLeft += ModIcon.SIZE + 4;
			}
			extractor.text(font, suggestion.label(), labelLeft, entryTop + 3, Palette.TEXT);
			suggestion.detail().ifPresent(detail -> extractor.text(
					font, detail, x + width - 4 - font.width(detail), entryTop + 3, Palette.TEXT_MUTED));
		}
	}

	public Optional<Suggestion> clickedAt(double mouseX, double mouseY) {
		if (!visible) {
			return Optional.empty();
		}
		for (int index = 0; index < suggestions.size(); index++) {
			int entryTop = y + index * ENTRY_HEIGHT;
			if (isOver((int) mouseX, (int) mouseY, entryTop)) {
				return Optional.of(suggestions.get(index));
			}
		}
		return Optional.empty();
	}

	private boolean isOver(int mouseX, int mouseY, int entryTop) {
		return mouseX >= x && mouseX <= x + width && mouseY >= entryTop && mouseY < entryTop + ENTRY_HEIGHT;
	}
}
