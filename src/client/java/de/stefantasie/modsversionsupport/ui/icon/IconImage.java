package de.stefantasie.modsversionsupport.ui.icon;

import com.mojang.blaze3d.platform.NativeImage;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Optional;
import javax.imageio.ImageIO;

/** Turns downloaded bytes into a texture image. Modrinth serves webp, which NativeImage cannot read. */
public final class IconImage {

	private IconImage() {
	}

	public static Optional<NativeImage> decode(byte[] bytes) {
		return readAsPng(bytes).or(() -> readThroughImageIo(bytes));
	}

	private static Optional<NativeImage> readAsPng(byte[] bytes) {
		try {
			return Optional.of(NativeImage.read(bytes));
		} catch (IOException unsupportedFormat) {
			return Optional.empty();
		}
	}

	private static Optional<NativeImage> readThroughImageIo(byte[] bytes) {
		try {
			BufferedImage decoded = ImageIO.read(new ByteArrayInputStream(bytes));
			return decoded == null ? Optional.empty() : Optional.of(toNativeImage(decoded));
		} catch (IOException | RuntimeException unreadable) {
			return Optional.empty();
		}
	}

	private static NativeImage toNativeImage(BufferedImage source) {
		NativeImage target = new NativeImage(source.getWidth(), source.getHeight(), false);
		for (int y = 0; y < source.getHeight(); y++) {
			for (int x = 0; x < source.getWidth(); x++) {
				target.setPixelABGR(x, y, toAbgr(source.getRGB(x, y)));
			}
		}
		return target;
	}

	private static int toAbgr(int argb) {
		int alpha = argb >>> 24;
		int red = (argb >> 16) & 0xFF;
		int green = (argb >> 8) & 0xFF;
		int blue = argb & 0xFF;
		return (alpha << 24) | (blue << 16) | (green << 8) | red;
	}
}
