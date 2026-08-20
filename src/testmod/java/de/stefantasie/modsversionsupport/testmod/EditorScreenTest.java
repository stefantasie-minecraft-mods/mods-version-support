package de.stefantasie.modsversionsupport.testmod;

import de.stefantasie.modsversionsupport.entrypoint.ModsVersionSupportClient;
import de.stefantasie.modsversionsupport.runtime.ClientRuntime;
import de.stefantasie.modsversionsupport.ui.screen.editor.ProfileEditorScreen;
import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.minecraft.client.gui.screens.TitleScreen;
import org.lwjgl.glfw.GLFW;

/** Opens the editor, types into its fields and records the suggestion popups. */
public final class EditorScreenTest implements FabricClientGameTest {

	@Override
	public void runTest(ClientGameTestContext context) {
		ClientRuntime runtime = ModsVersionSupportClient.runtime();

		context.setScreen(() -> ProfileEditorScreen.forNewProfile(runtime, null));
		context.waitTicks(20);
		context.takeScreenshot("editor-fresh");

		typeIntoVersionField(context);
		context.waitTicks(20);
		context.takeScreenshot("editor-version-suggestions");

		typeIntoSearchField(context);
		context.waitTicks(60);
		context.takeScreenshot("editor-mod-suggestions");

		context.setScreen(TitleScreen::new);
		context.waitTicks(5);
	}

	private void typeIntoVersionField(ClientGameTestContext context) {
		context.getInput().pressKey(GLFW.GLFW_KEY_TAB);
		clearField(context);
		context.getInput().typeChars("26.");
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
