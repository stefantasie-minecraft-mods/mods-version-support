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

	private static final List<String> PUBLISHED_MODS = List.of("cloth-config", "fabric-api", "modmenu");

	@Override
	public void runTest(ClientGameTestContext context) {
		ClientRuntime runtime = ModsVersionSupportClient.runtime();
		addProfile(runtime, "26.2");
		addProfile(runtime, "1.21.11");
		addProfileOfPublishedMods(runtime, "26.2");

		context.setScreen(() -> new ProfileOverviewScreen(runtime, null));
		context.waitTicks(10);
		context.takeScreenshot("overview-running");

		context.waitTicks(200);
		context.takeScreenshot("overview-finished");

		context.setScreen(TitleScreen::new);
		context.waitTicks(5);
	}

	private void addProfileOfPublishedMods(ClientRuntime runtime, String version) {
		List<TrackedMod> published = runtime.installedMods().stream()
				.filter(mod -> PUBLISHED_MODS.contains(mod.modId()))
				.map(TrackedMod.class::cast)
				.toList();
		runtime.profiles().add(VersionProfile.create("Handpicked", version, ModSelection.allOf(published)));
	}

	private void addProfile(ClientRuntime runtime, String version) {
		List<TrackedMod> installed = List.copyOf(runtime.installedMods());
		String name = ProfileNames.defaultNameFor(version, runtime.profiles().namesInUse());
		runtime.profiles().add(VersionProfile.create(name, version, ModSelection.allOf(installed)));
	}
}
