# Phase 2 Dedicated UI Plan

The phase 1 command path is intentionally backed by `GachaService` so the final block UI can call the same rule implementation.

## Target UX

- Player places a `Recycler Terminal` block.
- Right-click opens a screen similar to a crafting table.
- The screen has 9 cost slots, a roll button, and a prize preview/output area.
- The button is enabled only when all 9 cost slots contain the same non-empty item.
- Clicking the button consumes the 9 cost items, marks that item ID as used in world saved data, and gives 9 of a random prize item.
- The consumed item is excluded from that roll's prize pool.
- No server-wide chat broadcast is sent.

## Required server pieces

- `RecyclerTerminalBlock`
  - Opens the menu on use.
- `RecyclerTerminalBlockEntity`
  - Stores 9 input slots.
- `RecyclerTerminalMenu`
  - Owns slot validation and button handling on the server.
- `RecyclerTerminalScreen`
  - Client rendering only.
- A packet or menu button handler for the roll action.

## Acquisition must not disappear

The terminal must remain obtainable even after common resources have been consumed by the apocalypse rule.

Recommended options:

1. Keep an operator-accessible `/recycle give_terminal` command.
2. Add a permanent loot-free recipe based on a custom item that the mod grants via command or advancement.
3. Add the block to a creative/operator tab and document the command as the survival fallback.

For normal survival, option 1 is the most reliable because it does not depend on any world drop remaining available.

## Core logic extension

Add a new service method:

```java
GachaResult roll(ServerPlayer player, ItemStack costStack, Consumer<ItemStack> prizeSink)
```

The command path can pass the player's main-hand stack and inventory insert/drop sink. The block UI can pass a virtual cost stack assembled from the 9 input slots and an output-slot/inventory sink.

All prize emission must remain wrapped in `DropFilter.withBypass`.
