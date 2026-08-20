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

/** One mod with its verdict for the target version. */
public final class ModSupportRow extends ObjectSelectionList.Entry<ModSupportRow> {

	private static final int ICON_LEFT = 4;
	private static final int TEXT_LEFT = 26;

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
		int left = getContentX();
		int top = getContentY();

		ModIcon.draw(extractor, font, icons.iconFor(view.mod()), view.mod().displayName(), left + ICON_LEFT, top + 3);
		extractor.text(font, Component.literal(view.mod().displayName()), left + TEXT_LEFT, top + 1, Palette.TEXT);
		extractor.text(font, Component.literal(secondLine()), left + TEXT_LEFT, top + 12, Palette.TEXT_MUTED);

		int badgeWidth = SupportBadge.widthOf(font, view.state());
		SupportBadge.draw(extractor, font, view.state(), getContentRight() - badgeWidth - 4, top + 6);
	}

	private String secondLine() {
		String source = view.mod().fileName().orElse("Modrinth");
		return view.support().versionNumber().map(version -> source + "  →  " + version).orElse(source);
	}
}
