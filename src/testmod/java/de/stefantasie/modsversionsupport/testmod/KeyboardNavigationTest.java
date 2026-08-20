package de.stefantasie.modsversionsupport.testmod;

import de.stefantasie.modsversionsupport.domain.mod.TrackedMod;
import de.stefantasie.modsversionsupport.domain.profile.VersionProfile;
import de.stefantasie.modsversionsupport.domain.selection.ModSelection;
import de.stefantasie.modsversionsupport.entrypoint.ModsVersionSupportClient;
import de.stefantasie.modsversionsupport.runtime.ClientRuntime;
import de.stefantasie.modsversionsupport.ui.screen.detail.ProfileDetailScreen;
import de.stefantasie.modsversionsupport.ui.screen.overview.ProfileOverviewScreen;
import java.util.List;
import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.minecraft.client.gui.screens.TitleScreen;
import org.lwjgl.glfw.GLFW;

/** Arrow keys pick a row, enter opens its mods, escape returns. */
public final class KeyboardNavigationTest implements FabricClientGameTest {

	@Override
	public void runTest(ClientGameTestContext context) {
		ClientRuntime runtime = ModsVersionSupportClient.runtime();
		addProfile(runtime, "Keyboard one", "26.2");
		addProfile(runtime, "Keyboard two", "26.1.2");

		context.setScreen(() -> new ProfileOverviewScreen(runtime, null));
		context.waitTicks(10);
		context.getInput().pressKey(GLFW.GLFW_KEY_DOWN);
		context.waitTicks(2);
		context.takeScreenshot("keyboard-row-selected");

		context.getInput().pressKey(GLFW.GLFW_KEY_ENTER);
		context.waitForScreen(ProfileDetailScreen.class);
		context.takeScreenshot("keyboard-detail-opened");

		context.getInput().pressKey(GLFW.GLFW_KEY_ESCAPE);
		context.waitForScreen(ProfileOverviewScreen.class);
		context.takeScreenshot("keyboard-back-in-overview");

		context.setScreen(TitleScreen::new);
		context.waitTicks(5);
	}

	private void addProfile(ClientRuntime runtime, String name, String version) {
		List<TrackedMod> installed = List.copyOf(runtime.installedMods());
		runtime.profiles().add(VersionProfile.create(name, version, ModSelection.allOf(installed)));
	}
}
