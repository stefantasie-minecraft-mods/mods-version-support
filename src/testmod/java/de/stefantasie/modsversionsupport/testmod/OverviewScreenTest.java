package de.stefantasie.modsversionsupport.testmod;

import de.stefantasie.modsversionsupport.domain.mod.TrackedMod;
import de.stefantasie.modsversionsupport.domain.profile.ProfileNames;
import de.stefantasie.modsversionsupport.domain.profile.VersionProfile;
import de.stefantasie.modsversionsupport.domain.selection.ModSelection;
import de.stefantasie.modsversionsupport.entrypoint.ModsVersionSupportClient;
import de.stefantasie.modsversionsupport.runtime.ClientRuntime;
import de.stefantasie.modsversionsupport.ui.screen.overview.ProfileOverviewScreen;
import java.util.List;
import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.minecraft.client.gui.screens.TitleScreen;

/** Drives the overview screen and records what it looks like. */
public final class OverviewScreenTest implements FabricClientGameTest {

	@Override
	public void runTest(ClientGameTestContext context) {
		ClientRuntime runtime = ModsVersionSupportClient.runtime();
		addProfile(runtime, "26.2");
		addProfile(runtime, "1.21.11");

		context.setScreen(() -> new ProfileOverviewScreen(runtime, null));
		context.waitTicks(10);
		context.takeScreenshot("overview-running");

		context.waitTicks(200);
		context.takeScreenshot("overview-finished");

		context.setScreen(TitleScreen::new);
		context.waitTicks(5);
	}

	private void addProfile(ClientRuntime runtime, String version) {
		List<TrackedMod> installed = List.copyOf(runtime.installedMods());
		String name = ProfileNames.defaultNameFor(version, runtime.profiles().namesInUse());
		runtime.profiles().add(VersionProfile.create(name, version, ModSelection.allOf(installed)));
	}
}
