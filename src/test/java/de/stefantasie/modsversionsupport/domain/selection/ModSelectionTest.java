package de.stefantasie.modsversionsupport.domain.selection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import de.stefantasie.modsversionsupport.domain.mod.InstalledMod;
import de.stefantasie.modsversionsupport.domain.mod.ModrinthMod;
import de.stefantasie.modsversionsupport.domain.mod.TrackedMod;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class ModSelectionTest {

	private final TrackedMod sodium = new InstalledMod("sodium", "Sodium", "sodium.jar", Optional.of("abc"));
	private final TrackedMod fabricApi = new InstalledMod("fabric-api", "Fabric API", "fabric-api.jar", Optional.empty());
	private final TrackedMod added = new ModrinthMod("AANobbMI", "Sodium Extra", Optional.empty());

	@Test
	void newlyAddedModStartsSelected() {
		ModSelection selection = ModSelection.empty().withMod(added);

		assertTrue(selection.isSelected(added.key()));
	}

	@Test
	void togglingRemovesFromTheCheck() {
		ModSelection selection = ModSelection.allOf(List.of(sodium, fabricApi)).withToggled(sodium.key());

		assertFalse(selection.isSelected(sodium.key()));
		assertEquals(List.of(fabricApi), selection.selectedMods());
	}

	@Test
	void clearingKeepsTheModsInTheList() {
		ModSelection selection = ModSelection.allOf(List.of(sodium, fabricApi)).withNoneSelected();

		assertEquals(2, selection.mods().size());
		assertTrue(selection.selectedMods().isEmpty());
	}

	@Test
	void removingSelectedDropsThemFromTheList() {
		ModSelection selection = ModSelection.allOf(List.of(sodium, fabricApi))
				.withToggled(fabricApi.key())
				.withoutSelected();

		assertEquals(List.of(fabricApi), selection.mods());
	}

	@Test
	void addingAModTwiceChangesNothing() {
		ModSelection selection = ModSelection.empty().withMod(added).withMod(added);

		assertEquals(1, selection.mods().size());
	}
}
