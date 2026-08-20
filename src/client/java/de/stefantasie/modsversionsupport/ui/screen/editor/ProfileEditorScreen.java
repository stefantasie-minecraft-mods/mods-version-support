package de.stefantasie.modsversionsupport.ui.screen.editor;

import de.stefantasie.modsversionsupport.ModsVersionSupport;
import de.stefantasie.modsversionsupport.domain.mod.TrackedMod;
import de.stefantasie.modsversionsupport.domain.profile.ProfileNames;
import de.stefantasie.modsversionsupport.domain.profile.VersionProfile;
import de.stefantasie.modsversionsupport.domain.report.ModSupportView;
import de.stefantasie.modsversionsupport.domain.report.SupportOrder;
import de.stefantasie.modsversionsupport.domain.selection.ModSelection;
import de.stefantasie.modsversionsupport.runtime.ClientRuntime;
import de.stefantasie.modsversionsupport.ui.theme.Palette;
import de.stefantasie.modsversionsupport.ui.widget.autocomplete.AutocompleteBinding;
import de.stefantasie.modsversionsupport.ui.widget.autocomplete.Suggestion;
import de.stefantasie.modsversionsupport.ui.widget.autocomplete.SuggestionIcons;
import de.stefantasie.modsversionsupport.ui.widget.autocomplete.SuggestionOverlay;
import java.util.List;
import java.util.Optional;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

/** Creates and edits one version profile. */
public final class ProfileEditorScreen extends Screen {

	private static final int FIELD_HEIGHT = 20;
	private static final int LABEL_TOP = 32;
	private static final int FIELD_TOP = 42;
	private static final int SEARCH_TOP = 72;
	private static final int ORDER_TOP = 96;
	private static final int LIST_TOP = 116;
	private static final int FOOTER_HEIGHT = 66;
	private static final int CONTENT_WIDTH = 420;

	private final ClientRuntime runtime;
	private final Screen parent;
	private final EditorState state;
	private final VersionSuggestions versionSuggestions;
	private final ModSuggestions modSuggestions;

	private SupportOrder order = SupportOrder.AVAILABILITY;
	private Button orderButton;
	private EditBox nameField;
	private EditBox versionField;
	private EditBox searchField;
	private ModListWidget modList;
	private AutocompleteBinding versionAutocomplete;
	private AutocompleteBinding searchAutocomplete;

	private ProfileEditorScreen(ClientRuntime runtime, Screen parent, EditorState state) {
		super(Component.translatable(ModsVersionSupport.translationKey(
				state.isNew() ? "editor.title_new" : "editor.title_edit")));
		this.runtime = runtime;
		this.parent = parent;
		this.state = state;
		this.versionSuggestions = new VersionSuggestions(runtime.versions(), runtime.settings().includeSnapshots());
		this.modSuggestions = new ModSuggestions(runtime.search());
	}

	public static ProfileEditorScreen forNewProfile(ClientRuntime runtime, Screen parent) {
		String version = runtime.versions().get().visible(runtime.settings().includeSnapshots()).stream()
				.findFirst()
				.map(de.stefantasie.modsversionsupport.mojang.versions.GameVersion::id)
				.orElse("");
		String name = ProfileNames.defaultNameFor(version, runtime.profiles().namesInUse());
		List<TrackedMod> installed = List.copyOf(runtime.installedMods());
		return new ProfileEditorScreen(runtime, parent, EditorState.forNew(name, version, ModSelection.allOf(installed)));
	}

	public static ProfileEditorScreen forExistingProfile(ClientRuntime runtime, Screen parent, VersionProfile profile) {
		return new ProfileEditorScreen(runtime, parent, EditorState.forExisting(profile));
	}

	@Override
	protected void init() {
		int left = (width - CONTENT_WIDTH) / 2;

		nameField = addRenderableWidget(new EditBox(font, left, FIELD_TOP, 200, FIELD_HEIGHT,
				Component.translatable(ModsVersionSupport.translationKey("editor.name"))));
		nameField.setValue(state.displayName());
		nameField.setResponder(state::renameTo);

		versionField = addRenderableWidget(new EditBox(font, left + 210, FIELD_TOP, CONTENT_WIDTH - 210 - 24, FIELD_HEIGHT,
				Component.translatable(ModsVersionSupport.translationKey("editor.version"))));
		versionField.setValue(state.targetVersion());

		versionAutocomplete = new AutocompleteBinding(versionField, versionSuggestions, new SuggestionOverlay(font, SuggestionIcons.NONE), suggestion -> {
			versionField.setValue(suggestion.value());
			state.retargetTo(suggestion.value());
		});
		versionField.setResponder(typed -> {
			state.retargetTo(typed);
			versionAutocomplete.openForTyping();
		});

		addRenderableWidget(Button.builder(Component.literal("▼"), press -> openVersionDropdown())
				.bounds(left + CONTENT_WIDTH - 20, FIELD_TOP, 20, FIELD_HEIGHT).build());

		searchField = addRenderableWidget(new EditBox(font, left, SEARCH_TOP, CONTENT_WIDTH, FIELD_HEIGHT,
				Component.translatable(ModsVersionSupport.translationKey("editor.search"))));
		searchField.setHint(Component.translatable(ModsVersionSupport.translationKey("editor.search")));
		searchAutocomplete = new AutocompleteBinding(searchField, modSuggestions, new SuggestionOverlay(font, this::iconForSuggestion), suggestion ->
				modSuggestions.modFor(suggestion).ifPresent(mod -> {
					state.add(mod);
					searchField.setValue("");
					showMods();
				}));
		searchField.setResponder(typed -> searchAutocomplete.openForTyping());

		orderButton = addRenderableWidget(Button.builder(orderLabel(), press -> cycleOrder())
				.bounds(left + CONTENT_WIDTH - 150, ORDER_TOP, 150, 16).build());

		modList = addRenderableWidget(new ModListWidget(minecraft, width, height - LIST_TOP - FOOTER_HEIGHT, LIST_TOP));
		showMods();

		addToolbar(left);
		addFooter();
		setInitialFocus(nameField);
	}

