package de.stefantasie.modsversionsupport.ui.screen.overview;

import de.stefantasie.modsversionsupport.ModsVersionSupport;
import de.stefantasie.modsversionsupport.domain.profile.VersionProfile;
import de.stefantasie.modsversionsupport.runtime.CheckStatus;
import de.stefantasie.modsversionsupport.ui.theme.Palette;
import de.stefantasie.modsversionsupport.ui.widget.progress.Spinner;
import de.stefantasie.modsversionsupport.ui.widget.progress.SupportBar;
import java.util.List;
import java.util.function.Function;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

/** One profile in the overview: name, version, percentage and the traffic light bar. */
public final class ProfileRow extends ContainerObjectSelectionList.Entry<ProfileRow> {

	private static final int BUTTON_SIZE = 18;
	private static final int BAR_WIDTH = 110;
	private static final int BAR_HEIGHT = 12;
	private static final int LABEL_LEFT = 6;
	private static final float DIMMED = 0.45f;

	private final VersionProfile profile;
	private final Function<VersionProfile, CheckStatus> status;
	private final Font font;
	private final List<Button> buttons;
	private final RowActions actions;

	public ProfileRow(VersionProfile profile, Function<VersionProfile, CheckStatus> status, Font font, RowActions actions) {
		this.profile = profile;
		this.status = status;
		this.font = font;
		this.actions = actions;
		this.buttons = List.of(
				iconButton("▲", "overview.move_up", () -> actions.moveUp().accept(profile)),
				iconButton("▼", "overview.move_down", () -> actions.moveDown().accept(profile)),
				iconButton("✎", "overview.edit", () -> actions.edit().accept(profile)),
				iconButton("✕", "overview.delete", () -> actions.delete().accept(profile)));
	}

	public VersionProfile profile() {
		return profile;
	}

	@Override
	public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
		if (super.mouseClicked(event, doubleClick)) {
			return true;
		}
		actions.open().accept(profile);
		return true;
	}

	@Override
	public List<? extends GuiEventListener> children() {
		return buttons;
	}

	@Override
	public List<? extends NarratableEntry> narratables() {
		return buttons;
	}

	@Override
	public void extractContent(GuiGraphicsExtractor extractor, int mouseX, int mouseY, boolean hovered, float partialTick) {
		CheckStatus current = status.apply(profile);
		float alpha = current.running() ? DIMMED : 1f;
		int left = getContentX();
		int top = getContentY();
		int right = getContentRight();

		extractor.fill(left, top, right, getContentBottom(), Palette.withAlpha(Palette.ROW_BACKGROUND, alpha));
		if (hovered) {
			extractor.outline(left, top, right - left, getContentHeight(), Palette.SELECTION);
		}
		drawLabels(extractor, left + LABEL_LEFT, top, alpha);
		drawBar(extractor, current, right, top, alpha);
		layoutButtons(extractor, right, top, mouseX, mouseY, partialTick);
	}

	private void drawLabels(GuiGraphicsExtractor extractor, int left, int top, float alpha) {
		extractor.text(font, Component.literal(profile.displayName()), left, top + 4, Palette.withAlpha(Palette.TEXT, alpha));
		extractor.text(font, Component.translatable(ModsVersionSupport.translationKey("overview.target"), profile.targetVersion()),
				left, top + 16, Palette.withAlpha(Palette.TEXT_MUTED, alpha));
	}

	private void drawBar(GuiGraphicsExtractor extractor, CheckStatus current, int right, int top, float alpha) {
		int barLeft = right - buttonBlockWidth() - BAR_WIDTH - 6;
		int barTop = top + (getContentHeight() - BAR_HEIGHT) / 2;
		if (current.running()) {
			SupportBar.drawProgress(extractor, barLeft, barTop, BAR_WIDTH, BAR_HEIGHT, current.progress().fraction(), alpha);
			Spinner.draw(extractor, barLeft + (BAR_WIDTH - Spinner.SIZE) / 2, barTop + (BAR_HEIGHT - Spinner.SIZE) / 2);
			return;
		}
		double supported = current.report().map(report -> report.supportedRatio()).orElse(0d);
		SupportBar.drawResult(extractor, barLeft, barTop, BAR_WIDTH, BAR_HEIGHT, supported, alpha);
		drawPercentOnBar(extractor, current, barLeft, barTop, alpha);
	}

	private void drawPercentOnBar(GuiGraphicsExtractor extractor, CheckStatus current, int barLeft, int barTop, float alpha) {
		Component percent = Component.literal(current.report().map(report -> report.percent() + "%").orElse("--"));
		int textLeft = barLeft + (BAR_WIDTH - font.width(percent)) / 2;
		extractor.text(font, percent, textLeft, barTop + 2, Palette.withAlpha(Palette.TEXT, alpha), true);
	}

	private void layoutButtons(GuiGraphicsExtractor extractor, int right, int top, int mouseX, int mouseY, float partialTick) {
		int x = right - buttonBlockWidth();
		for (Button button : buttons) {
			button.setX(x);
			button.setY(top + 6);
			button.extractRenderState(extractor, mouseX, mouseY, partialTick);
			x += BUTTON_SIZE + 2;
		}
	}

	private int buttonBlockWidth() {
		return buttons.size() * (BUTTON_SIZE + 2);
	}

	private static Button iconButton(String glyph, String tooltipKey, Runnable action) {
		return Button.builder(Component.literal(glyph), press -> action.run())
				.size(BUTTON_SIZE, BUTTON_SIZE)
				.tooltip(net.minecraft.client.gui.components.Tooltip.create(
						Component.translatable(ModsVersionSupport.translationKey(tooltipKey))))
				.build();
	}
}
