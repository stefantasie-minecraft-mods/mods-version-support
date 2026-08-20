package de.stefantasie.modsversionsupport.platform.installed;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.Optional;

/** Modrinth identifies files by their SHA-1 sum. */
public final class JarHash {

	private static final int BUFFER_SIZE = 64 * 1024;

	private JarHash() {
	}

	public static Optional<String> sha1Of(Path file) {
		try (InputStream input = Files.newInputStream(file);
				DigestInputStream digest = new DigestInputStream(input, MessageDigest.getInstance("SHA-1"))) {
			byte[] buffer = new byte[BUFFER_SIZE];
			while (digest.read(buffer) != -1) {
				continue;
			}
			return Optional.of(HexFormat.of().formatHex(digest.getMessageDigest().digest()));
		} catch (IOException | NoSuchAlgorithmException unreadable) {
			return Optional.empty();
		}
	}
}
