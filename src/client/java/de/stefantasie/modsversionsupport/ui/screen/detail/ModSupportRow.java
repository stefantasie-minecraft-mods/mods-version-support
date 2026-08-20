package de.stefantasie.modsversionsupport.ui.screen.detail;

import de.stefantasie.modsversionsupport.domain.report.ModSupportView;
import de.stefantasie.modsversionsupport.ui.theme.Palette;
import de.stefantasie.modsversionsupport.ui.widget.list.SupportBadge;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.network.chat.Component;

/** One mod with its verdict for the target version. */
public final class ModSupportRow extends ObjectSelectionList.Entry<ModSupportRow> {

	private final ModSupportView view;
	private final Font font;

	public ModSupportRow(ModSupportView view, Font font) {
		this.view = view;
		this.font = font;
	}

	@Override
	public Component getNarration() {
		return Component.literal(view.mod().displayName());
	}

	@Override
	public void extractContent(GuiGraphicsExtractor extractor, int mouseX, int mouseY, boolean hovered, float partialTick) {
		int left = getContentX();
		int top = getContentY();

		extractor.text(font, Component.literal(view.mod().displayName()), left + 4, top + 1, Palette.TEXT);
		extractor.text(font, Component.literal(secondLine()), left + 4, top + 12, Palette.TEXT_MUTED);

		int badgeWidth = SupportBadge.widthOf(font, view.state());
		SupportBadge.draw(extractor, font, view.state(), getContentRight() - badgeWidth - 4, top + 6);
	}

	private String secondLine() {
		String source = view.mod().fileName().orElse("Modrinth");
		return view.support().versionNumber().map(version -> source + "  →  " + version).orElse(source);
	}
}
