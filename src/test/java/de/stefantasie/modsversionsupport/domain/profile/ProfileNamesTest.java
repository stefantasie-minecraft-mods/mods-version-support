package de.stefantasie.modsversionsupport.domain.profile;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.junit.jupiter.api.Test;

class ProfileNamesTest {

	@Test
	void usesVersionWhenNoProfileCarriesIt() {
		assertEquals("26.2", ProfileNames.defaultNameFor("26.2", List.of()));
	}

	@Test
	void countsUpWhileNameIsTaken() {
		assertEquals("26.2 (3)", ProfileNames.defaultNameFor("26.2", List.of("26.2", "26.2 (2)")));
	}

	@Test
	void ignoresUnrelatedNames() {
		assertEquals("26.2", ProfileNames.defaultNameFor("26.2", List.of("1.21.11", "Before the update")));
	}
}
