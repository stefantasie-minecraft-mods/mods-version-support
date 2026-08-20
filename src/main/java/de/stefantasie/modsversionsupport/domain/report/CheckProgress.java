package de.stefantasie.modsversionsupport.domain.report;

public record CheckProgress(int completed, int total) {

	public static final CheckProgress IDLE = new CheckProgress(0, 0);

	public double fraction() {
		return total == 0 ? 0 : (double) completed / total;
	}

	public boolean isFinished() {
		return total > 0 && completed >= total;
	}
}
