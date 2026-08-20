package de.stefantasie.modsversionsupport.storage.codec;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import de.stefantasie.modsversionsupport.domain.mod.ModKey;
import de.stefantasie.modsversionsupport.domain.mod.TrackedMod;
import de.stefantasie.modsversionsupport.domain.profile.ProfileId;
import de.stefantasie.modsversionsupport.domain.profile.VersionProfile;
import de.stefantasie.modsversionsupport.domain.report.SupportReport;
import de.stefantasie.modsversionsupport.domain.selection.ModSelection;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public final class VersionProfileCodec {

	private VersionProfileCodec() {
	}

	public static JsonObject write(VersionProfile profile) {
		JsonArray mods = new JsonArray();
		profile.selection().mods().forEach(mod -> mods.add(TrackedModCodec.write(mod)));

		JsonArray selected = new JsonArray();
		profile.selection().selected().forEach(key -> selected.add(key.stored()));

		JsonObject json = new JsonObject();
		json.addProperty("id", profile.id().stored());
		json.addProperty("displayName", profile.displayName());
		json.addProperty("targetVersion", profile.targetVersion());
		json.add("mods", mods);
		json.add("selected", selected);
		profile.lastReport().ifPresent(report -> json.add("lastReport", SupportReportCodec.write(report)));
		return json;
	}

	public static VersionProfile read(JsonObject json) {
		List<TrackedMod> mods = new ArrayList<>();
		json.getAsJsonArray("mods").forEach(element -> mods.add(TrackedModCodec.read(element.getAsJsonObject())));

		Set<ModKey> selected = new LinkedHashSet<>();
		json.getAsJsonArray("selected").forEach(element -> selected.add(ModKey.parse(element.getAsString())));

		Optional<SupportReport> report = json.has("lastReport")
				? Optional.of(SupportReportCodec.read(json.getAsJsonObject("lastReport")))
				: Optional.empty();

		return new VersionProfile(
				ProfileId.parse(json.get("id").getAsString()),
				json.get("displayName").getAsString(),
				json.get("targetVersion").getAsString(),
				new ModSelection(mods, selected),
				report);
	}
}