	@Override
	public void extractRenderState(GuiGraphicsExtractor extractor, int mouseX, int mouseY, float partialTick) {
		super.extractRenderState(extractor, mouseX, mouseY, partialTick);
		extractor.centeredText(font, title, width / 2, 14, Palette.TEXT);
		int left = (width - CONTENT_WIDTH) / 2;
		extractor.text(font, Component.translatable(ModsVersionSupport.translationKey("editor.name")),
				left, LABEL_TOP, Palette.TEXT_MUTED);
		extractor.text(font, Component.translatable(ModsVersionSupport.translationKey("editor.version")),
				left + 210, LABEL_TOP, Palette.TEXT_MUTED);
		extractor.text(font, Component.translatable(ModsVersionSupport.translationKey("editor.mod_count"),
				state.selection().selectedMods().size(), state.selection().mods().size()),
				left, LIST_TOP - 12, Palette.TEXT_MUTED);
		versionAutocomplete.draw(extractor, mouseX, mouseY);
		searchAutocomplete.draw(extractor, mouseX, mouseY);
	}

	@Override
	public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
		if (versionAutocomplete.pickAt(event.x(), event.y()) || searchAutocomplete.pickAt(event.x(), event.y())) {
			return true;
		}
		return super.mouseClicked(event, doubleClick);
	}

	@Override
	public void onClose() {
		minecraft.setScreen(parent);
	}

	private void addToolbar(int left) {
		int toolbarY = height - FOOTER_HEIGHT + 4;
		int buttonWidth = CONTENT_WIDTH / 4 - 3;
		addRenderableWidget(toolbarButton("editor.select_all", left, toolbarY, buttonWidth, () -> {
			state.selectAll();
			showMods();
		}));
		addRenderableWidget(toolbarButton("editor.select_none", left + buttonWidth + 4, toolbarY, buttonWidth, () -> {
			state.selectNone();
			showMods();
		}));
		addRenderableWidget(toolbarButton("editor.remove_selected", left + 2 * (buttonWidth + 4), toolbarY, buttonWidth, () -> {
			state.removeSelected();
			showMods();
		}));
		addRenderableWidget(toolbarButton("editor.remove_all", left + 3 * (buttonWidth + 4), toolbarY, buttonWidth, () -> {
			state.removeAll();
			showMods();
		}));
	}

	private void addFooter() {
		int footerY = height - 28;
		addRenderableWidget(Button.builder(Component.translatable(ModsVersionSupport.translationKey("editor.save")), press -> save())
				.bounds(width / 2 - 104, footerY, 100, FIELD_HEIGHT).build());
		addRenderableWidget(Button.builder(Component.translatable("gui.cancel"), press -> onClose())
				.bounds(width / 2 + 4, footerY, 100, FIELD_HEIGHT).build());
	}

	private Button toolbarButton(String key, int x, int y, int buttonWidth, Runnable action) {
		return Button.builder(Component.translatable(ModsVersionSupport.translationKey(key)), press -> action.run())
				.bounds(x, y, buttonWidth, FIELD_HEIGHT).build();
	}

	private void openVersionDropdown() {
		versionField.setFocused(true);
		setFocused(versionField);
		versionAutocomplete.openWith(versionSuggestions.all());
	}

	private Optional<Identifier> iconForSuggestion(Suggestion suggestion) {
		return modSuggestions.modFor(suggestion).flatMap(runtime.icons()::iconFor);
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
		List<ModSupportView> views = ModSupportView.of(state.selection().mods(), state.lastReport());
		List<ModRow> rows = order.sort(views).stream()
				.map(view -> new ModRow(view, state.selection().isSelected(view.mod().key()), font, runtime.icons(), this::toggle))
				.toList();
		modList.show(rows);
	}

	private void toggle(TrackedMod mod) {
		state.toggle(mod.key());
	}

	private void save() {
		VersionProfile profile = state.toProfile();
		if (state.isNew()) {
			runtime.profiles().add(profile);
		} else {
			runtime.profiles().replace(profile);
		}
		runtime.checks().check(profile);
		onClose();
	}
}
