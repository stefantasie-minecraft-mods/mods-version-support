package de.stefantasie.modsversionsupport.ui.widget.progress;

import de.stefantasie.modsversionsupport.ui.theme.Palette;
import net.minecraft.client.gui.GuiGraphicsExtractor;

/** Dots circling while a check runs, the leading one brightest. */
public final class Spinner {

	public static final int SIZE = 14;

	private static final int DOTS = 8;
	private static final int DOT_SIZE = 3;
	private static final int RADIUS = 5;
	private static final long STEP_MILLIS = 110;
	private static final float DIMMEST = 0.15f;

	private Spinner() {
	}

	public static void draw(GuiGraphicsExtractor extractor, int x, int y) {
		int centreX = x + SIZE / 2;
		int centreY = y + SIZE / 2;
		int lead = (int) (System.currentTimeMillis() / STEP_MILLIS % DOTS);

		for (int dot = 0; dot < DOTS; dot++) {
			double angle = 2 * Math.PI * dot / DOTS;
			int left = centreX + (int) Math.round(Math.cos(angle) * RADIUS) - DOT_SIZE / 2;
			int top = centreY + (int) Math.round(Math.sin(angle) * RADIUS) - DOT_SIZE / 2;
			extractor.fill(left, top, left + DOT_SIZE, top + DOT_SIZE,
					Palette.withAlpha(Palette.TEXT, brightnessOf(dot, lead)));
		}
	}

	private static float brightnessOf(int dot, int lead) {
		int distance = Math.floorMod(lead - dot, DOTS);
		return Math.max(DIMMEST, 1f - distance / (float) DOTS);
	}
}
