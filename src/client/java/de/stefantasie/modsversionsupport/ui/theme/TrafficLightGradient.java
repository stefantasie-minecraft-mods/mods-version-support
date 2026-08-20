package de.stefantasie.modsversionsupport.ui.theme;

/** Red on the left, warm amber in the middle, green on the right. */
public final class TrafficLightGradient {

	private static final int[] STOPS = {Palette.MISSING, Palette.PRERELEASE, Palette.SUPPORTED};

	private TrafficLightGradient() {
	}

	public static int sample(float position) {
		float clamped = Math.clamp(position, 0f, 1f);
		float scaled = clamped * (STOPS.length - 1);
		int lower = (int) Math.floor(scaled);
		int upper = Math.min(lower + 1, STOPS.length - 1);
		return blend(STOPS[lower], STOPS[upper], scaled - lower);
	}

	private static int blend(int from, int to, float amount) {
		int alpha = channel(from, 24, to, amount);
		int red = channel(from, 16, to, amount);
		int green = channel(from, 8, to, amount);
		int blue = channel(from, 0, to, amount);
		return (alpha << 24) | (red << 16) | (green << 8) | blue;
	}

	private static int channel(int from, int shift, int to, float amount) {
		int start = (from >>> shift) & 0xFF;
		int end = (to >>> shift) & 0xFF;
		return Math.round(start + (end - start) * amount);
	}
}
