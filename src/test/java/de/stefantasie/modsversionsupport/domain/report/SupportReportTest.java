package de.stefantasie.modsversionsupport.domain.report;

import static org.junit.jupiter.api.Assertions.assertEquals;

import de.stefantasie.modsversionsupport.domain.mod.ModKey;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;

class SupportReportTest {

	@Test
	void unknownProjectsCountAgainstThePercentage() {
		SupportReport report = new SupportReport(List.of(
				ModSupport.supported(ModKey.ofInstalled("sodium"), "0.9.2", ReleaseChannel.RELEASE, false),
				ModSupport.unsupported(ModKey.ofInstalled("iris")),
				ModSupport.unknownProject(ModKey.ofInstalled("homemade")),
				ModSupport.failed(ModKey.ofInstalled("lithium"))), Instant.EPOCH);

		assertEquals(25, report.percent());
	}

	@Test
	void prereleasesCountAsSupported() {
		SupportReport report = new SupportReport(List.of(
				ModSupport.supported(ModKey.ofInstalled("sodium"), "0.9.2-alpha", ReleaseChannel.ALPHA, false),
				ModSupport.unsupported(ModKey.ofInstalled("iris"))), Instant.EPOCH);

		assertEquals(50, report.percent());
		assertEquals(SupportState.SUPPORTED_PRERELEASE, report.results().getFirst().state());
	}

	@Test
	void emptyProfileReportsZero() {
		assertEquals(0, new SupportReport(List.of(), Instant.EPOCH).percent());
	}
}
