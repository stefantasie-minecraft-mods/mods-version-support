package de.stefantasie.modsversionsupport.modrinth.search;

import java.util.Optional;

public record SearchHit(String projectId, String slug, String title, Optional<String> iconUrl) {
}
