package com.example.book;

import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;

import java.util.List;
import java.util.Set;

public record BookDefinition(
		String id,
		String title,
		String author,
		Item item,
		float dropChance,
		Set<ResourceKey<LootTable>> lootTables,
		List<String> pages
) {
}
