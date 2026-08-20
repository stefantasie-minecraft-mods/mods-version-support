package de.stefantasie.modsversionsupport.modrinth.project;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import de.stefantasie.modsversionsupport.domain.report.ReleaseChannel;
import de.stefantasie.modsversionsupport.http.RecordingJsonHttpClient;
import java.util.List;
import org.junit.jupiter.api.Test;

class ProjectVersionGatewayTest {

	private final RecordingJsonHttpClient http = RecordingJsonHttpClient.answering("sodium-versions.json");
	private final ProjectVersionGateway gateway = new ProjectVersionGateway(http, List.of("fabric", "quilt"));

	@Test
	void readsChannelAndLoaders() {
		List<ModrinthVersion> versions = gateway.versionsFor("sodium", "26.1.2");

		assertEquals(ReleaseChannel.ALPHA, versions.getFirst().channel());
		assertTrue(versions.getFirst().supportsFabric());
	}

	@Test
	void asksForBothLoadersAndTheTargetVersion() {
		gateway.versionsFor("sodium", "26.1.2");

		String uri = http.requestedUris().getFirst().toString();
		assertTrue(uri.contains("fabric"), uri);
		assertTrue(uri.contains("quilt"), uri);
		assertTrue(uri.contains("26.1.2"), uri);
	}
}
