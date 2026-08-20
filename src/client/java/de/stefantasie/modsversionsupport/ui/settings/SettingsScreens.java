package de.stefantasie.modsversionsupport.ui.settings;

import de.stefantasie.modsversionsupport.ModsVersionSupport;
import de.stefantasie.modsversionsupport.config.Settings;
import de.stefantasie.modsversionsupport.runtime.ClientRuntime;
import java.util.concurrent.atomic.AtomicReference;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/** The settings screen, built with Cloth Config. */
public final class SettingsScreens {

	private SettingsScreens() {
	}

	public static Screen create(ClientRuntime runtime, Screen parent) {
		Settings current = runtime.settings();
		AtomicReference<Settings> edited = new AtomicReference<>(current);

		ConfigBuilder builder = ConfigBuilder.create()
				.setParentScreen(parent)
				.setTitle(label("settings.title"))
				.setSavingRunnable(() -> runtime.applySettings(edited.get()));

		ConfigEntryBuilder entries = builder.entryBuilder();
		ConfigCategory category = builder.getOrCreateCategory(label("settings.category"));

		category.addEntry(entries.startBooleanToggle(label("settings.snapshots"), current.includeSnapshots())
				.setDefaultValue(Settings.defaults().includeSnapshots())
				.setTooltip(label("settings.snapshots.tooltip"))
				.setSaveConsumer(value -> edited.updateAndGet(settings -> new Settings(
						value, settings.parallelProfiles(), settings.cacheLifetimeMinutes(), settings.contact())))
				.build());

		category.addEntry(entries.startIntSlider(label("settings.parallel"), current.parallelProfiles(),
						Settings.MIN_PARALLEL_PROFILES, Settings.MAX_PARALLEL_PROFILES)
				.setDefaultValue(Settings.defaults().parallelProfiles())
				.setTooltip(label("settings.restart_needed"))
				.setSaveConsumer(value -> edited.updateAndGet(settings -> new Settings(
						settings.includeSnapshots(), value, settings.cacheLifetimeMinutes(), settings.contact())))
				.build());

		category.addEntry(entries.startIntSlider(label("settings.cache"), current.cacheLifetimeMinutes(),
						Settings.MIN_CACHE_MINUTES, Settings.MAX_CACHE_MINUTES)
				.setDefaultValue(Settings.defaults().cacheLifetimeMinutes())
				.setTooltip(label("settings.restart_needed"))
				.setSaveConsumer(value -> edited.updateAndGet(settings -> new Settings(
						settings.includeSnapshots(), settings.parallelProfiles(), value, settings.contact())))
				.build());

		category.addEntry(entries.startStrField(label("settings.contact"), current.contact())
				.setDefaultValue(Settings.defaults().contact())
				.setTooltip(label("settings.contact.tooltip"))
				.setSaveConsumer(value -> edited.updateAndGet(settings -> new Settings(
						settings.includeSnapshots(), settings.parallelProfiles(), settings.cacheLifetimeMinutes(), value)))
				.build());

		return builder.build();
	}

	private static Component label(String key) {
		return Component.translatable(ModsVersionSupport.translationKey(key));
	}
}
