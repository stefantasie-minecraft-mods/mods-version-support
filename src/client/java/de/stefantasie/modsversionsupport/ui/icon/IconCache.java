package de.stefantasie.modsversionsupport.ui.icon;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.Optional;

/** Keeps downloaded icons on disk so a restart does not fetch them again. */
public final class IconCache {

	private final Path directory;

	public IconCache(Path directory) {
		this.directory = directory;
	}

	public Optional<byte[]> read(String url) {
		Path file = fileFor(url);
		try {
			return Files.exists(file) ? Optional.of(Files.readAllBytes(file)) : Optional.empty();
		} catch (IOException unreadable) {
			return Optional.empty();
		}
	}

	public void write(String url, byte[] bytes) {
		try {
			Files.createDirectories(directory);
			Files.write(fileFor(url), bytes);
		} catch (IOException ignored) {
			// Without the cache the icon is fetched again next time.
		}
	}

	private Path fileFor(String url) {
		return directory.resolve(hashOf(url));
	}

	private static String hashOf(String url) {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-1");
			return HexFormat.of().formatHex(digest.digest(url.getBytes(java.nio.charset.StandardCharsets.UTF_8)));
		} catch (NoSuchAlgorithmException impossible) {
			throw new IllegalStateException(impossible);
		}
	}
}
