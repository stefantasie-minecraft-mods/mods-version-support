package de.stefantasie.modsversionsupport.entrypoint;

import de.stefantasie.modsversionsupport.runtime.ClientRuntime;
import de.stefantasie.modsversionsupport.ui.screen.overview.ProfileOverviewScreen;
import java.util.function.Supplier;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.ClientCommands;
import net.minecraft.client.Minecraft;

public final class OverviewCommand {

	private static final String NAME = "modsversionsupport";

	private OverviewCommand() {
	}

	public static void register(Supplier<ClientRuntime> runtime) {
		ClientCommandRegistrationCallback.EVENT.register((dispatcher, context) ->
				dispatcher.register(ClientCommands.literal(NAME).executes(command -> {
					Minecraft client = Minecraft.getInstance();
					client.execute(() -> client.setScreenAndShow(new ProfileOverviewScreen(runtime.get(), null)));
					return 1;
				})));
	}
}
