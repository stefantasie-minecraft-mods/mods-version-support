package de.stefantasie.modsversionsupport.runtime;

import de.stefantasie.modsversionsupport.domain.report.CheckProgress;
import de.stefantasie.modsversionsupport.domain.report.SupportReport;
import java.util.Optional;

/** What the overview needs to draw one row right now. */
public record CheckStatus(boolean running, CheckProgress progress, Optional<SupportReport> report) {

	public static CheckStatus idle(Optional<SupportReport> lastReport) {
		return new CheckStatus(false, CheckProgress.IDLE, lastReport);
	}
}
