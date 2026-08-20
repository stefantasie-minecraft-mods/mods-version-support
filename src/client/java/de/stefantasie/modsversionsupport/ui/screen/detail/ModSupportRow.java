package de.stefantasie.modsversionsupport.ui.screen.detail;

import de.stefantasie.modsversionsupport.domain.report.ModSupportView;
import de.stefantasie.modsversionsupport.ui.icon.ModIconTextures;
import de.stefantasie.modsversionsupport.ui.theme.Palette;
import de.stefantasie.modsversionsupport.ui.widget.icon.ModIcon;
import de.stefantasie.modsversionsupport.ui.widget.list.SupportBadge;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.network.chat.Component;

/** One mod with its verdict for the target version and the newest Minecraft version it reaches. */
public final class ModSupportRow extends ObjectSelectionList.Entry<ModSupportRow> {

	private static final int ICON_LEFT = 6;
	private static final int TEXT_LEFT = 30;
	private static final int FIRST_LINE = 6;
	private static final int SECOND_LINE = 18;
	private static final int EDGE = 6;

	private final ModSupportView view;
	private final Font font;
	private final ModIconTextures icons;

	public ModSupportRow(ModSupportView view, Font font, ModIconTextures icons) {
		this.view = view;
		this.font = font;
		this.icons = icons;
	}

	@Override
	public Component getNarration() {
		return Component.literal(view.mod().displayName());
	}

	@Override
	public void extractContent(GuiGraphicsExtractor extractor, int mouseX, int mouseY, boolean hovered, float partialTick) {
		int left = getX();
		int top = getY();
		int right = getX() + getWidth();

		if (hovered) {
			extractor.outline(left, top + 1, getWidth(), getHeight() - 2, Palette.SELECTION);
		}

		ModIcon.draw(extractor, font, icons.iconFor(view.mod()), view.mod().displayName(),
				left + ICON_LEFT, top + (getHeight() - ModIcon.SIZE) / 2);
		extractor.text(font, Component.literal(view.mod().displayName()), left + TEXT_LEFT, top + FIRST_LINE, Palette.TEXT);
		extractor.text(font, Component.literal(view.mod().fileName().orElse("Modrinth")),
				left + TEXT_LEFT, top + SECOND_LINE, Palette.TEXT_MUTED);

		int badgeWidth = SupportBadge.widthOf(font, view.state());
		SupportBadge.draw(extractor, font, view.state(), right - EDGE - badgeWidth, top + FIRST_LINE);
		drawReachedVersion(extractor, right, top);
	}

	private void drawReachedVersion(GuiGraphicsExtractor extractor, int right, int top) {
		view.support().newestSupportedGameVersion().ifPresent(version -> {
			Component label = Component.literal(version);
			extractor.text(font, label, right - EDGE - font.width(label), top + SECOND_LINE, Palette.TEXT_MUTED);
		});
	}
}
