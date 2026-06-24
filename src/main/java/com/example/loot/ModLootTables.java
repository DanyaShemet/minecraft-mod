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
	private static final float MUSIC_DISC_CHEST_CHANCE = 0.07F;
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
	private static final float PRANKODIUS_DISC_CHANCE = 1.0F;
	private static final int PRANKODIUS_DISC_WEIGHT = 1;

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
			new MusicDiscLoot(ModItems.GOD2_DISC, GOD2_DISC_CHANCE, GOD2_DISC_WEIGHT),
			new MusicDiscLoot(ModItems.PRANKODIUS_DISC, PRANKODIUS_DISC_CHANCE, PRANKODIUS_DISC_WEIGHT)
	};

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

	private static ResourceKey<LootTable> creeperLootTable() {
		return ResourceKey.create(Registries.LOOT_TABLE, ResourceLocation.fromNamespaceAndPath("minecraft", "entities/creeper"));
	}

	private record MusicDiscLoot(Item item, float chance, int weight) {
	}
}
