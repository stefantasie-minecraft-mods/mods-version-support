package de.stefantasie.modsversionsupport.ui.screen.detail;

import de.stefantasie.modsversionsupport.ModsVersionSupport;
import de.stefantasie.modsversionsupport.domain.profile.VersionProfile;
import de.stefantasie.modsversionsupport.domain.report.ModSupportView;
import de.stefantasie.modsversionsupport.domain.report.SupportOrder;
import de.stefantasie.modsversionsupport.domain.report.SupportReport;
import de.stefantasie.modsversionsupport.runtime.CheckStatus;
import de.stefantasie.modsversionsupport.runtime.ClientRuntime;
import de.stefantasie.modsversionsupport.ui.theme.Palette;
import de.stefantasie.modsversionsupport.ui.widget.progress.SupportBar;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/** Shows for one profile which mods already support the target version. */
public final class ProfileDetailScreen extends Screen {

	private static final int LIST_TOP = 66;
	private static final int FOOTER_HEIGHT = 36;
	private static final int BAR_WIDTH = 200;

	private final ClientRuntime runtime;
	private final Screen parent;
	private final VersionProfile profile;

	private SupportOrder order = SupportOrder.AVAILABILITY;
	private Optional<Instant> shownReport = Optional.empty();
	private ModSupportListWidget list;
	private Button orderButton;

	public ProfileDetailScreen(ClientRuntime runtime, Screen parent, VersionProfile profile) {
		super(Component.literal(profile.displayName()));
		this.runtime = runtime;
		this.parent = parent;
		this.profile = profile;
	}

	@Override
	protected void init() {
		list = addRenderableWidget(new ModSupportListWidget(minecraft, width, height - LIST_TOP - FOOTER_HEIGHT, LIST_TOP));
		showMods();

		orderButton = addRenderableWidget(Button.builder(orderLabel(), press -> cycleOrder())
				.bounds(width / 2 - 154, height - 28, 150, 20).build());
		addRenderableWidget(Button.builder(Component.translatable("gui.done"), press -> onClose())
				.bounds(width / 2 + 4, height - 28, 150, 20).build());
	}

	@Override
	public void extractRenderState(GuiGraphicsExtractor extractor, int mouseX, int mouseY, float partialTick) {
		super.extractRenderState(extractor, mouseX, mouseY, partialTick);
		CheckStatus status = runtime.checks().statusOf(profile);

		extractor.centeredText(font, title, width / 2, 14, Palette.TEXT);
		extractor.centeredText(font, Component.translatable(
				ModsVersionSupport.translationKey("overview.target"), profile.targetVersion()), width / 2, 26, Palette.TEXT_MUTED);

		int barLeft = (width - BAR_WIDTH) / 2;
		if (status.running()) {
			SupportBar.drawProgress(extractor, barLeft, 40, BAR_WIDTH, 10, status.progress().fraction(), 1f);
		} else {
			double supported = status.report().map(report -> report.supportedRatio()).orElse(0d);
			SupportBar.drawResult(extractor, barLeft, 40, BAR_WIDTH, 10, supported, 1f);
			status.report().ifPresent(report -> extractor.centeredText(
					font, Component.literal(report.percent() + "%"), width / 2, 52, Palette.TEXT));
		}
	}

	@Override
	public void tick() {
		Optional<Instant> current = currentProfile().lastReport().map(SupportReport::finishedAt);
		if (!current.equals(shownReport)) {
			showMods();
		}
	}

	@Override
	public void onClose() {
		minecraft.setScreenAndShow(parent);
	}

	private void cycleOrder() {
		order = order.next();
		orderButton.setMessage(orderLabel());
		showMods();
	}

	private Component orderLabel() {
		return Component.translatable(ModsVersionSupport.translationKey("detail.order"),
				Component.translatable(ModsVersionSupport.translationKey("detail.order." + order.name().toLowerCase())));
	}

	private void showMods() {
		VersionProfile current = currentProfile();
		shownReport = current.lastReport().map(SupportReport::finishedAt);
		List<ModSupportView> views = ModSupportView.of(current.selection().selectedMods(), current.lastReport());
		list.show(order.sort(views).stream().map(view -> new ModSupportRow(view, font, runtime.icons())).toList());
	}

	private VersionProfile currentProfile() {
		return runtime.profiles().find(profile.id()).orElse(profile);
	}
}
