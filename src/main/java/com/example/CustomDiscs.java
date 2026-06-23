package com.example;

import com.example.command.DiscStatsCommand;
import com.example.item.ModItems;
import com.example.book.ModBooks;
import com.example.loot.ModLootTables;
import com.example.sound.ModSoundEvents;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

import net.minecraft.resources.ResourceLocation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CustomDiscs implements ModInitializer {
	public static final String MOD_ID = "custom-discs";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		ModSoundEvents.initialize();
		ModItems.initialize();
		ModLootTables.initialize();
		ModBooks.initialize();

		// Test-only command for measuring disc/book drop rates. Disabled so it cannot be
		// invoked in-game; re-enable this line when you need to run /discstats.
		// CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
		// 		DiscStatsCommand.register(dispatcher));

		LOGGER.info("Loaded custom music discs");
	}

	public static ResourceLocation id(String path) {
		return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
	}
}
