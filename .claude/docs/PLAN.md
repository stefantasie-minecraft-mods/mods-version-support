# Mods Version Support — Implementation Plan

A client-side Fabric mod for Minecraft 26.1.2. It answers one question: if I move my instance
to a newer Minecraft version, which of my installed mods come along?

Mod id `mods-version-support`, package `de.stefantasie.modsversionsupport`, display name
Mods Version Support.

## Verified groundwork

These were checked against the live APIs and the local instance on 2026-08-20, not assumed.

| Fact | Value |
| --- | --- |
| Minecraft | 26.1.2, first unobfuscated release line, Mojang names only |
| Yarn mappings | stop at 1.21.11, unusable here |
| Build | `net.fabricmc.fabric-loom` (the non-remapping plugin), Gradle 9.4+, Java 25+ |
| Loader in test instance | Fabric Loader 0.19.3 |
| Fabric API for 26.1.2 | 0.155.2+26.1.2 (installed), newer builds target 26.2/26.3 |
| Also installed | Cloth Config 26.1.154, Mod Menu 18.0.0, Placeholder API 3.0.0+26.1 |
| Mojang version list | `piston-meta.mojang.com/mc/game/version_manifest_v2.json`, the source Prism reads |
| Modrinth identification | `POST /v2/version_files` with SHA-1 resolved all four installed jars |
| Modrinth limits | 300 requests per minute, unique User-Agent mandatory |
| Modrinth icons | mostly `.webp`; `NativeImage` reads PNG only, so a decode step is unavoidable |

## Decisions

Settled with Stefantasie before implementation started.

- The percentage counts every selected mod. A jar Modrinth does not know counts as unsupported;
  deselecting it is the way out.
- Release channels are graded. A release counts as supported and paints green, beta and alpha
  count as supported as well but paint amber, and the detail screen names the channel.
- Opening the overview starts a check for every profile, served from the response cache. A
  refresh action bypasses the cache.
- The bar carries a red-to-green gradient across its full width and is clipped at the
  percentage, so a low value ends inside the red part.
- Entry points: Mod Menu, a key binding without a default key, and the client command
  `/modsversionsupport`.
- The version picker lists releases; snapshots appear once the setting is enabled.
- Both `fabric` and `quilt` count as matching loaders, with quilt-only results marked in the
  detail screen.
- A development entry point behind a system property drives the screens and writes screenshots,
  used to verify the UI against this plan.

## Libraries

- **Fabric API** — required, already present. Key bindings, client lifecycle, client commands.
- **Mod Menu** — entry point from the mod list, plus the badge. Implemented through
  `ModMenuApi` as an optional dependency so the mod still loads without it.
- **Cloth Config** — the settings screen only (concurrency, cache lifetime, whether snapshots
  appear in the version picker, User-Agent contact string).
- **Gson** — ships with Minecraft, used for both the API responses and the profile file.
- **`java.net.http.HttpClient`** — in the JDK, no dependency needed.
- **TwelveMonkeys `imageio-webp`** — jar-in-jar, decodes Modrinth's webp icons. It has known
  trouble with some lossy+alpha files, so the loader falls back to a placeholder icon rather
  than failing a row.
- **Placeholder API is not used.** It fills text placeholders in server-side messages and has
  nothing to contribute to a client GUI. It stays installed in the test instance, that is all.

## Domain model

**Version profile** — one entry in the overview. Holds a generated id, an editable display
name, the target Minecraft version, the set of selected mods, its position in the list, and
the last result. The name is not an identity: the default is the version number, and a second
profile for the same version gets a counted suffix, while a name typed by hand may repeat
freely.

**Mod entry** — a mod that a profile checks. Either resolved from the installed jar (mod id,
display name, file name, icon from inside the jar, SHA-1) or added by hand from Modrinth
search (project id, title, icon URL, no local file).

**Support state** per mod and target version: `SUPPORTED`, `UNSUPPORTED`, `NOT_ON_MODRINTH`,
`FAILED`, and `PENDING` while the check runs. The percentage on the overview row is supported
divided by resolvable, and unresolvable mods are reported separately instead of quietly
counting as failures.

## Package layout

Feature first, layers inside the feature, split down to one responsibility per class.

