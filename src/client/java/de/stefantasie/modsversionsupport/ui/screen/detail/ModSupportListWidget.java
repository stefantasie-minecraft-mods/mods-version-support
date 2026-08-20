package de.stefantasie.modsversionsupport.ui.screen.detail;

import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ObjectSelectionList;

public final class ModSupportListWidget extends ObjectSelectionList<ModSupportRow> {

	private static final int ROW_HEIGHT = 32;
	private static final int ROW_WIDTH = 420;

	public ModSupportListWidget(Minecraft minecraft, int width, int height, int top) {
		super(minecraft, width, height, top, ROW_HEIGHT);
	}

	public void show(List<ModSupportRow> rows) {
		replaceEntries(rows);
	}

	@Override
	public int getRowWidth() {
		return Math.min(ROW_WIDTH, width - 20);
	}
}
