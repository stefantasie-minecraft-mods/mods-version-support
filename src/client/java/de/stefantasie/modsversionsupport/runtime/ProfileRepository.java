package de.stefantasie.modsversionsupport.runtime;

import de.stefantasie.modsversionsupport.domain.profile.ProfileId;
import de.stefantasie.modsversionsupport.domain.profile.ProfileList;
import de.stefantasie.modsversionsupport.domain.profile.VersionProfile;
import de.stefantasie.modsversionsupport.storage.ProfileStore;
import java.util.List;
import java.util.Optional;

/** Holds the profiles of this instance and writes every change to disk. */
public final class ProfileRepository {

	private final ProfileStore store;
	private final ProfileList profiles;

	public ProfileRepository(ProfileStore store) {
		this.store = store;
		this.profiles = store.load();
	}

	public synchronized List<VersionProfile> all() {
		return profiles.asList();
	}

	public synchronized Optional<VersionProfile> find(ProfileId id) {
		return profiles.find(id);
	}

	public synchronized List<String> namesInUse() {
		return profiles.namesInUse();
	}

	public synchronized void add(VersionProfile profile) {
		profiles.add(profile);
		persist();
	}

	public synchronized void replace(VersionProfile profile) {
		profiles.replace(profile);
		persist();
	}

	public synchronized void remove(ProfileId id) {
		profiles.remove(id);
		persist();
	}

	public synchronized void moveUp(ProfileId id) {
		if (profiles.moveUp(id)) {
			persist();
		}
	}

	public synchronized void moveDown(ProfileId id) {
		if (profiles.moveDown(id)) {
			persist();
		}
	}

	private void persist() {
		store.save(profiles);
	}
}
