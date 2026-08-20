package de.stefantasie.modsversionsupport.storage;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.stefantasie.modsversionsupport.domain.profile.ProfileList;
import de.stefantasie.modsversionsupport.storage.codec.ProfileDocumentCodec;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;

/** Reads and writes the profile file, replacing it atomically. */
public final class ProfileStore {

	private final Path file;

	public ProfileStore(Path file) {
		this.file = file;
	}

	public ProfileList load() {
		if (!Files.exists(file)) {
			return ProfileList.of(List.of());
		}
		try {
			String content = Files.readString(file, StandardCharsets.UTF_8);
			JsonObject json = JsonParser.parseString(content).getAsJsonObject();
			return ProfileDocumentCodec.read(json);
		} catch (IOException cause) {
			throw new UncheckedIOException("Cannot read " + file, cause);
		}
	}

	public void save(ProfileList profiles) {
		String content = new GsonBuilder().setPrettyPrinting().create()
				.toJson(ProfileDocumentCodec.write(profiles));
		try {
			Files.createDirectories(file.getParent());
			Path temporary = file.resolveSibling(file.getFileName() + ".tmp");
			Files.writeString(temporary, content, StandardCharsets.UTF_8);
			Files.move(temporary, file, StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException cause) {
			throw new UncheckedIOException("Cannot write " + file, cause);
		}
	}
}
