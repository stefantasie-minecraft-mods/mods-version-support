package de.stefantasie.modsversionsupport.ui.screen.editor;

import de.stefantasie.modsversionsupport.domain.mod.TrackedMod;
import de.stefantasie.modsversionsupport.ui.theme.Palette;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.network.chat.Component;

/** One mod in the editor: checkbox, display name and the file it came from. */
public final class ModRow extends ContainerObjectSelectionList.Entry<ModRow> {

	private static final int TEXT_LEFT = 26;

	private final TrackedMod mod;
	private final Font font;
	private final Checkbox checkbox;

	public ModRow(TrackedMod mod, boolean selected, Font font, Consumer<TrackedMod> onToggle) {
		this.mod = mod;
		this.font = font;
		this.checkbox = Checkbox.builder(Component.empty(), font)
				.selected(selected)
				.onValueChange((box, value) -> onToggle.accept(mod))
				.build();
	}

	public TrackedMod mod() {
		return mod;
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

		extractor.text(font, Component.literal(mod.displayName()), left + TEXT_LEFT, top + 1, Palette.TEXT);
		extractor.text(font, Component.literal(mod.fileName().orElse("Modrinth")), left + TEXT_LEFT, top + 12, Palette.TEXT_MUTED);
	}
}
