package com.example.loot;

import com.example.item.ModItems;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.minecraft.core.registries.Registries;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.predicates.LootItemEntityPropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

import java.util.Set;

public final class ModLootTables {
	private static final float MUSIC_DISC_CHEST_CHANCE = 0.90F;
	private static final float STRONGHOLD_DISC_CHANCE = 0.90F;
	private static final float R_UKRAINE_DISC_CHANCE = 1.0F;
	private static final int R_UKRAINE_DISC_WEIGHT = 1;
	private static final float ECHO_VILLAGER_DISC_CHANCE = 1.0F;
	private static final int ECHO_VILLAGER_DISC_WEIGHT = 1;
	private static final float SHAMAN_P_DISC_CHANCE = 1.0F;
	private static final int SHAMAN_P_DISC_WEIGHT = 1;
	private static final float PARA_22_DISC_CHANCE = 1.0F;
	private static final int PARA_22_DISC_WEIGHT = 1;
	private static final float FOREST_DISC_CHANCE = 1.0F;
	private static final int FOREST_DISC_WEIGHT = 1;
	private static final float GOD_DISC_CHANCE = 1.0F;
	private static final int GOD_DISC_WEIGHT = 1;
	private static final float PODCAST_1_DISC_CHANCE = 1.0F;
	private static final int PODCAST_1_DISC_WEIGHT = 1;
	private static final float PODCAST_2_DISC_CHANCE = 1.0F;
	private static final int PODCAST_2_DISC_WEIGHT = 1;
	private static final float PODCAST_3_DISC_CHANCE = 1.0F;
	private static final int PODCAST_3_DISC_WEIGHT = 1;
	private static final float OPERA_DISC_CHANCE = 1.0F;
	private static final int OPERA_DISC_WEIGHT = 1;
	private static final float GOD2_DISC_CHANCE = 1.0F;
	private static final int GOD2_DISC_WEIGHT = 1;

	private static final MusicDiscLoot[] MUSIC_DISC_LOOT = {
			new MusicDiscLoot(ModItems.R_UKRAINE_DISC, R_UKRAINE_DISC_CHANCE, R_UKRAINE_DISC_WEIGHT),
			new MusicDiscLoot(ModItems.ECHO_VILLAGER_DISC, ECHO_VILLAGER_DISC_CHANCE, ECHO_VILLAGER_DISC_WEIGHT),
			new MusicDiscLoot(ModItems.SHAMAN_P_DISC, SHAMAN_P_DISC_CHANCE, SHAMAN_P_DISC_WEIGHT),
			new MusicDiscLoot(ModItems.PARA_22_DISC, PARA_22_DISC_CHANCE, PARA_22_DISC_WEIGHT),
			new MusicDiscLoot(ModItems.FOREST_DISC, FOREST_DISC_CHANCE, FOREST_DISC_WEIGHT),
			new MusicDiscLoot(ModItems.GOD_DISC, GOD_DISC_CHANCE, GOD_DISC_WEIGHT),
			new MusicDiscLoot(ModItems.PODCAST_1_DISC, PODCAST_1_DISC_CHANCE, PODCAST_1_DISC_WEIGHT),
			new MusicDiscLoot(ModItems.PODCAST_2_DISC, PODCAST_2_DISC_CHANCE, PODCAST_2_DISC_WEIGHT),
			new MusicDiscLoot(ModItems.PODCAST_3_DISC, PODCAST_3_DISC_CHANCE, PODCAST_3_DISC_WEIGHT),
			new MusicDiscLoot(ModItems.OPERA_DISC, OPERA_DISC_CHANCE, OPERA_DISC_WEIGHT),
			new MusicDiscLoot(ModItems.GOD2_DISC, GOD2_DISC_CHANCE, GOD2_DISC_WEIGHT)
	};

	private static final Set<ResourceKey<LootTable>> MUSIC_DISC_CHESTS = Set.of(
			chest("abandoned_mineshaft"),
			chest("ancient_city"),
			chest("nether_bridge"),
			chest("bastion_bridge"),
			chest("bastion_hoglin_stable"),
			chest("bastion_other"),
			chest("bastion_treasure"),
			chest("end_city_treasure"),
			chest("simple_dungeon"),
			chest("stronghold_corridor"),
			chest("stronghold_crossing"),
			chest("stronghold_library"),
			chest("woodland_mansion"),
			chest("village/village_armorer"),
			chest("village/village_butcher"),
			chest("village/village_cartographer"),
			chest("village/village_desert_house"),
			chest("village/village_fisher"),
			chest("village/village_fletcher"),
			chest("village/village_mason"),
			chest("village/village_plains_house"),
			chest("village/village_savanna_house"),
			chest("village/village_shepherd"),
			chest("village/village_snowy_house"),
			chest("village/village_taiga_house"),
			chest("village/village_tannery"),
			chest("village/village_temple"),
			chest("village/village_toolsmith"),
			chest("village/village_weaponsmith")
	);

