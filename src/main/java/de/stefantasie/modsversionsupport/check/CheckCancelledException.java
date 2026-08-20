package de.stefantasie.modsversionsupport.check;

public final class CheckCancelledException extends RuntimeException {

	public CheckCancelledException() {
		super("Check cancelled");
	}
}
