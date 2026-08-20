package de.stefantasie.modsversionsupport.ui.screen.editor;

import de.stefantasie.modsversionsupport.domain.mod.TrackedMod;
import de.stefantasie.modsversionsupport.domain.report.ModSupportView;
import de.stefantasie.modsversionsupport.domain.report.SupportState;
import de.stefantasie.modsversionsupport.ui.theme.Palette;
import de.stefantasie.modsversionsupport.ui.widget.list.SupportBadge;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.network.chat.Component;

/** One mod in the editor: checkbox, name, source file and, once checked, its state. */
public final class ModRow extends ContainerObjectSelectionList.Entry<ModRow> {

	private static final int TEXT_LEFT = 26;

	private final ModSupportView view;
	private final Font font;
	private final Checkbox checkbox;

	public ModRow(ModSupportView view, boolean selected, Font font, Consumer<TrackedMod> onToggle) {
		this.view = view;
		this.font = font;
		this.checkbox = Checkbox.builder(Component.empty(), font)
				.selected(selected)
				.onValueChange((box, value) -> onToggle.accept(view.mod()))
				.build();
	}

	public TrackedMod mod() {
		return view.mod();
	}

	@Override
	public List<? extends GuiEventListener> children() {
		return List.of(checkbox);
	}

	@Override
	public List<? extends NarratableEntry> narratables() {
		return List.of(checkbox);
	}

	@Override
	public void extractContent(GuiGraphicsExtractor extractor, int mouseX, int mouseY, boolean hovered, float partialTick) {
		int left = getContentX();
		int top = getContentY();

		checkbox.setX(left + 2);
		checkbox.setY(top + 2);
		checkbox.extractRenderState(extractor, mouseX, mouseY, partialTick);

		extractor.text(font, Component.literal(view.mod().displayName()), left + TEXT_LEFT, top + 1, Palette.TEXT);
		extractor.text(font, Component.literal(view.mod().fileName().orElse("Modrinth")),
				left + TEXT_LEFT, top + 12, Palette.TEXT_MUTED);

		if (view.state() != SupportState.PENDING) {
			int badgeWidth = SupportBadge.widthOf(font, view.state());
			SupportBadge.draw(extractor, font, view.state(), getContentRight() - badgeWidth - 4, top + 6);
		}
	}
}