	private static final Set<ResourceKey<LootTable>> STRONGHOLD_CHESTS = Set.of(
			chest("stronghold_corridor"),
			chest("stronghold_crossing"),
			chest("stronghold_library")
	);

	// Chests from the YUNG's Better mods (these structures replace their vanilla counterparts
	// and use their own loot tables, so vanilla loot injection never reaches them).
	private static final Set<ResourceKey<LootTable>> YUNG_CHESTS = Set.of(
			table("betterdeserttemples", "chests/tomb"),
			table("betterdeserttemples", "chests/tomb_pharaoh"),
			table("betterdeserttemples", "chests/pharaoh_hidden"),
			table("betterdeserttemples", "chests/library"),
			table("betterdeserttemples", "chests/lab"),
			table("betterdeserttemples", "chests/storage"),
			table("betterdeserttemples", "chests/food_storage"),
			table("betterdeserttemples", "chests/statue"),
			table("betterdeserttemples", "chests/wardrobe"),
			table("betterdeserttemples", "chests/pot"),
			table("betterjungletemples", "chests/treasure"),
			table("betterjungletemples", "chests/campsite"),
			table("betteroceanmonuments", "chests/upper_side_chamber"),
			table("betterfortresses", "chests/hall"),
			table("betterfortresses", "chests/keep"),
			table("betterfortresses", "chests/quarters"),
			table("betterfortresses", "chests/storage"),
			table("betterfortresses", "chests/beacon"),
			table("betterfortresses", "chests/worship"),
			table("betterfortresses", "chests/puzzle"),
			table("betterfortresses", "chests/obsidian"),
			table("betterfortresses", "chests/extra"),
			table("betterdungeons", "skeleton_dungeon/chests/common"),
			table("betterdungeons", "skeleton_dungeon/chests/middle"),
			table("betterdungeons", "small_dungeon/chests/loot_piles"),
			table("betterdungeons", "small_nether_dungeon/chests/common"),
			table("betterdungeons", "spider_dungeon/chests/egg_room"),
			table("betterdungeons", "zombie_dungeon/chests/common"),
			table("betterdungeons", "zombie_dungeon/chests/special"),
			table("betterdungeons", "zombie_dungeon/chests/tombstone"),
			table("betterwitchhuts", "chests/hut_0")
	);

