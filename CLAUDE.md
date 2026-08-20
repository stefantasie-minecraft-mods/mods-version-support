# Mods Version Support

Client-side Fabric mod for Minecraft 26.1.2 that reports which installed mods already support
a newer Minecraft version, using Modrinth as the source.

## Rules that override defaults

- [Clean Code rules](.claude/guidelines/clean-code.md) — binding for every file.
- [Language policy](.claude/guidelines/language-policy.md) — chat German, files English.
- [Human-authored style](.claude/guidelines/human-authored-style.md) — read before writing
  prose, code, or commit messages.

## Commits

Conventional Commits, short subject, no AI attribution and no trailers.
One commit per plan step.

## Plan

[.claude/docs/PLAN.md](.claude/docs/PLAN.md)

## Environment

- Build: `net.fabricmc.fabric-loom`, Gradle 9.4+, Java 25+, Mojang names, no remapping.
- Test instance: `~/Library/Application Support/PrismLauncher/instances/26.1.2`.
- Modrinth needs a unique User-Agent and stays under 300 requests per minute.
