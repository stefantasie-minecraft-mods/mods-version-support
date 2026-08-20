package de.stefantasie.modsversionsupport.ui.screen.overview;

import de.stefantasie.modsversionsupport.domain.profile.VersionProfile;
import java.util.List;
import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;

public final class ProfileListWidget extends ContainerObjectSelectionList<ProfileRow> {

	private static final int ROW_HEIGHT = 32;
	private static final int ROW_WIDTH = 420;

	public ProfileListWidget(Minecraft minecraft, int width, int height, int top) {
		super(minecraft, width, height, top, ROW_HEIGHT);
	}

	public void show(List<ProfileRow> rows) {
		replaceEntries(rows);
		if (getSelected() == null && !rows.isEmpty()) {
			setSelected(rows.getFirst());
		}
	}

	public Optional<VersionProfile> selectedProfile() {
		return Optional.ofNullable(getSelected()).map(ProfileRow::profile);
	}

	public boolean selectNeighbour(int steps) {
		List<ProfileRow> rows = children();
		if (rows.isEmpty()) {
			return false;
		}
		int current = rows.indexOf(getSelected());
		int next = Math.floorMod(current + steps, rows.size());
		ProfileRow selected = rows.get(next);
		setSelected(selected);
		scrollToEntry(selected);
		return true;
	}

	@Override
	public int getRowWidth() {
		return Math.min(ROW_WIDTH, width - 20);
	}
}
