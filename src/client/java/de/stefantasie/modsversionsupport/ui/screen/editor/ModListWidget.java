package de.stefantasie.modsversionsupport.ui.screen.editor;

import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;

public final class ModListWidget extends ContainerObjectSelectionList<ModRow> {

	private static final int ROW_HEIGHT = 28;
	private static final int ROW_WIDTH = 420;

	public ModListWidget(Minecraft minecraft, int width, int height, int top) {
		super(minecraft, width, height, top, ROW_HEIGHT);
	}

	public void show(List<ModRow> rows) {
		replaceEntries(rows);
	}

	@Override
	public int getRowWidth() {
		return Math.min(ROW_WIDTH, width - 20);
	}
}
