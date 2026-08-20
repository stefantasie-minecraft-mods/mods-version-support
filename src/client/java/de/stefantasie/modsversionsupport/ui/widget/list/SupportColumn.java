package de.stefantasie.modsversionsupport.ui.widget.list;

import de.stefantasie.modsversionsupport.domain.report.ModSupportView;
import de.stefantasie.modsversionsupport.domain.report.SupportState;
import de.stefantasie.modsversionsupport.ui.theme.Palette;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;

/** The right-hand side of a mod row: its state and the newest Minecraft version it reaches. */
public final class SupportColumn {

	private SupportColumn() {
	}

	public static void draw(GuiGraphicsExtractor extractor, Font font, ModSupportView view, int right, int top, int lineGap) {
		if (view.state() == SupportState.PENDING) {
			return;
		}
		int badgeWidth = SupportBadge.widthOf(font, view.state());
		SupportBadge.draw(extractor, font, view.state(), right - badgeWidth, top);

		view.support().newestSupportedGameVersion().ifPresent(version -> {
			Component label = Component.literal(version);
			extractor.text(font, label, right - font.width(label), top + lineGap, Palette.TEXT_MUTED);
		});
	}
}
