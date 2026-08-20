package de.stefantasie.modsversionsupport.http;

public class HttpFailure extends RuntimeException {

	private final int statusCode;

	public HttpFailure(String message, int statusCode) {
		super(message);
		this.statusCode = statusCode;
	}

	public HttpFailure(String message, Throwable cause) {
		super(message, cause);
		this.statusCode = 0;
	}

	public int statusCode() {
		return statusCode;
	}

	public boolean isRateLimited() {
		return statusCode == 429;
	}
}
