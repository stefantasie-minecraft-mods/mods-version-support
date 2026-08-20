package de.stefantasie.modsversionsupport.platform.installed;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class JarHashTest {

	@Test
	void matchesTheSumModrinthExpects(@TempDir Path directory) throws IOException {
		Path file = Files.writeString(directory.resolve("mod.jar"), "abc");

		assertEquals("a9993e364706816aba3e25717850c26c9cd0d89d", JarHash.sha1Of(file).orElseThrow());
	}

	@Test
	void anUnreadableFileYieldsNoHash(@TempDir Path directory) {
		assertTrue(JarHash.sha1Of(directory.resolve("absent.jar")).isEmpty());
	}
}
