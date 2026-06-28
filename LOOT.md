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

- 12 discs in `MUSIC_DISC_LOOT`, all weight 1 → uniform random pick.
- Injected into **every** injectable chest. One pool, 1 roll, gated by a random
  chance. Discs are **not** unique — they can repeat.
- Chance constant: `MUSIC_DISC_CHEST_CHANCE` = **0.07** (7%), everywhere.
- Creeper/skeleton-kill disc drop is a separate, keyed listener (entity table,
  unaffected by the global chest logic).
- The old per-structure disc chest sets were **deleted** — injection is global,
  so they were dead code. (Discs no longer have any hand-maintained list.)

## Books (`ModBooks`)

18 books total (`BOOKS`) — 5 themed + 13 general. Each book is **unique per
world**: it can drop **once ever**, tracked in `GeneratedBookState` (per-level
SavedData). The `UniqueBookLootFunction` picks a random *not-yet-generated*
matching book and marks it; if all matching books are already generated it
returns `EMPTY`.

Chances (`bookChanceFor(key)`):
- `THEMED_BOOK_CHANCE` = **0.10** (10%) — elemental books in their own themed
  places (desert / jungle / ocean), detected by `isThemedChest(key)`.
- `BOOK_CHEST_CHANCE` = **0.07** (7%) — the default: stronghold, villages,
  dungeons, the global fallback, **and the nether** (the nether book is
  explicitly excluded from `isThemedChest`, so it uses the default).

`isThemedChest(key)` = the key is in an elemental book's own `lootTables`, except
`bones_in_hell` (nether stays default). Stronghold is never a themed chest, so it
falls through to the default. (The old `STRONGHOLD_BOOK_CHANCE` and
`RARE_BIOME_BOOK_CHANCE`/`RARE_BIOME_CHESTS` were removed — both equalled the
default, so they were no-ops.)

### Which books drop where — `matchingBooks(key)`

A book matches a chest if any of:
1. `book.lootTables().contains(key)` — its own themed chest list.
2. `strongholdChest && !bones_in_hell` — **stronghold is the backup**: all books
   except the nether one. `isStrongholdChest` matches by path
   (`path.contains("stronghold")`), so it works for vanilla **and** Stellarity
   (`stellarity:stronghold/*`) and any other mod that overhauls strongholds.
3. `generalChest && canSpawnInSimpleDungeon(book)` — general dungeons get only
   non-themed books.
4. `overworldGeneralChest && isOverworldGeneral(book)` — gap/overworld-general
   chests get only non-themed books.

If `matchingBooks(key)` is **empty** (an unlisted/modded chest), the MODIFY
listener falls back to `generalOverworldBooks()` (the 13 non-themed books) at
`BOOK_CHEST_CHANCE`. So **every** chest has books; themed books never leak via
the fallback.

### Themed ("elemental") books — exclusivity

`canSpawnInSimpleDungeon` and `isOverworldGeneral` both exclude the same 5 themed
books, so themed books **never** drop in dungeons or generic modded chests:
`bones_in_hell`, `sea_of_turtles`, `ocean_echoes`, `jungle_temple_journal`,
`prayer_in_the_sand`. Every other book is a general "lore" book that drops
everywhere except the themed elemental chests.

Backups = stronghold **and** ancient city. Both host all books except the nether
one (`backupChest && !bones_in_hell` in `matchingBooks`). `my_struggle`
(«Моя боротьба», Minecramet), `on_roads_and_stone` («Про Дороги і Камінь»,
PARA_22) and `on_gardens_and_time` («Про Сади і Час», Sleepwalking) are ordinary
general books (declare `VILLAGE_CHESTS`, not excluded from any set), so they drop
like the other lore books — villages, dungeons, the global fallback and the
backups — just not in the elemental chests.

| Book | id | Where it can drop |
|---|---|---|
| Кістки в Пеклі (nether) | `bones_in_hell` | nether chests only (NOT backups) |
| Молитва Серед Пісків (desert) | `prayer_in_the_sand` | desert chests + backups |
| Записки з Південних Джунглів (jungle) | `jungle_temple_journal` | jungle chests + backups |
| Море Черепах (ocean) | `sea_of_turtles` | ocean chests + backups |
| Знахідка на Дні (ocean) | `ocean_echoes` | ocean chests + backups |

The 13 non-themed/"lore" books drop in: villages + mansions (woodland_mansion,
illager_mansion/*), the backups (stronghold + ancient city), general dungeons,
and as the global fallback.

### Notable manual sets in `ModBooks`

- `VILLAGE_CHESTS` — includes `woodland_mansion`, `illager_mansion/generic`,
  `illager_mansion/secret_room` (mansions share the village book pool).
- Strongholds are detected by path (`isStrongholdChest`), not a set.
- Ancient city is a backup (`isAncientCityChest`, path match) — all books except
  nether, at a rarer chance. Not a themed chest.
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
+ books automatically. Stellarity strongholds (`stellarity:stronghold/*`) are
recognised by `isStrongholdChest` (path match), so they correctly get the full
stronghold book set (all books except the nether one), same as vanilla.

## Test command (currently DISABLED)

`com.example.command.DiscStatsCommand` — `/discstats chest <id> [n]` only
(default 10000 rolls). Rolls the real loot table to measure disc/book drop rates;
snapshots and restores `GeneratedBookState` so it never consumes books from the
world. The old `all` mode was removed (with global injection, "all" = every chest
in the game, so there is no list to iterate).

Registration is commented out in `CustomDiscs.onInitialize()`. Re-enable the two
commented lines to use it.

## Chances (current / production)

- Discs: `MUSIC_DISC_CHEST_CHANCE` = **0.07** (every chest).
- Themed books in themed places: `THEMED_BOOK_CHANCE` = **0.10**.
- All other books (stronghold / villages / dungeons / fallback / nether):
  `BOOK_CHEST_CHANCE` = **0.07**.
- Ancient city: `ANCIENT_CITY_BOOK_CHANCE` = **0.06** (a second backup like
  stronghold — all books except nether — but rarer).

To re-enable easy in-game testing, bump these constants up (e.g. 0.90) and
rebuild.

## Key files

| File | Role |
|---|---|
| `CustomDiscs.java` | mod init; registers items, loot, books; (disabled) command |
| `item/ModItems.java` | 12 discs + 18 book items |
| `loot/ModLootTables.java` | global disc injection, `isInjectableChest`, blacklist |
| `book/ModBooks.java` | book matching/theming, stronghold + global fallback |
| `book/UniqueBookLootFunction.java` | picks a random unused book, marks it generated |
| `book/GeneratedBookState.java` | per-world set of generated book ids (+ snapshot/restore) |
| `command/DiscStatsCommand.java` | disabled drop-rate test command |