	// Treasure / high-value chests from Structory, Terralith and Dungeons & Taverns (incl. nova_structures).
	// Discs only drop in the notable "treasure / vault / boss / main" chests of these mods, not every container.
	private static final Set<ResourceKey<LootTable>> TREASURE_CHESTS = Set.of(
			// Terralith
			table("terralith", "underground/chest"),
			// Structory
			table("structory", "harvest/manor2/treasure"),
			table("structory", "harvest/old_manor/treasure"),
			table("structory", "ruin/taiga/illager_treasure"),
			table("structory", "ruin/taiga/illager_high"),
			table("structory", "library/high"),
			// Dungeons & Taverns - vanilla structure overrides (parity with woodland mansion / nether fortress)
			chest("illager_mansion/generic"),
			chest("illager_mansion/secret_room"),
			chest("nether_fortress/fort_inside"),
			chest("nether_fortress/fort_inside_generic"),
			// Dungeons & Taverns - nova_structures
			table("nova_structures", "chests/dungeon_1"),
			table("nova_structures", "chests/dungeon_2"),
			table("nova_structures", "chests/dungeon_3"),
			table("nova_structures", "chests/dungeon_4"),
			table("nova_structures", "chests/dungeon_5"),
			table("nova_structures", "chests/dungeon_6"),
			table("nova_structures", "chests/dungeon_7"),
			table("nova_structures", "chests/conduit_ruin/conduit_ruin_big"),
			table("nova_structures", "chests/conduit_ruin/conduit_ruin_main"),
			table("nova_structures", "chests/creeping_crypt/vault_creeping"),
			table("nova_structures", "chests/desert_ruins/desert_ruin_main_temple"),
			table("nova_structures", "chests/desert_ruins/desert_ruin_lesser_treasure"),
			table("nova_structures", "chests/end_castle/greater_loot"),
			table("nova_structures", "chests/end_castle/treasure_lighthouse"),
			table("nova_structures", "chests/end_castle/vault_brigattine"),
			table("nova_structures", "chests/end_castle/vault_galleon"),
			table("nova_structures", "chests/end_castle/vault_slope"),
			table("nova_structures", "chests/end_lighthouse/vault_lighthouse"),
			table("nova_structures", "chests/hamlet/hamlet_tresure"),
			table("nova_structures", "chests/illager_hideout_tresure"),
			table("nova_structures", "chests/illager_hideout_lesser_tresure"),
			table("nova_structures", "chests/illager_hideout_heart_loot"),
			table("nova_structures", "chests/jungle_ruins/jungle_ruins_main_temple"),
			table("nova_structures", "chests/jungle_ruins/jungle_ruins_main_temple_wild"),
			table("nova_structures", "chests/lone_citadel/c_vault"),
			table("nova_structures", "chests/lone_citadel/c_vault_boss"),
			table("nova_structures", "chests/nether_keep/vault_keep"),
			table("nova_structures", "chests/nether_port/nether_port_chest"),
			table("nova_structures", "chests/nether_skeleton_tower/skeleton_tower_chest"),
			table("nova_structures", "chests/piglin_donjon/vault_piglin_donjon"),
			table("nova_structures", "chests/piglin_outstation/outstation_treasure"),
			table("nova_structures", "chests/piglin_outstation/vault_piglin_outstation"),
			table("nova_structures", "chests/pillager_outpost_treasure"),
			table("nova_structures", "chests/shrine/combat_treasure_1"),
			table("nova_structures", "chests/shrine/combat_treasure_2"),
			table("nova_structures", "chests/shrine/combat_treasure_3"),
			table("nova_structures", "chests/shrine/combat_treasure_4"),
			table("nova_structures", "chests/shrine/combat_treasure_5"),
			table("nova_structures", "chests/shrine/vault_shrine_ominous"),
			table("nova_structures", "chests/shrine/shrine_lesser_treasure"),
			table("nova_structures", "chests/stray_fort_tresure"),
			table("nova_structures", "chests/toxic_lair/toxic_boss_vault"),
			table("nova_structures", "chests/toxic_lair/toxic_ominous_vault"),
			table("nova_structures", "chests/toxic_lair/toxic_vault"),
			table("nova_structures", "chests/trident_trial_monument/ttm_boss_vault_heart"),
			table("nova_structures", "chests/trident_trial_monument/ttm_boss_vault_trident"),
			table("nova_structures", "chests/trident_trial_monument/ttm_common_vault")
	);

	// Gap structures (Dungeons & Taverns) with no themed match - discs only, mirrors ModBooks' overworld-general set.
	private static final Set<ResourceKey<LootTable>> GAP_CHESTS = Set.of(
			table("nova_structures", "chests/witch_villa/music_room"),
			table("nova_structures", "chests/witch_villa/library"),
			table("nova_structures", "chests/witch_villa/lab"),
			table("nova_structures", "chests/witch_villa/potion_brewing"),
			table("nova_structures", "chests/witch_villa/slime_room"),
			table("nova_structures", "chests/mangrove_witchhud"),
			table("nova_structures", "chests/badland_miner_outpost"),
			table("nova_structures", "chests/badland_miner_outpost_forge"),
			table("nova_structures", "chests/badland_miner_outpost_towers"),
			table("nova_structures", "chests/bunker_altar"),
			table("nova_structures", "chests/illager_camp"),
			table("nova_structures", "chests/firewatch_tower"),
			table("nova_structures", "chests/undead_crypts_grave"),
			table("nova_structures", "chests/tavern_quest"),
			table("nova_structures", "chests/village/village_birch_house")
	);

	private ModLootTables() {
	}

	public static void initialize() {
		LootTableEvents.MODIFY.register((key, tableBuilder, source, registries) -> {
			// Global: inject the music-disc pool into every chest loot table, regardless of
			// namespace or mod, so no container is ever missed (minus the blacklist).
			if (!source.isBuiltin() || !isInjectableChest(key, tableBuilder)) {
				return;
			}

			tableBuilder.pool(createDiscPool(MUSIC_DISC_CHEST_CHANCE).build());
		});

		LootTableEvents.MODIFY.register((key, tableBuilder, source, registries) -> {
			if (!source.isBuiltin() || !creeperLootTable().equals(key)) {
				return;
			}

			tableBuilder.pool(createCreeperDiscPool().build());
		});
	}

	private static float discChanceFor(ResourceKey<LootTable> key) {
		return STRONGHOLD_CHESTS.contains(key) ? STRONGHOLD_DISC_CHANCE : MUSIC_DISC_CHEST_CHANCE;
	}

