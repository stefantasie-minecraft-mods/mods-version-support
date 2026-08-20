package de.stefantasie.modsversionsupport.entrypoint;

import de.stefantasie.modsversionsupport.ModsVersionSupport;
import de.stefantasie.modsversionsupport.runtime.ClientRuntime;
import de.stefantasie.modsversionsupport.runtime.RuntimeSettings;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;

public final class ModsVersionSupportClient implements ClientModInitializer {

	private static ClientRuntime runtime;

	public static ClientRuntime runtime() {
		if (runtime == null) {
			throw new IllegalStateException(ModsVersionSupport.MOD_ID + " is not initialized yet");
		}
		return runtime;
	}

	@Override
	public void onInitializeClient() {
		runtime = ClientRuntime.start(RuntimeSettings.defaults());
		OverviewKeyMapping.register(ModsVersionSupportClient::runtime);
		OverviewCommand.register(ModsVersionSupportClient::runtime);
		ClientLifecycleEvents.CLIENT_STOPPING.register(client -> runtime.close());
	}
}
