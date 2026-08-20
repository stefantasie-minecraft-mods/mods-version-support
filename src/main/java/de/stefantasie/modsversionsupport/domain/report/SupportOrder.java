package de.stefantasie.modsversionsupport.domain.report;

import java.util.Comparator;
import java.util.List;

/** How the mod lists may be sorted. */
public enum SupportOrder {

	AVAILABILITY(Comparator
			.comparingInt((ModSupportView view) -> rankOf(view.state()))
			.thenComparing(view -> view.mod().displayName(), String.CASE_INSENSITIVE_ORDER)),
	NAME(Comparator.comparing(view -> view.mod().displayName(), String.CASE_INSENSITIVE_ORDER)),
	SOURCE(Comparator
			.comparing((ModSupportView view) -> view.mod().key().source())
			.thenComparing(view -> view.mod().displayName(), String.CASE_INSENSITIVE_ORDER));

	private final Comparator<ModSupportView> comparator;

	SupportOrder(Comparator<ModSupportView> comparator) {
		this.comparator = comparator;
	}

	public List<ModSupportView> sort(List<ModSupportView> views) {
		return views.stream().sorted(comparator).toList();
	}

	public SupportOrder next() {
		return values()[(ordinal() + 1) % values().length];
	}

	private static int rankOf(SupportState state) {
		return switch (state) {
			case SUPPORTED -> 0;
			case SUPPORTED_PRERELEASE -> 1;
			case UNSUPPORTED -> 2;
			case NOT_ON_MODRINTH -> 3;
			case FAILED -> 4;
			case PENDING -> 5;
		};
	}
}
