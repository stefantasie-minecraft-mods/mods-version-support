package de.stefantasie.modsversionsupport.ui.theme;

public final class Palette {

	public static final int TEXT = 0xFFEDEDED;
	public static final int TEXT_MUTED = 0xFF9B9B9B;
	public static final int TEXT_DISABLED = 0xFF6E6E6E;
	public static final int ROW_BACKGROUND = 0x50000000;
	public static final int ROW_BORDER = 0x40FFFFFF;
	public static final int BAR_TRACK = 0xFF2B2B2B;
	public static final int PROGRESS = 0xFF7C7C7C;
	public static final int SUPPORTED = 0xFF3FA34D;
	public static final int PRERELEASE = 0xFFE8B03A;
	public static final int MISSING = 0xFFC63A2E;
	public static final int UNKNOWN = 0xFF7A7A7A;

	private Palette() {
	}

	public static int withAlpha(int argb, float alpha) {
		int scaled = Math.round(((argb >>> 24) & 0xFF) * alpha);
		return (scaled << 24) | (argb & 0x00FFFFFF);
	}
}
