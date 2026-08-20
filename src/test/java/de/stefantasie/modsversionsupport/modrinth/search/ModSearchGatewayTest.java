package de.stefantasie.modsversionsupport.modrinth.search;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import de.stefantasie.modsversionsupport.http.RecordingJsonHttpClient;
import java.util.List;
import org.junit.jupiter.api.Test;

class ModSearchGatewayTest {

	private final RecordingJsonHttpClient http = RecordingJsonHttpClient.answering("search-sodium.json");

	@Test
	void readsTitleAndIconOfEveryHit() {
		List<SearchHit> hits = new ModSearchGateway(http).search("sodium");

		assertEquals("Sodium", hits.getFirst().title());
		assertEquals("sodium", hits.getFirst().slug());
		assertTrue(hits.getFirst().iconUrl().orElseThrow().startsWith("https://cdn.modrinth.com/"));
	}

	@Test
	void anEmptyQueryStaysLocal() {
		assertTrue(new ModSearchGateway(http).search("  ").isEmpty());
		assertTrue(http.requestedUris().isEmpty());
	}
}
