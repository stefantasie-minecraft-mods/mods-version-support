package de.stefantasie.modsversionsupport.ui.screen.overview;

import com.mojang.blaze3d.platform.InputConstants;
import de.stefantasie.modsversionsupport.ModsVersionSupport;
import de.stefantasie.modsversionsupport.domain.profile.VersionProfile;
import de.stefantasie.modsversionsupport.runtime.ClientRuntime;
import de.stefantasie.modsversionsupport.ui.screen.detail.ProfileDetailScreen;
import de.stefantasie.modsversionsupport.ui.screen.editor.ProfileEditorScreen;
import de.stefantasie.modsversionsupport.ui.theme.Palette;
import java.util.List;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import de.stefantasie.modsversionsupport.ui.settings.SettingsScreens;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.network.chat.Component;

/** Lists the version profiles and their result. */
public final class ProfileOverviewScreen extends Screen {

	private static final int HEADER_HEIGHT = 34;
	private static final int FOOTER_HEIGHT = 40;
	private static final int BUTTON_HEIGHT = 20;

	private final ClientRuntime runtime;
	private final Screen parent;
	private ProfileListWidget list;

	public ProfileOverviewScreen(ClientRuntime runtime, Screen parent) {
		super(Component.translatable(ModsVersionSupport.translationKey("overview.title")));
		this.runtime = runtime;
		this.parent = parent;
	}

	@Override
	protected void init() {
		list = addRenderableWidget(new ProfileListWidget(minecraft, width, height - HEADER_HEIGHT - FOOTER_HEIGHT, HEADER_HEIGHT));
		showProfiles();

		int buttonY = height - FOOTER_HEIGHT + 10;
		addRenderableWidget(Button.builder(Component.translatable(ModsVersionSupport.translationKey("overview.add")), press -> addProfile())
				.bounds(width / 2 - 154, buttonY, 100, BUTTON_HEIGHT).build());
		addRenderableWidget(Button.builder(Component.translatable(ModsVersionSupport.translationKey("overview.refresh")), press -> refresh())
				.bounds(width / 2 - 50, buttonY, 100, BUTTON_HEIGHT).build());
		addRenderableWidget(Button.builder(Component.translatable("gui.done"), press -> onClose())
				.bounds(width / 2 + 54, buttonY, 100, BUTTON_HEIGHT).build());
		addRenderableWidget(Button.builder(Component.literal("⚙"), press -> openSettings())
				.tooltip(Tooltip.create(Component.translatable(ModsVersionSupport.translationKey("settings.title"))))
				.bounds(width - 28, 8, 20, 20).build());

		runtime.checks().checkAll();
		setInitialFocus(list);
	}

	@Override
	public boolean keyPressed(KeyEvent event) {
		if (isEnter(event) && list.selectedProfile().isPresent()) {
			openDetail(list.selectedProfile().orElseThrow());
			return true;
		}
		if (event.key() == InputConstants.KEY_DOWN) {
			return list.selectNeighbour(1);
		}
		if (event.key() == InputConstants.KEY_UP) {
			return list.selectNeighbour(-1);
		}
		return super.keyPressed(event);
	}

	private static boolean isEnter(KeyEvent event) {
		return event.key() == InputConstants.KEY_RETURN || event.key() == InputConstants.KEY_NUMPADENTER;
	}

	@Override
	public void extractRenderState(GuiGraphicsExtractor extractor, int mouseX, int mouseY, float partialTick) {
		super.extractRenderState(extractor, mouseX, mouseY, partialTick);
		extractor.centeredText(font, title, width / 2, 14, Palette.TEXT);
		if (runtime.profiles().all().isEmpty()) {
			extractor.centeredText(font, Component.translatable(ModsVersionSupport.translationKey("overview.empty")),
					width / 2, height / 2 - 4, Palette.TEXT_MUTED);
		}
	}

	@Override
	public void onClose() {
		minecraft.setScreenAndShow(parent);
	}

	private void showProfiles() {
		RowActions actions = new RowActions(this::openDetail, this::openEditor, this::deleteProfile, this::moveUp, this::moveDown);
		List<ProfileRow> rows = runtime.profiles().all().stream()
				.map(profile -> new ProfileRow(profile, runtime.checks()::statusOf, font, actions))
				.toList();
		list.show(rows);
	}

	private void addProfile() {
		minecraft.setScreenAndShow(ProfileEditorScreen.forNewProfile(runtime, this));
	}

	private void openSettings() {
		minecraft.setScreenAndShow(SettingsScreens.create(runtime, this));
	}

	private void openDetail(VersionProfile profile) {
		minecraft.setScreenAndShow(new ProfileDetailScreen(runtime, this, profile));
	}

	private void openEditor(VersionProfile profile) {
		minecraft.setScreenAndShow(ProfileEditorScreen.forExistingProfile(runtime, this, profile));
	}

	private void refresh() {
		runtime.checks().checkAll();
	}

	private void deleteProfile(VersionProfile profile) {
		runtime.checks().forget(profile.id());
		runtime.profiles().remove(profile.id());
		showProfiles();
	}

	private void moveUp(VersionProfile profile) {
		runtime.profiles().moveUp(profile.id());
		showProfiles();
	}

	private void moveDown(VersionProfile profile) {
		runtime.profiles().moveDown(profile.id());
		showProfiles();
	}
}
