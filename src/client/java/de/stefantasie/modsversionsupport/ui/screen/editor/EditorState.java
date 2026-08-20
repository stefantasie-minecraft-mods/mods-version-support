package de.stefantasie.modsversionsupport.ui.screen.editor;

import de.stefantasie.modsversionsupport.domain.mod.ModKey;
import de.stefantasie.modsversionsupport.domain.mod.TrackedMod;
import de.stefantasie.modsversionsupport.domain.profile.VersionProfile;
import de.stefantasie.modsversionsupport.domain.selection.ModSelection;
import java.util.Optional;

/** The profile while it is being edited. */
public final class EditorState {

	private final Optional<VersionProfile> edited;
	private String displayName;
	private String targetVersion;
	private ModSelection selection;

	private EditorState(Optional<VersionProfile> edited, String displayName, String targetVersion, ModSelection selection) {
		this.edited = edited;
		this.displayName = displayName;
		this.targetVersion = targetVersion;
		this.selection = selection;
	}

	public static EditorState forNew(String suggestedName, String targetVersion, ModSelection selection) {
		return new EditorState(Optional.empty(), suggestedName, targetVersion, selection);
	}

	public static EditorState forExisting(VersionProfile profile) {
		return new EditorState(Optional.of(profile), profile.displayName(), profile.targetVersion(), profile.selection());
	}

	public String displayName() {
		return displayName;
	}

	public void renameTo(String name) {
		displayName = name;
	}

	public String targetVersion() {
		return targetVersion;
	}

	public void retargetTo(String version) {
		targetVersion = version;
	}

	public ModSelection selection() {
		return selection;
	}

	public void selectAll() {
		selection = selection.withAllSelected();
	}

	public void selectNone() {
		selection = selection.withNoneSelected();
	}

	public void toggle(ModKey key) {
		selection = selection.withToggled(key);
	}

	public void add(TrackedMod mod) {
		selection = selection.withMod(mod);
	}

	public void removeSelected() {
		selection = selection.withoutSelected();
	}

	public void removeAll() {
		selection = selection.withoutAll();
	}

	public boolean isNew() {
		return edited.isEmpty();
	}

	public VersionProfile toProfile() {
		return edited
				.map(profile -> profile.withDisplayName(displayName).withTargetVersion(targetVersion).withSelection(selection))
				.orElseGet(() -> VersionProfile.create(displayName, targetVersion, selection));
	}
}
