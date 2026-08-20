package de.stefantasie.modsversionsupport.ui.widget.list;

import de.stefantasie.modsversionsupport.ModsVersionSupport;
import de.stefantasie.modsversionsupport.domain.report.SupportState;
import de.stefantasie.modsversionsupport.ui.theme.Palette;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;

/** A coloured dot plus a word, telling whether a mod is ready for the target version. */
public final class SupportBadge {

	private static final int DOT_SIZE = 6;

	private SupportBadge() {
	}

	public static void draw(GuiGraphicsExtractor extractor, Font font, SupportState state, int x, int y) {
		extractor.fill(x, y + 1, x + DOT_SIZE, y + 1 + DOT_SIZE, colourOf(state));
		extractor.text(font, labelOf(state), x + DOT_SIZE + 4, y, Palette.TEXT_MUTED);
	}

	public static int widthOf(Font font, SupportState state) {
		return DOT_SIZE + 4 + font.width(labelOf(state));
	}

	public static Component labelOf(SupportState state) {
		return Component.translatable(ModsVersionSupport.translationKey("state." + state.name().toLowerCase()));
	}

	public static int colourOf(SupportState state) {
		return switch (state) {
			case SUPPORTED -> Palette.SUPPORTED;
			case SUPPORTED_PRERELEASE -> Palette.PRERELEASE;
			case UNSUPPORTED, FAILED -> Palette.MISSING;
			case NOT_ON_MODRINTH, PENDING -> Palette.UNKNOWN;
		};
	}
}
