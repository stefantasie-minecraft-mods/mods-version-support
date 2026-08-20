package de.stefantasie.modsversionsupport.ui.screen.detail;

import de.stefantasie.modsversionsupport.ModsVersionSupport;
import de.stefantasie.modsversionsupport.domain.report.ModSupportView;
import de.stefantasie.modsversionsupport.ui.icon.ModIconTextures;
import de.stefantasie.modsversionsupport.ui.theme.Palette;
import de.stefantasie.modsversionsupport.ui.widget.icon.ModIcon;
import de.stefantasie.modsversionsupport.ui.widget.list.SupportBadge;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.ObjectSelectionList;
import java.util.Optional;
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

		if (hovered) {
			extractor.outline(left, top, getContentWidth(), getContentHeight(), Palette.SELECTION);
		}
		ModIcon.draw(extractor, font, icons.iconFor(view.mod()), view.mod().displayName(), left + ICON_LEFT, top + 3);
		extractor.text(font, Component.literal(view.mod().displayName()), left + TEXT_LEFT, top + 1, Palette.TEXT);
		extractor.text(font, secondLine(), left + TEXT_LEFT, top + 12, Palette.TEXT_MUTED);

		int badgeWidth = SupportBadge.widthOf(font, view.state());
		SupportBadge.draw(extractor, font, view.state(), getContentRight() - badgeWidth - 4, top + 6);
	}

	private Component secondLine() {
		String source = view.mod().fileName().orElse("Modrinth");
		Optional<String> found = view.support().versionNumber();
		if (found.isPresent()) {
			return Component.literal(source + "  →  " + found.get());
		}
		return view.support().newestSupportedGameVersion()
				.map(version -> Component.literal(source + "  →  ")
						.append(Component.translatable(ModsVersionSupport.translationKey("detail.up_to"), version)))
				.map(Component.class::cast)
				.orElseGet(() -> Component.literal(source));
	}
}
