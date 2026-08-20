package de.stefantasie.modsversionsupport.domain.report;

import static org.junit.jupiter.api.Assertions.assertEquals;

import de.stefantasie.modsversionsupport.domain.mod.InstalledMod;
import de.stefantasie.modsversionsupport.domain.mod.ModrinthMod;
import de.stefantasie.modsversionsupport.domain.mod.TrackedMod;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class SupportOrderTest {

	private final TrackedMod ready = new InstalledMod("sodium", "Sodium", "sodium.jar", Optional.empty());
	private final TrackedMod alpha = new InstalledMod("iris", "Iris", "iris.jar", Optional.empty());
	private final TrackedMod missing = new InstalledMod("lithium", "Lithium", "lithium.jar", Optional.empty());
	private final TrackedMod added = new ModrinthMod("AANobbMI", "Appleskin", Optional.empty());

	private final List<ModSupportView> views = List.of(
			new ModSupportView(missing, ModSupport.unsupported(missing.key())),
			new ModSupportView(added, ModSupport.unknownProject(added.key())),
			new ModSupportView(alpha, ModSupport.supported(alpha.key(), "1.0", ReleaseChannel.ALPHA, false)),
			new ModSupportView(ready, ModSupport.supported(ready.key(), "1.0", ReleaseChannel.RELEASE, false)));

	@Test
	void availabilityPutsReadyModsFirst() {
		assertEquals(
				List.of("Sodium", "Iris", "Lithium", "Appleskin"),
				names(SupportOrder.AVAILABILITY.sort(views)));
	}

	@Test
	void nameSortsAlphabetically() {
		assertEquals(List.of("Appleskin", "Iris", "Lithium", "Sodium"), names(SupportOrder.NAME.sort(views)));
	}

	@Test
	void sourceKeepsInstalledModsTogether() {
		assertEquals(List.of("Iris", "Lithium", "Sodium", "Appleskin"), names(SupportOrder.SOURCE.sort(views)));
	}

	@Test
	void cyclingReturnsToTheStart() {
		assertEquals(SupportOrder.AVAILABILITY, SupportOrder.SOURCE.next());
	}

	private List<String> names(List<ModSupportView> sorted) {
		return sorted.stream().map(view -> view.mod().displayName()).toList();
	}
}
