package de.stefantasie.modsversionsupport.entrypoint;

import com.mojang.blaze3d.platform.InputConstants;
import de.stefantasie.modsversionsupport.ModsVersionSupport;
import de.stefantasie.modsversionsupport.runtime.ClientRuntime;
import de.stefantasie.modsversionsupport.ui.screen.overview.ProfileOverviewScreen;
import java.util.function.Supplier;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;

/** Opens the overview from a key the player may bind. */
public final class OverviewKeyMapping {

	private OverviewKeyMapping() {
	}

	public static void register(Supplier<ClientRuntime> runtime) {
		KeyMapping mapping = KeyMappingHelper.registerKeyMapping(new KeyMapping(
				ModsVersionSupport.translationKey("key.open_overview"),
				InputConstants.UNKNOWN.getValue(),
				KeyMapping.Category.MISC));

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			while (mapping.consumeClick()) {
				client.setScreenAndShow(new ProfileOverviewScreen(runtime.get(), null));
			}
		});
	}
}
