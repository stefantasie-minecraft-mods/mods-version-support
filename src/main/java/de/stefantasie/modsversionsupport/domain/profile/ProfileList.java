package de.stefantasie.modsversionsupport.domain.profile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/** The ordered profiles of the overview. The order is what the user sees and rearranges. */
public final class ProfileList {

	private final List<VersionProfile> profiles = new ArrayList<>();

	public static ProfileList of(List<VersionProfile> profiles) {
		ProfileList list = new ProfileList();
		list.profiles.addAll(profiles);
		return list;
	}

	public List<VersionProfile> asList() {
		return List.copyOf(profiles);
	}

	public int size() {
		return profiles.size();
	}

	public boolean isEmpty() {
		return profiles.isEmpty();
	}

	public void add(VersionProfile profile) {
		profiles.add(profile);
	}

	public void remove(ProfileId id) {
		profiles.removeIf(profile -> profile.id().equals(id));
	}

	public void replace(VersionProfile profile) {
		int index = indexOf(profile.id());
		if (index >= 0) {
			profiles.set(index, profile);
		}
	}

	public Optional<VersionProfile> find(ProfileId id) {
		return profiles.stream().filter(profile -> profile.id().equals(id)).findFirst();
	}

	public List<String> namesInUse() {
		return profiles.stream().map(VersionProfile::displayName).toList();
	}

	public boolean moveUp(ProfileId id) {
		int index = indexOf(id);
		if (index <= 0) {
			return false;
		}
		Collections.swap(profiles, index, index - 1);
		return true;
	}

	public boolean moveDown(ProfileId id) {
		int index = indexOf(id);
		if (index < 0 || index >= profiles.size() - 1) {
			return false;
		}
		Collections.swap(profiles, index, index + 1);
		return true;
	}

	private int indexOf(ProfileId id) {
		for (int index = 0; index < profiles.size(); index++) {
			if (profiles.get(index).id().equals(id)) {
				return index;
			}
		}
		return -1;
	}
}
