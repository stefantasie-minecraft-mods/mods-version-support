package de.stefantasie.modsversionsupport.domain.report;

public enum SupportState {
	PENDING,
	SUPPORTED,
	SUPPORTED_PRERELEASE,
	UNSUPPORTED,
	NOT_ON_MODRINTH,
	FAILED;

	public boolean countsAsSupported() {
		return this == SUPPORTED || this == SUPPORTED_PRERELEASE;
	}

	public boolean isResolved() {
		return this != PENDING;
	}
}
