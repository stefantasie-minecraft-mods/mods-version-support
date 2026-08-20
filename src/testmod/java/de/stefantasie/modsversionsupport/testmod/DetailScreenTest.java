package de.stefantasie.modsversionsupport.testmod;

import de.stefantasie.modsversionsupport.domain.mod.TrackedMod;
import de.stefantasie.modsversionsupport.domain.profile.VersionProfile;
import de.stefantasie.modsversionsupport.domain.selection.ModSelection;
import de.stefantasie.modsversionsupport.entrypoint.ModsVersionSupportClient;
import de.stefantasie.modsversionsupport.runtime.ClientRuntime;
import de.stefantasie.modsversionsupport.ui.screen.detail.ProfileDetailScreen;
import java.util.List;
import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.minecraft.client.gui.screens.TitleScreen;

/** Checks a profile against the live API and records the per-mod verdicts. */
public final class DetailScreenTest implements FabricClientGameTest {

	private static final int SORT_BUTTON_OFFSET_FROM_BOTTOM = 18;
	private static final String AHEAD_OF_EVERYTHING = "26.3-snapshot-9";
	private static final List<String> PUBLISHED_MODS = List.of("cloth-config", "fabric-api", "modmenu");

	@Override
	public void runTest(ClientGameTestContext context) {
		ClientRuntime runtime = ModsVersionSupportClient.runtime();
		List<TrackedMod> installed = List.copyOf(runtime.installedMods());
		VersionProfile profile = VersionProfile.create("Detail", "26.1.2", ModSelection.allOf(installed));
		runtime.profiles().add(profile);
		runtime.checks().check(profile);

		context.setScreen(() -> new ProfileDetailScreen(runtime, null, profile));
		context.waitFor(client -> runtime.profiles().find(profile.id())
				.filter(stored -> stored.lastReport().isPresent())
				.isPresent(), 1200);
		context.waitTicks(5);
		context.takeScreenshot("detail-availability");

		clickSortButton(context);
		context.waitTicks(5);
		context.takeScreenshot("detail-sorted-by-name");

		showNewestSupportedVersions(context, runtime);

		context.setScreen(TitleScreen::new);
		context.waitTicks(5);
	}

	private void showNewestSupportedVersions(ClientGameTestContext context, ClientRuntime runtime) {
		List<TrackedMod> published = runtime.installedMods().stream()
				.filter(mod -> PUBLISHED_MODS.contains(mod.modId()))
				.map(TrackedMod.class::cast)
				.toList();
		VersionProfile ahead = VersionProfile.create("Ahead", AHEAD_OF_EVERYTHING, ModSelection.allOf(published));
		runtime.profiles().add(ahead);
		runtime.checks().check(ahead);

		context.setScreen(() -> new ProfileDetailScreen(runtime, null, ahead));
		context.waitFor(client -> runtime.profiles().find(ahead.id())
				.filter(stored -> stored.lastReport().isPresent())
				.isPresent(), 1200);
		context.waitTicks(5);
		context.takeScreenshot("detail-newest-supported");
	}

	private void clickSortButton(ClientGameTestContext context) {
		double[] center = context.computeOnClient(client -> {
			double scale = client.getWindow().getGuiScale();
			return new double[] {
					(client.getWindow().getGuiScaledWidth() / 2.0 - 79) * scale,
					(client.getWindow().getGuiScaledHeight() - SORT_BUTTON_OFFSET_FROM_BOTTOM) * scale};
		});
		context.getInput().setCursorPos(center[0], center[1]);
		context.waitTick();
		context.getInput().pressMouse(0);
	}
}
