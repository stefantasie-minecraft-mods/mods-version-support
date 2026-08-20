package de.stefantasie.modsversionsupport.ui.widget.progress;

import de.stefantasie.modsversionsupport.ui.theme.Palette;
import de.stefantasie.modsversionsupport.ui.theme.TrafficLightGradient;
import net.minecraft.client.gui.GuiGraphicsExtractor;

/** While a check runs the bar stays grey; the gradient belongs to the finished result. */
public final class SupportBar {

	private static final int SEGMENT_WIDTH = 2;

	private SupportBar() {
	}

	public static void drawProgress(GuiGraphicsExtractor extractor, int x, int y, int width, int height, double done, float alpha) {
		drawTrack(extractor, x, y, width, height, alpha);
		int filledWidth = widthOf(width, done);
		extractor.fill(x, y, x + filledWidth, y + height, Palette.withAlpha(Palette.PROGRESS, alpha));
		drawFrame(extractor, x, y, width, height, alpha);
	}

	public static void drawResult(GuiGraphicsExtractor extractor, int x, int y, int width, int height, double supported, float alpha) {
		drawTrack(extractor, x, y, width, height, alpha);
		int filledWidth = widthOf(width, supported);
		for (int offset = 0; offset < filledWidth; offset += SEGMENT_WIDTH) {
			int segmentEnd = Math.min(offset + SEGMENT_WIDTH, filledWidth);
			int colour = TrafficLightGradient.sample((float) offset / width);
			extractor.fill(x + offset, y, x + segmentEnd, y + height, Palette.withAlpha(colour, alpha));
		}
		drawFrame(extractor, x, y, width, height, alpha);
	}

	private static void drawTrack(GuiGraphicsExtractor extractor, int x, int y, int width, int height, float alpha) {
		extractor.fill(x, y, x + width, y + height, Palette.withAlpha(Palette.BAR_TRACK, alpha));
	}

	private static void drawFrame(GuiGraphicsExtractor extractor, int x, int y, int width, int height, float alpha) {
		extractor.outline(x, y, width, height, Palette.withAlpha(Palette.ROW_BORDER, alpha));
	}

	private static int widthOf(int width, double share) {
		return (int) Math.round(Math.clamp(share, 0d, 1d) * width);
	}
}
