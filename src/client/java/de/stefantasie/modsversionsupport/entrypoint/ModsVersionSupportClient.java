package de.stefantasie.modsversionsupport.entrypoint;

import de.stefantasie.modsversionsupport.ModsVersionSupport;
import net.fabricmc.api.ClientModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ModsVersionSupportClient implements ClientModInitializer {

	private static final Logger LOG = LoggerFactory.getLogger(ModsVersionSupport.MOD_ID);

	@Override
	public void onInitializeClient() {
		LOG.info("Mods Version Support ready");
	}
}
