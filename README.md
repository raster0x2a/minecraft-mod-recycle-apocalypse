# RECYCLE APOCALYPSE

Minecraft Java Edition 26.1.2 / Fabric mod prototype.

Japanese README: [README.ja.md](README.ja.md)

## Installation

1. Install Fabric Loader for Minecraft Java Edition 26.1.2.
2. Put Fabric API `0.149.1+26.1.2` or a compatible 26.1.2 build into your `mods` folder.
3. Put this mod jar into your `mods` folder:

```text
build/libs/recycle-apocalypse-0.4.3.jar
```

Typical Windows client mods folder:

```text
%APPDATA%\.minecraft\mods
```

For multiplayer servers, install the same mod jar and Fabric API on both the server and every client. This version adds a custom block and GUI.

## Build requirements

- JDK 25
- Gradle 9.5.1+

This workspace includes local tool installs under `.tools/`. Load them with:

```sh
. tools/env.sh
```

Build command:

```sh
tools/build.sh
```

## GitHub Releases

Push a version tag such as `v0.4.3` to build the mod on GitHub Actions and attach the release jar to the matching GitHub Release.

The release jar excludes the `-sources.jar` artifact.

## Phase 1 behavior

- `/recycle give_table`
  - Gives the player a Gacha Table.
  - The command remains available so the table cannot become unobtainable.
- Gacha Table
  - Right-click the block to open a 3x3 input UI.
  - Place one item in each slot, using 9 different item types.
  - Duplicate and previously consumed items are rejected at input slots.
  - Press the roll button to consume the 9 input items and receive 9 random prize items.
  - Previously consumed items cannot be used as gacha materials again.
  - Press the used-items button to browse consumed items as item icons with page controls.
  - Successful rolls play sounds and particles.
- `/recycle used [page]`
  - Shows consumed item IDs that are no longer allowed to drop.

Used item IDs are saved in the overworld saved-data file under this mod's namespace.

## Drop rule

The mod filters `ItemEntity` insertion on the server. If the entity's item is marked as used, it is discarded before it enters the world. This covers drops from blocks, mobs, farming, and other world item spawns with one shared rule.

Gacha prizes are inserted with an explicit bypass so a prize stack is not deleted by the drop filter.

Crafting result slots cannot be picked up if the result item has already been consumed by the apocalypse rule.

## Planned dedicated UI

The Gacha Table implementation calls `GachaService`, keeping the core gacha rules separate from UI code.

Recommended final flow:

1. Add a `RecyclerTerminalBlock` with a block entity inventory of 9 input slots and 1 output/result area.
2. Open a server-backed screen handler from the block.
3. Enable the roll button only when the 9 input slots contain valid cost items.
4. On button click, call a service method equivalent to `GachaService`, consume the input slots, mark their item IDs, and insert/drop the prize with `DropFilter.withBypass`.
5. Keep a command such as `/recycle give_terminal` or a non-dropping recipe source so terminal acquisition cannot become impossible after world resources are consumed.
