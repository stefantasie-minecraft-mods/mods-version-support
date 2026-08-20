package de.stefantasie.modsversionsupport.storage.codec;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import de.stefantasie.modsversionsupport.domain.mod.ModKey;
import de.stefantasie.modsversionsupport.domain.report.ModSupport;
import de.stefantasie.modsversionsupport.domain.report.ReleaseChannel;
import de.stefantasie.modsversionsupport.domain.report.SupportReport;
import de.stefantasie.modsversionsupport.domain.report.SupportState;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public final class SupportReportCodec {

	private SupportReportCodec() {
	}

	public static JsonObject write(SupportReport report) {
		JsonArray results = new JsonArray();
		for (ModSupport result : report.results()) {
			JsonObject entry = new JsonObject();
			entry.addProperty("mod", result.mod().stored());
			entry.addProperty("state", result.state().name());
			result.versionNumber().ifPresent(version -> entry.addProperty("versionNumber", version));
			result.channel().ifPresent(channel -> entry.addProperty("channel", channel.name()));
			result.newestSupportedGameVersion().ifPresent(version -> entry.addProperty("newestSupported", version));
			if (result.quiltOnly()) {
				entry.addProperty("quiltOnly", true);
			}
			results.add(entry);
		}

		JsonObject json = new JsonObject();
		json.addProperty("finishedAt", report.finishedAt().toString());
		json.add("results", results);
		return json;
	}

	public static SupportReport read(JsonObject json) {
		List<ModSupport> results = new ArrayList<>();
		for (var element : json.getAsJsonArray("results")) {
			JsonObject entry = element.getAsJsonObject();
			results.add(new ModSupport(
					ModKey.parse(entry.get("mod").getAsString()),
					SupportState.valueOf(entry.get("state").getAsString()),
					JsonValues.optionalString(entry, "versionNumber"),
					JsonValues.optionalString(entry, "channel").map(ReleaseChannel::valueOf),
					JsonValues.optionalString(entry, "newestSupported"),
					entry.has("quiltOnly") && entry.get("quiltOnly").getAsBoolean()));
		}
		return new SupportReport(results, Instant.parse(json.get("finishedAt").getAsString()));
	}
}
