package de.stefantasie.modsversionsupport.ui.widget.icon;

import de.stefantasie.modsversionsupport.ui.theme.Palette;
import java.util.Optional;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

/** Draws a mod icon, or its initial while the texture is still loading. */
public final class ModIcon {

	public static final int SIZE = 16;

	private ModIcon() {
	}

	public static void draw(GuiGraphicsExtractor extractor, Font font, Optional<Identifier> texture, String name, int x, int y) {
		texture.ifPresentOrElse(
				identifier -> extractor.blit(RenderPipelines.GUI_TEXTURED, identifier, x, y, 0f, 0f, SIZE, SIZE, SIZE, SIZE),
				() -> drawInitial(extractor, font, name, x, y));
	}

	private static void drawInitial(GuiGraphicsExtractor extractor, Font font, String name, int x, int y) {
		extractor.fill(x, y, x + SIZE, y + SIZE, Palette.ROW_BACKGROUND);
		extractor.outline(x, y, SIZE, SIZE, Palette.ROW_BORDER);
		if (!name.isBlank()) {
			Component initial = Component.literal(name.substring(0, 1).toUpperCase());
			extractor.text(font, initial, x + (SIZE - font.width(initial)) / 2, y + 4, Palette.TEXT_MUTED);
		}
	}
}
