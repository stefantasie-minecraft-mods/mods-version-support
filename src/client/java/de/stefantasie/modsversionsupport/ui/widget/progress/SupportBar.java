package de.stefantasie.modsversionsupport.ui.widget.progress;

import de.stefantasie.modsversionsupport.ui.theme.Palette;
import de.stefantasie.modsversionsupport.ui.theme.TrafficLightGradient;
import net.minecraft.client.gui.GuiGraphicsExtractor;

/** The gradient spans the whole width and is cut off at the given share. */
public final class SupportBar {

	private static final int SEGMENT_WIDTH = 2;

	private SupportBar() {
	}

	public static void draw(GuiGraphicsExtractor extractor, int x, int y, int width, int height, double filled, float alpha) {
		extractor.fill(x, y, x + width, y + height, Palette.withAlpha(Palette.BAR_TRACK, alpha));

		int filledWidth = (int) Math.round(Math.clamp(filled, 0d, 1d) * width);
		for (int offset = 0; offset < filledWidth; offset += SEGMENT_WIDTH) {
			int segmentEnd = Math.min(offset + SEGMENT_WIDTH, filledWidth);
			int colour = TrafficLightGradient.sample((float) offset / width);
			extractor.fill(x + offset, y, x + segmentEnd, y + height, Palette.withAlpha(colour, alpha));
		}
		extractor.outline(x, y, width, height, Palette.withAlpha(Palette.ROW_BORDER, alpha));
	}
}
