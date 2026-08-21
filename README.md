# Mods Version Support

A client-side Fabric mod for Minecraft 26.1.2. It answers one question: if this instance moved
to a newer Minecraft version, which of the installed mods would come along?

![Overview](docs/overview.png)

## What it does

- Reads the mods of the running instance and identifies them on Modrinth by the SHA-1 of their jar.
- Keeps a list of **version entries**. Each entry pairs a target Minecraft version with the mods
  to check against it. The same version may appear in several entries with different mod sets.
- Checks every entry in the background. While a check runs the row is greyed out, carries a
  spinner and a grey progress bar; the red-amber-green gradient appears once the result is in.
- Shows per mod whether it is ready, only has a prerelease, has nothing for that version, or is
  unknown to Modrinth. The list sorts by availability, name or source.
- Adds mods that are not installed through a Modrinth search with autocomplete and icons.
- Reports for a mod without a build for the target version which Minecraft version it still sits on.
- Takes the icon of an installed mod out of its own jar; search results show Modrinth's project
  icon, downloaded once and cached on disk under `config/mods-version-support/icons`.

## Using it

Open the overview through Mod Menu, the client command `/modsversionsupport`, or a key you bind
yourself under Controls. Arrow keys move through the entries, enter opens the mods of one entry,
escape goes back; the same keys drive the version and search suggestions. Add an entry with the plus button, pick a Minecraft version from the
dropdown or by typing, and choose which mods take part.

Minecraft versions come from Mojang's `version_manifest_v2.json`, the same list a launcher shows.
Snapshots appear once they are enabled in the settings.

## Releases

Pushing a tag that starts with `v` builds the jar and publishes it to Modrinth and as a GitHub
release. It needs `MODRINTH_TOKEN` as a repository secret and `MODRINTH_PROJECT_ID` as a
repository variable.

## Building

```
./gradlew build
```

The jar lands in `build/libs`. `./gradlew installToPrism` copies it into the Prism Launcher
instance for this Minecraft version; pass `-Pprism_instance_dir=…` for a different path.

Requirements: JDK 25 or newer, Gradle wrapper included. The build uses the non-remapping
`net.fabricmc.fabric-loom` plugin, since 26.1 ships unobfuscated.

One jar covers Minecraft 26.1 up to 26.2: every Minecraft call this mod makes exists unchanged
in both, and the client game tests pass against either. Building against another Minecraft
version is a matter of `minecraft_version` and the dependency versions in `gradle.properties`.

## Tests

```
./gradlew test              # domain, storage, gateways, check engine
./gradlew runClientGameTest # drives the screens and writes screenshots to run/screenshots
```

The client game tests open every screen, type into the autocomplete fields and record what the
game draws.

## Dependencies

Fabric API is required. Mod Menu and Cloth Config are optional: without them the entry in the
mod list and the settings screen are missing, everything else works. Modrinth serves its project
icons as webp, so a decoder for that format ships inside the jar.
