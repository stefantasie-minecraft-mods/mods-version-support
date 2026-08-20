package de.stefantasie.modsversionsupport.testmod;

import de.stefantasie.modsversionsupport.entrypoint.ModsVersionSupportClient;
import de.stefantasie.modsversionsupport.ui.settings.SettingsScreens;
import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.minecraft.client.gui.screens.TitleScreen;

/** Opens the Cloth Config screen to make sure the settings render. */
public final class SettingsScreenTest implements FabricClientGameTest {

	@Override
	public void runTest(ClientGameTestContext context) {
		context.setScreen(() -> SettingsScreens.create(ModsVersionSupportClient.runtime(), null));
		context.waitTicks(20);
		context.takeScreenshot("settings");

		context.setScreen(TitleScreen::new);
		context.waitTicks(5);
	}
}
