# Loot System Reference

Quick-recall notes for how this mod (`custom-discs`) injects its music discs and
unique books into chests. Read this before touching loot logic.

## TL;DR

- Loot is injected **globally**: every chest-type loot table in the game (any
  namespace / any mod) gets a disc pool **and** a book pool. There is **no
  hand-maintained allowlist** deciding *whether* loot appears.
- The injection gate is `ModLootTables.isInjectableChest(key, builder)` =
  `isChestTable(builder) && !isBlacklisted(key)`.
- Manual lists still exist, but only to control **which book** drops where
  (theming), not whether loot appears.
- Discs are fully uniform everywhere. Books are themed where known, with a
  general fallback everywhere else.

## How injection works

Both `ModLootTables.initialize()` and `ModBooks.initialize()` register a
`net.fabricmc.fabric.api.loot.v3.LootTableEvents.MODIFY` listener.

Gate (shared, in `ModLootTables`):

```java
isInjectableChest(key, tableBuilder)
  = isChestTable(tableBuilder)        // builder.build().getParamSet() == LootContextParamSets.CHEST
 && !isBlacklisted(key)               // path does not contain a blacklist fragment
```

- **Why param set, not path?** A chest table is detected by its `CHEST` context
  param set, read via `tableBuilder.build().getParamSet()`. Fabric's MODIFY
  builder preserves the param set (`FabricLootTableBuilder.copyOf` copies it).
  This catches every real container across all mods and auto-skips mob/block/
  fishing/archaeology/etc. tables (different param sets). It also skips junk like
  Stellarity's `{}` `end_city_treasure` override (its type defaults to non-chest).
- **Blacklist** (`ModLootTables.PATH_BLACKLIST`) is a second line of defence for
  tables that wrongly use the CHEST param set. Fragments (substring match on
  `key.location().getPath()`): `entity`, `entities`, `fishing`, `archaeology`,
  `blocks`, `gameplay`. Add fragments here (one line) if loot shows up somewhere
  unwanted.
- All listeners also require `source.isBuiltin()` (vanilla + mod resources;
  excludes external user datapacks).

## Discs (`ModLootTables`)

- 11 discs in `MUSIC_DISC_LOOT`, all weight 1 → uniform random pick.
- Injected into **every** injectable chest. One pool, 1 roll, gated by a random
  chance. Discs are **not** unique — they can repeat.
- Chance constant: `MUSIC_DISC_CHEST_CHANCE` (also `STRONGHOLD_DISC_CHANCE`,
  same value). **Currently 0.90 (TEST). Production value was 0.07.**
- Creeper/skeleton-kill disc drop is a separate, keyed listener (entity table,
  unaffected by the global chest logic).
- The old disc chest sets (`MUSIC_DISC_CHESTS`, `YUNG_CHESTS`, `TREASURE_CHESTS`,
  `GAP_CHESTS`, `STRONGHOLD_CHESTS`) **no longer drive disc injection** (it's
  global). They remain only for the (disabled) test command's accessors.

## Books (`ModBooks`)

13 books total (`BOOKS`). Each book is **unique per world**: it can drop **once
ever**, tracked in `GeneratedBookState` (per-level SavedData). The
`UniqueBookLootFunction` picks a random *not-yet-generated* matching book and
marks it; if all matching books are already generated it returns `EMPTY`.

Chances: `BOOK_CHEST_CHANCE`, `RARE_BIOME_BOOK_CHANCE`, `STRONGHOLD_BOOK_CHANCE`.
**Currently all 0.90 (TEST). Production value was 0.09.**

### Which books drop where — `matchingBooks(key)`

A book matches a chest if any of:
1. `book.lootTables().contains(key)` — its own themed chest list.
2. `strongholdChest && !bones_in_hell` — **stronghold is the backup**: all books
   except the nether one.
3. `generalChest && canSpawnInSimpleDungeon(book)` — general dungeons get only
   non-themed books.
4. `overworldGeneralChest && isOverworldGeneral(book)` — gap/overworld-general
   chests get only non-themed books.

