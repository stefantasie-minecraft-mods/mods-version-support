package de.stefantasie.modsversionsupport.domain.profile;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import de.stefantasie.modsversionsupport.domain.selection.ModSelection;
import java.util.List;
import org.junit.jupiter.api.Test;

class ProfileListTest {

	private final VersionProfile first = VersionProfile.create("26.2", "26.2", ModSelection.empty());
	private final VersionProfile second = VersionProfile.create("26.3", "26.3", ModSelection.empty());
	private final VersionProfile third = VersionProfile.create("26.3 (2)", "26.3", ModSelection.empty());

	@Test
	void moveUpSwapsWithPredecessor() {
		ProfileList list = ProfileList.of(List.of(first, second, third));

		assertTrue(list.moveUp(second.id()));

		assertEquals(List.of(second.id(), first.id(), third.id()), idsOf(list));
	}

	@Test
	void moveUpDoesNothingAtTheTop() {
		ProfileList list = ProfileList.of(List.of(first, second));

		assertFalse(list.moveUp(first.id()));
		assertEquals(List.of(first.id(), second.id()), idsOf(list));
	}

	@Test
	void moveDownDoesNothingAtTheBottom() {
		ProfileList list = ProfileList.of(List.of(first, second));

		assertFalse(list.moveDown(second.id()));
	}

	@Test
	void sameVersionMayAppearInSeveralProfiles() {
		ProfileList list = ProfileList.of(List.of(second, third));

		assertEquals(2, list.size());
		assertEquals(List.of("26.3", "26.3 (2)"), list.namesInUse());
	}

	@Test
	void replaceKeepsPosition() {
		ProfileList list = ProfileList.of(List.of(first, second));

		list.replace(second.withDisplayName("Next update"));

		assertEquals("Next update", list.asList().get(1).displayName());
	}

	private List<ProfileId> idsOf(ProfileList list) {
		return list.asList().stream().map(VersionProfile::id).toList();
	}
}
