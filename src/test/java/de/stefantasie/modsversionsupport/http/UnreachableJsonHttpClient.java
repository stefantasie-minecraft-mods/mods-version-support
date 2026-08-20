package de.stefantasie.modsversionsupport.http;

import com.google.gson.JsonElement;
import java.io.IOException;
import java.net.URI;

/** Stands in for a machine without a connection, or for an API that is down. */
public final class UnreachableJsonHttpClient implements JsonHttpClient {

	private final HttpFailure failure;

	private UnreachableJsonHttpClient(HttpFailure failure) {
		this.failure = failure;
	}

	public static UnreachableJsonHttpClient offline() {
		return new UnreachableJsonHttpClient(new HttpFailure("offline", new IOException("no route to host")));
	}

	public static UnreachableJsonHttpClient answering(int statusCode) {
		return new UnreachableJsonHttpClient(new HttpFailure("status " + statusCode, statusCode));
	}

	@Override
	public JsonElement get(URI uri) {
		throw failure;
	}

	@Override
	public JsonElement post(URI uri, JsonElement body) {
		throw failure;
	}
}
