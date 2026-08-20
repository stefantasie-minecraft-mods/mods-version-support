package de.stefantasie.modsversionsupport.domain.profile;

import java.util.UUID;

public record ProfileId(UUID value) {

	public static ProfileId random() {
		return new ProfileId(UUID.randomUUID());
	}

	public static ProfileId parse(String stored) {
		return new ProfileId(UUID.fromString(stored));
	}

	public String stored() {
		return value.toString();
	}
}
