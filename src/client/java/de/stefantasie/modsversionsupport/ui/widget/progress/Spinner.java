package de.stefantasie.modsversionsupport.ui.widget.progress;

import de.stefantasie.modsversionsupport.ui.theme.Palette;
import net.minecraft.client.gui.GuiGraphicsExtractor;

/** Four dots circling while a check runs. */
public final class Spinner {

	private static final int DOTS = 4;
	private static final int DOT_SIZE = 2;
	private static final long STEP_MILLIS = 140;
	private static final int[] OFFSETS_X = {2, 5, 5, 2};
	private static final int[] OFFSETS_Y = {2, 2, 5, 5};

	private Spinner() {
	}

	public static void draw(GuiGraphicsExtractor extractor, int x, int y) {
		int lead = (int) (System.currentTimeMillis() / STEP_MILLIS % DOTS);
		for (int dot = 0; dot < DOTS; dot++) {
			float brightness = dot == lead ? 1f : 0.35f;
			int left = x + OFFSETS_X[dot];
			int top = y + OFFSETS_Y[dot];
			extractor.fill(left, top, left + DOT_SIZE, top + DOT_SIZE, Palette.withAlpha(Palette.TEXT, brightness));
		}
	}
}
