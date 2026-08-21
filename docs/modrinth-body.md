# Mods Version Support

Before you move an instance to a newer Minecraft version, this mod tells you which of your mods
would come along.

## What it does

Add a **version entry**: a target Minecraft version plus the mods to check against it. The same
Minecraft version may appear in several entries with different mod sets, so you can compare
"everything I have" against "the ones I actually need".

Each entry is checked against Modrinth in the background. While it runs the row is greyed out and
shows a grey progress bar; once it finishes the bar turns into a red-amber-green gradient with the
share of mods that are ready.

Open an entry and you see every mod with its verdict — ready, prerelease, not yet, or unknown to
Modrinth — and the newest Minecraft version that mod still reaches. That last column is the useful
one for mods that are behind: you see at a glance whether a mod stopped at 1.21.11 or is only one
release short. Sort by availability, name or source.

Mods you have not installed can be added through a Modrinth search with autocomplete and icons,
which makes it easy to check a mod before you download it.

## Notes

- Client-side only. Nothing is sent anywhere except read-only requests to Modrinth and Mojang's
  version manifest.
- Minecraft versions come from the same manifest a launcher reads, so snapshots and older releases
  are available too.
- Open it through Mod Menu, the client command `/modsversionsupport`, or a key you bind yourself.
- Fabric API is required. Mod Menu and Cloth Config are optional.
