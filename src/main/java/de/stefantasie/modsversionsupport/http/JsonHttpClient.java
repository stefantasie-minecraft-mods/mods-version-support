package de.stefantasie.modsversionsupport.http;

import com.google.gson.JsonElement;
import java.net.URI;

/** Blocking access to JSON endpoints. Callers bring their own thread. */
public interface JsonHttpClient {

	JsonElement get(URI uri);

	JsonElement post(URI uri, JsonElement body);
}
