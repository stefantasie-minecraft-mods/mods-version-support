package de.stefantasie.modsversionsupport.testmod;

import de.stefantasie.modsversionsupport.entrypoint.ModsVersionSupportClient;
import de.stefantasie.modsversionsupport.domain.mod.TrackedMod;
import de.stefantasie.modsversionsupport.domain.profile.VersionProfile;
import de.stefantasie.modsversionsupport.domain.selection.ModSelection;
import de.stefantasie.modsversionsupport.runtime.ClientRuntime;
import de.stefantasie.modsversionsupport.ui.screen.editor.ProfileEditorScreen;
import java.util.List;
import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.minecraft.client.gui.screens.TitleScreen;
import org.lwjgl.glfw.GLFW;

/** Opens the editor, types into its fields and records the suggestion popups. */
public final class EditorScreenTest implements FabricClientGameTest {

	private static final List<String> PUBLISHED_MODS = List.of("cloth-config", "fabric-api", "modmenu");

	@Override
	public void runTest(ClientGameTestContext context) {
		ClientRuntime runtime = ModsVersionSupportClient.runtime();

		context.setScreen(() -> ProfileEditorScreen.forNewProfile(runtime, null));
		context.waitTicks(20);
		context.takeScreenshot("editor-fresh");

		typeIntoVersionField(context);
		context.waitTicks(20);
		context.takeScreenshot("editor-version-suggestions");

		pickSecondSuggestionWithArrowKeys(context);
		context.waitTicks(5);
		context.takeScreenshot("editor-version-picked-by-keyboard");

		openDropdownButton(context);
		context.waitTicks(5);
		context.takeScreenshot("editor-dropdown");
		context.getInput().pressKey(GLFW.GLFW_KEY_ESCAPE);

		typeIntoSearchField(context);
		context.waitTicks(60);
		context.takeScreenshot("editor-mod-suggestions");

		showCheckedProfileInTheEditor(context, runtime);

		context.setScreen(TitleScreen::new);
		context.waitTicks(5);
	}

	private void showCheckedProfileInTheEditor(ClientGameTestContext context, ClientRuntime runtime) {
		List<TrackedMod> published = runtime.installedMods().stream()
				.filter(mod -> PUBLISHED_MODS.contains(mod.modId()))
				.map(TrackedMod.class::cast)
				.toList();
		VersionProfile checked = VersionProfile.create("Checked", "26.2", ModSelection.allOf(published));
		runtime.profiles().add(checked);
		runtime.checks().check(checked);
		context.waitFor(client -> runtime.profiles().find(checked.id())
				.filter(stored -> stored.lastReport().isPresent())
				.isPresent(), 1200);

		context.setScreen(() -> ProfileEditorScreen.forExistingProfile(
				runtime, null, runtime.profiles().find(checked.id()).orElseThrow()));
		context.waitTicks(20);
		context.takeScreenshot("editor-with-states");
	}

	private void typeIntoVersionField(ClientGameTestContext context) {
		context.getInput().pressKey(GLFW.GLFW_KEY_TAB);
		clearField(context);
		context.getInput().typeChars("26.");
	}

	private void pickSecondSuggestionWithArrowKeys(ClientGameTestContext context) {
		context.getInput().pressKey(GLFW.GLFW_KEY_DOWN);
		context.getInput().pressKey(GLFW.GLFW_KEY_DOWN);
		context.getInput().pressKey(GLFW.GLFW_KEY_ENTER);
	}

	private void openDropdownButton(ClientGameTestContext context) {
		double[] target = context.computeOnClient(client -> {
			double scale = client.getWindow().getGuiScale();
			double width = client.getWindow().getGuiScaledWidth();
			return new double[] {(width / 2 + 200) * scale, 52 * scale};
		});
		context.getInput().setCursorPos(target[0], target[1]);
		context.waitTick();
		context.getInput().pressMouse(0);
	}

	private void clearField(ClientGameTestContext context) {
		for (int keystroke = 0; keystroke < 12; keystroke++) {
			context.getInput().pressKey(GLFW.GLFW_KEY_BACKSPACE);
		}
	}

	private void typeIntoSearchField(ClientGameTestContext context) {
		context.getInput().pressKey(GLFW.GLFW_KEY_TAB);
		context.getInput().pressKey(GLFW.GLFW_KEY_TAB);
		context.getInput().typeChars("sodium");
	}
}
