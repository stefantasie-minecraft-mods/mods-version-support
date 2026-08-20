package de.stefantasie.modsversionsupport.ui.screen.overview;

import de.stefantasie.modsversionsupport.domain.profile.VersionProfile;
import java.util.function.Consumer;

public record RowActions(
		Consumer<VersionProfile> edit,
		Consumer<VersionProfile> delete,
		Consumer<VersionProfile> moveUp,
		Consumer<VersionProfile> moveDown) {
}
