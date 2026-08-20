package de.stefantasie.modsversionsupport.ui.icon;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Optional;

/** Fetches project icons, serving the disk cache first. */
public final class IconDownloader {

	private static final Duration TIMEOUT = Duration.ofSeconds(15);

	private final HttpClient http;
	private final IconCache cache;
	private final String userAgent;

	public IconDownloader(IconCache cache, String userAgent) {
		this.cache = cache;
		this.userAgent = userAgent;
		this.http = HttpClient.newBuilder().connectTimeout(TIMEOUT).followRedirects(HttpClient.Redirect.NORMAL).build();
	}

	public Optional<byte[]> fetch(String url) {
		return cache.read(url).or(() -> download(url));
	}

	private Optional<byte[]> download(String url) {
		HttpRequest request = HttpRequest.newBuilder(URI.create(url))
				.timeout(TIMEOUT)
				.header("User-Agent", userAgent)
				.GET()
				.build();
		try {
			HttpResponse<byte[]> response = http.send(request, HttpResponse.BodyHandlers.ofByteArray());
			if (response.statusCode() >= 400) {
				return Optional.empty();
			}
			cache.write(url, response.body());
			return Optional.of(response.body());
		} catch (IOException unreachable) {
			return Optional.empty();
		} catch (InterruptedException interrupted) {
			Thread.currentThread().interrupt();
			return Optional.empty();
		}
	}
}