```
de.stefantasie.modsversionsupport
├─ entrypoint/            client initializer, keybinding, client command
├─ platform/
│  ├─ installed/          scanner over FabricLoader, jar hashing, jar icon extraction
│  └─ paths/              config directory, cache directory
├─ mojang/
│  └─ versions/           manifest gateway, game version record, release-type filter, catalog
├─ modrinth/
│  ├─ http/               client, user agent, rate limiter, retry on 429, json decoding
│  ├─ hash/               batch lookup of installed jars
│  ├─ project/            version query per project and game version
│  ├─ search/             query building, paging, search hit record
│  └─ cache/              in-memory and on-disk response cache with a lifetime
├─ domain/
│  ├─ profile/            profile, name generation, ordered profile list, move up and down
│  ├─ selection/          selected mods, select all, clear, remove, add
│  └─ report/             support state, per-mod result, ratio, progress snapshot
├─ check/                 per-profile async run, scheduling, progress publishing, cancelling
├─ storage/               profile file reading and writing, codecs, migration
├─ config/                Cloth Config definition and accessors
└─ ui/
   ├─ screen/
   │  ├─ overview/        profile rows with spinner, percentage, traffic-light bar, arrows
   │  ├─ editor/          create and edit a profile
   │  └─ detail/          per-mod availability for one profile, sortable
   ├─ widget/
   │  ├─ autocomplete/    text field with a suggestion popup, one provider per source
   │  ├─ list/            checkable rows, toolbar actions
   │  ├─ progress/        spinner, percentage label, red-to-green bar
   │  └─ icon/            icon widget
   ├─ icon/               texture registry, jar loader, remote loader, webp decoding, fallback
   └─ modmenu/            config screen factory
```

## Screens

### Overview

The list of profiles. Each row shows the display name, the Minecraft version, and once the
check finished, the percentage plus a bar that runs from red on the left to green on the
right in proportion to the result. While a check is running the row is greyed out, carries a
spinner, and the bar doubles as the progress indicator for how many mods have been asked
about so far. Editing, deleting, and reordering stay available during the run. Rows move with
up and down arrows. A plus button creates a profile, and a newly created profile appears in
the running state immediately.

### Editor

Creating and editing one profile.

- Target version through a dropdown and through an autocomplete field over the same catalog.
  Older versions are searchable; snapshots are behind a setting.
- Display name, prefilled with the version and free to change.
- The mod list, each row with its icon, display name, and file name, with a checkbox.
  Toolbar: select all, clear selection, remove selected, remove all.
- A Modrinth search field with autocomplete showing icon and title, adding a hit to the list.
- Once a check has run, every row also carries its support state for the target version and
  the list can be sorted by availability, so the same information is at hand while editing.

### Detail

Opened from a finished profile row. Lists every checked mod with its state, so it is visible
at a glance which mods already have a build for the target version and which do not. Sortable
by availability, by name, and by source, with the sort choice remembered per profile.

## Checking

Each profile runs its own asynchronous job on a shared bounded executor, so several profiles
can run at once without flooding Modrinth. Installed mods are resolved in one batch hash
request. Per mod, the check asks Modrinth for versions of that project matching the target
game version and the fabric loader, and a non-empty answer means supported. Responses are
cached per project and version for a configurable lifetime so that a second profile on the
same version costs almost nothing. A token-bucket limiter keeps request volume under 300 per
minute and a 429 triggers a backoff rather than an error row. Progress is published as a
snapshot the UI polls on the render thread; no network callback ever touches a widget
directly.

## Persistence

`config/mods-version-support/profiles.json` holds the ordered profiles with their selections
and last results. Icons downloaded from Modrinth are cached as PNG under the same directory
after decoding, which keeps the webp dependency out of the render path. A schema version field
is written from the start.

## Phases

1. **Bootstrap.** Gradle build with the new Loom plugin, `fabric.mod.json`, client entrypoint,
   `runClient` working against 26.1.2, plus a task that installs the built jar into the Prism
   instance.
2. **Domain and storage.** Profiles, naming, ordering, selection, JSON round-trip, unit tests.
   No Minecraft classes involved.
3. **Version catalog.** Mojang manifest gateway, filtering, sorting, caching.
4. **Modrinth gateway.** HTTP client with User-Agent and limiter, hash lookup, project version
   query, search, response cache. Tests run against recorded fixtures.
5. **Check engine.** Async runs, progress, cancellation, error handling.
6. **Overview screen.** Rows, spinner, traffic-light bar, reordering, Mod Menu entry, keybind.
7. **Editor screen.** Version picker with dropdown and autocomplete, mod list with icons and
   bulk actions, Modrinth search with autocomplete.
8. **Detail screen.** Availability per mod with sorting.
9. **Icons.** Jar extraction, remote download, webp decode with fallback, texture lifecycle.
10. **Settings and translations.** Cloth Config screen, `en_us.json` and `de_de.json`.
11. **Hardening.** Offline behaviour, rate-limit behaviour, large mod lists, Windows path
    handling, final pass over the code against the guidelines.

## Open risks

- The webp decoder is the weakest link. If TwelveMonkeys chokes on real Modrinth icons, the
  fallback keeps the UI usable, and a second decoder can be swapped in behind the same
  interface.
- Fabric API for 26.1.2 is at 0.155.2 while newer builds target 26.2 and 26.3. The build pins
  the 26.1.2 line.
- Autocomplete is not a vanilla widget. It is built from an `EditBox` plus a suggestion list
  rendered above the following widgets, which needs care with focus and z-order.
- Modrinth marks support per project version, and some mods list a version as supported that
  in practice needs a different loader build. The check reports what Modrinth states.
