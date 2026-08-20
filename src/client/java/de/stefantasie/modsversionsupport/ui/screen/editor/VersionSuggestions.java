package de.stefantasie.modsversionsupport.ui.screen.editor;

import de.stefantasie.modsversionsupport.mojang.versions.GameVersion;
import de.stefantasie.modsversionsupport.mojang.versions.VersionCatalogProvider;
import de.stefantasie.modsversionsupport.ui.widget.autocomplete.Suggestion;
import de.stefantasie.modsversionsupport.ui.widget.autocomplete.SuggestionSource;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import net.minecraft.network.chat.Component;

/** Minecraft versions for the picker, filtered by what the player typed. */
public final class VersionSuggestions implements SuggestionSource {

	private static final DateTimeFormatter RELEASE_DATE = DateTimeFormatter.ofPattern("yyyy-MM-dd").withZone(ZoneId.systemDefault());

	private final VersionCatalogProvider catalog;
	private final boolean includeSnapshots;

	public VersionSuggestions(VersionCatalogProvider catalog, boolean includeSnapshots) {
		this.catalog = catalog;
		this.includeSnapshots = includeSnapshots;
	}

	@Override
	public List<Suggestion> suggest(String typed) {
		return catalog.get().matching(typed, includeSnapshots).stream()
				.map(VersionSuggestions::toSuggestion)
				.toList();
	}

	public List<Suggestion> all() {
		return catalog.get().visible(includeSnapshots).stream().map(VersionSuggestions::toSuggestion).toList();
	}

	private static Suggestion toSuggestion(GameVersion version) {
		return Suggestion.of(
				version.id(),
				Component.literal(version.id()),
				Component.literal(RELEASE_DATE.format(version.releasedAt())));
	}
}
