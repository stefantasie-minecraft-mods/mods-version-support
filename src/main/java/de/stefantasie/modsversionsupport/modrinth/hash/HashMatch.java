package de.stefantasie.modsversionsupport.modrinth.hash;

/** The Modrinth project a locally installed jar belongs to. */
public record HashMatch(String sha1, String projectId) {
}
