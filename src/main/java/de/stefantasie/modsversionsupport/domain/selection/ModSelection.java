package de.stefantasie.modsversionsupport.domain.selection;

import de.stefantasie.modsversionsupport.domain.mod.ModKey;
import de.stefantasie.modsversionsupport.domain.mod.TrackedMod;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/** The mods a profile knows about, together with the subset that gets checked. */
public record ModSelection(List<TrackedMod> mods, Set<ModKey> selected) {

	public ModSelection {
		mods = List.copyOf(mods);
		selected = Set.copyOf(selected);
	}

	public static ModSelection empty() {
		return new ModSelection(List.of(), Set.of());
	}

	public static ModSelection allOf(List<TrackedMod> mods) {
		return new ModSelection(mods, keysOf(mods));
	}

	public boolean isSelected(ModKey key) {
		return selected.contains(key);
	}

	public List<TrackedMod> selectedMods() {
		return mods.stream().filter(mod -> selected.contains(mod.key())).toList();
	}

	public boolean contains(ModKey key) {
		return mods.stream().anyMatch(mod -> mod.key().equals(key));
	}

	public ModSelection withMod(TrackedMod mod) {
		if (contains(mod.key())) {
			return this;
		}
		List<TrackedMod> extended = new ArrayList<>(mods);
		extended.add(mod);
		Set<ModKey> selectedKeys = new LinkedHashSet<>(selected);
		selectedKeys.add(mod.key());
		return new ModSelection(extended, selectedKeys);
	}

	public ModSelection withSelection(ModKey key, boolean selectedNow) {
		Set<ModKey> selectedKeys = new LinkedHashSet<>(selected);
		if (selectedNow) {
			selectedKeys.add(key);
		} else {
			selectedKeys.remove(key);
		}
		return new ModSelection(mods, selectedKeys);
	}

	public ModSelection withToggled(ModKey key) {
		return withSelection(key, !isSelected(key));
	}

	public ModSelection withAllSelected() {
		return new ModSelection(mods, keysOf(mods));
	}

	public ModSelection withNoneSelected() {
		return new ModSelection(mods, Set.of());
	}

	public ModSelection without(ModKey key) {
		List<TrackedMod> remaining = mods.stream().filter(mod -> !mod.key().equals(key)).toList();
		Set<ModKey> selectedKeys = new LinkedHashSet<>(selected);
		selectedKeys.remove(key);
		return new ModSelection(remaining, selectedKeys);
	}

	public ModSelection withoutSelected() {
		List<TrackedMod> remaining = mods.stream().filter(mod -> !selected.contains(mod.key())).toList();
		return new ModSelection(remaining, Set.of());
	}

	public ModSelection withoutAll() {
		return empty();
	}

	private static Set<ModKey> keysOf(List<TrackedMod> mods) {
		return mods.stream().map(TrackedMod::key).collect(Collectors.toCollection(LinkedHashSet::new));
	}
}
