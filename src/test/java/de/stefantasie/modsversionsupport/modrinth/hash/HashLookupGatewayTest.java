package de.stefantasie.modsversionsupport.modrinth.hash;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import de.stefantasie.modsversionsupport.http.RecordingJsonHttpClient;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class HashLookupGatewayTest {

	private static final String FABRIC_API_SHA1 = "5fe5204ccc96a17340570db66a5b4127b8176246";
	private static final String MOD_MENU_SHA1 = "0b5cd2531cd3506e431cd2d739da3abceed08ea6";

	private final RecordingJsonHttpClient http = RecordingJsonHttpClient.answering("version-files.json");

	@Test
	void mapsEveryHashToItsProject() {
		Map<String, HashMatch> matches = new HashLookupGateway(http).lookup(List.of(FABRIC_API_SHA1, MOD_MENU_SHA1));

		assertEquals("P7dR8mSH", matches.get(FABRIC_API_SHA1).projectId());
		assertEquals("mOgUt4GM", matches.get(MOD_MENU_SHA1).projectId());
	}

	@Test
	void asksOnceForTheWholeBatch() {
		new HashLookupGateway(http).lookup(List.of(FABRIC_API_SHA1, MOD_MENU_SHA1));

		assertEquals(1, http.requestedUris().size());
		assertEquals("sha1", http.postedBodies().getFirst().getAsJsonObject().get("algorithm").getAsString());
		assertEquals(2, http.postedBodies().getFirst().getAsJsonObject().getAsJsonArray("hashes").size());
	}

	@Test
	void sendsNothingForAnEmptyInstance() {
		assertTrue(new HashLookupGateway(http).lookup(List.of()).isEmpty());
		assertTrue(http.requestedUris().isEmpty());
	}
}
