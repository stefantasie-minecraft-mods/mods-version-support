package de.stefantasie.modsversionsupport.testmod;

import net.fabricmc.api.ClientModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ScreenshotHarness implements ClientModInitializer {

	private static final Logger LOG = LoggerFactory.getLogger("mods-version-support-testmod");

	@Override
	public void onInitializeClient() {
		LOG.info("Harness loaded");
	}
}