	// Path fragments that mark a loot table as "not a real treasure chest" even if it somehow
	// uses the CHEST param set. A second line of defence on top of the param-set check.
	private static final Set<String> PATH_BLACKLIST = Set.of(
			"entity",
			"entities",
			"fishing",
			"archaeology",
			"blocks",
			"gameplay"
	);

	/**
	 * True if the loot table being modified is a chest-type table (uses the CHEST context param set).
	 * Works for any namespace/mod, because it inspects the table itself rather than a hard-coded list.
	 * Building the builder here is a cheap, one-time cost during datapack load.
	 */
	public static boolean isChestTable(LootTable.Builder builder) {
		return builder.build().getParamSet() == LootContextParamSets.CHEST;
	}

	/** True if the loot table's path contains a blacklisted fragment (mob/block/fishing/etc.). */
	public static boolean isBlacklisted(ResourceKey<LootTable> key) {
		String path = key.location().getPath();
		for (String fragment : PATH_BLACKLIST) {
			if (path.contains(fragment)) {
				return true;
			}
		}
		return false;
	}

	/** The single gate both disc and book injection use: a chest table that isn't blacklisted. */
	public static boolean isInjectableChest(ResourceKey<LootTable> key, LootTable.Builder builder) {
		return isChestTable(builder) && !isBlacklisted(key);
	}

	/** True if this mod injects a music-disc pool into the given loot table. */
	public static boolean isDiscChest(ResourceKey<LootTable> key) {
		return MUSIC_DISC_CHESTS.contains(key)
				|| YUNG_CHESTS.contains(key)
				|| TREASURE_CHESTS.contains(key)
				|| GAP_CHESTS.contains(key);
	}

	/** The configured disc drop chance for a chest, or 0 if the mod does not touch it. */
	public static float discChanceForChest(ResourceKey<LootTable> key) {
		return isDiscChest(key) ? discChanceFor(key) : 0.0F;
	}

	/** All chest loot tables that receive a music-disc pool from this mod. */
	public static Set<ResourceKey<LootTable>> affectedDiscChests() {
		Set<ResourceKey<LootTable>> all = new java.util.HashSet<>();
		all.addAll(MUSIC_DISC_CHESTS);
		all.addAll(YUNG_CHESTS);
		all.addAll(TREASURE_CHESTS);
		all.addAll(GAP_CHESTS);
		return all;
	}

	/** The music disc items that can roll out of a disc pool (all equally weighted). */
	public static java.util.List<Item> discItems() {
		java.util.List<Item> items = new java.util.ArrayList<>();
		for (MusicDiscLoot loot : MUSIC_DISC_LOOT) {
			items.add(loot.item());
		}
		return items;
	}

	private static LootPool.Builder createDiscPool(float chance) {
		LootPool.Builder pool = LootPool.lootPool()
				.setRolls(ConstantValue.exactly(1.0F))
				.when(LootItemRandomChanceCondition.randomChance(chance));

		for (MusicDiscLoot discLoot : MUSIC_DISC_LOOT) {
			pool.add(LootItem.lootTableItem(discLoot.item()).setWeight(discLoot.weight()));
		}

		return pool;
	}

	private static LootPool.Builder createCreeperDiscPool() {
		LootPool.Builder pool = LootPool.lootPool()
				.setRolls(ConstantValue.exactly(1.0F))
				.when(LootItemRandomChanceCondition.randomChance(MUSIC_DISC_CHEST_CHANCE))
				.when(LootItemEntityPropertyCondition.hasProperties(
						net.minecraft.world.level.storage.loot.LootContext.EntityTarget.ATTACKER,
						EntityPredicate.Builder.entity().of(EntityType.SKELETON)
				));

		for (MusicDiscLoot discLoot : MUSIC_DISC_LOOT) {
			pool.add(LootItem.lootTableItem(discLoot.item()).setWeight(discLoot.weight()));
		}

		return pool;
	}

	private static ResourceKey<LootTable> chest(String name) {
		return ResourceKey.create(Registries.LOOT_TABLE, ResourceLocation.fromNamespaceAndPath("minecraft", "chests/" + name));
	}

	private static ResourceKey<LootTable> table(String namespace, String path) {
		return ResourceKey.create(Registries.LOOT_TABLE, ResourceLocation.fromNamespaceAndPath(namespace, path));
	}

	private static ResourceKey<LootTable> creeperLootTable() {
		return ResourceKey.create(Registries.LOOT_TABLE, ResourceLocation.fromNamespaceAndPath("minecraft", "entities/creeper"));
	}

	private record MusicDiscLoot(Item item, float chance, int weight) {
	}
}