If `matchingBooks(key)` is **empty** (an unlisted/modded chest), the MODIFY
listener falls back to `generalOverworldBooks()` (the 8 non-themed books) at
`BOOK_CHEST_CHANCE`. So **every** chest has books; themed books never leak via
the fallback.

### Themed ("elemental") books — exclusivity

`canSpawnInSimpleDungeon` and `isOverworldGeneral` both exclude the same 5 themed
books, so themed books **never** drop in dungeons or generic modded chests:
`bones_in_hell`, `sea_of_turtles`, `ocean_echoes`, `jungle_temple_journal`,
`prayer_in_the_sand`.

| Book | id | Where it can drop |
|---|---|---|
| Кістки в Пеклі (nether) | `bones_in_hell` | nether chests only (NOT stronghold) |
| Молитва Серед Пісків (desert) | `prayer_in_the_sand` | desert chests + stronghold |
| Записки з Південних Джунглів (jungle) | `jungle_temple_journal` | jungle chests + `ancient_city` + stronghold |
| Море Черепах (ocean) | `sea_of_turtles` | ocean chests + `ancient_city` + stronghold |
| Знахідка на Дні (ocean) | `ocean_echoes` | ocean chests + `ancient_city` + stronghold |

The 8 non-themed/"lore" books drop in: villages + mansions (woodland_mansion,
illager_mansion/*), strongholds, general dungeons, and as the global fallback.

### Notable manual sets in `ModBooks`

- `VILLAGE_CHESTS` — includes `woodland_mansion`, `illager_mansion/generic`,
  `illager_mansion/secret_room` (mansions share the village book pool).
- `STRONGHOLD_CHESTS` — vanilla stronghold corridor/crossing/library.
- ocean books' `lootTables` include `chests/ancient_city`; jungle book too.
- Witch villa / mangrove are NOT ocean chests anymore (they get general books via
  `OVERWORLD_GENERAL_CHESTS`).

## Stellarity interaction

Stellarity overhauls End + strongholds:
- Overrides `minecraft:worldgen/structure/stronghold` to a jigsaw using
  `stellarity:stronghold/*` loot tables → vanilla `minecraft:chests/stronghold_*`
  are unused in Stellarity worlds.
- Empties `minecraft:chests/end_city_treasure` to `{}`; its End cities use
  `stellarity:end_city/*`.

Because injection is **global**, Stellarity's own chest tables still receive discs
+ books automatically. Caveat: Stellarity strongholds are "unlisted" chests, so
they get the **general fallback books**, not the full stronghold set. To give them
the full set, add `stellarity:stronghold/*` keys to `STRONGHOLD_CHESTS`.

## Test command (currently DISABLED)

`com.example.command.DiscStatsCommand` — `/discstats chest <id> [n]` and
`/discstats all [n]`. Rolls real loot tables to measure drop rates; snapshots and
restores `GeneratedBookState` so it never consumes books from the world.

Registration is commented out in `CustomDiscs.onInitialize()`. Re-enable the two
commented lines to use it. Note its disc accessors are now stale (discs are
global, not list-based).

## ⚠️ Before release

Restore production chances:
- `ModLootTables`: `MUSIC_DISC_CHEST_CHANCE`, `STRONGHOLD_DISC_CHANCE` → `0.07`.
- `ModBooks`: `BOOK_CHEST_CHANCE`, `RARE_BIOME_BOOK_CHANCE`,
  `STRONGHOLD_BOOK_CHANCE` → `0.09`.

(They are all temporarily `0.90` for in-game testing.)

## Key files

| File | Role |
|---|---|
| `CustomDiscs.java` | mod init; registers items, loot, books; (disabled) command |
| `item/ModItems.java` | 11 discs + 13 book items |
| `loot/ModLootTables.java` | global disc injection, `isInjectableChest`, blacklist |
| `book/ModBooks.java` | book matching/theming, stronghold + global fallback |
| `book/UniqueBookLootFunction.java` | picks a random unused book, marks it generated |
| `book/GeneratedBookState.java` | per-world set of generated book ids (+ snapshot/restore) |
| `command/DiscStatsCommand.java` | disabled drop-rate test command |
