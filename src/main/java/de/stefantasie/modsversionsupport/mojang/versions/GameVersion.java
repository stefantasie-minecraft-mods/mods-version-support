package de.stefantasie.modsversionsupport.mojang.versions;

import java.time.Instant;

public record GameVersion(String id, ReleaseType type, Instant releasedAt) {
}
