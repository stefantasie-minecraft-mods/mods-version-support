package de.stefantasie.modsversionsupport.ui.modmenu;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import de.stefantasie.modsversionsupport.entrypoint.ModsVersionSupportClient;
import de.stefantasie.modsversionsupport.ui.screen.overview.ProfileOverviewScreen;

public final class ModsVersionSupportModMenu implements ModMenuApi {

	@Override
	public ConfigScreenFactory<?> getModConfigScreenFactory() {
		return parent -> new ProfileOverviewScreen(ModsVersionSupportClient.runtime(), parent);
	}
}
