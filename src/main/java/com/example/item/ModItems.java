package com.example.item;

import com.example.CustomDiscs;
import com.example.book.CustomWrittenBookItem;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.JukeboxSong;

public final class ModItems {
	public static final ResourceKey<JukeboxSong> R_UKRAINE_SONG_KEY = jukeboxSongKey("r_ukraine");
	public static final ResourceKey<JukeboxSong> ECHO_VILLAGER_SONG_KEY = jukeboxSongKey("echo_villager");
	public static final ResourceKey<JukeboxSong> SHAMAN_P_SONG_KEY = jukeboxSongKey("shaman_p");
	public static final ResourceKey<JukeboxSong> PARA_22_SONG_KEY = jukeboxSongKey("para-22");
	public static final ResourceKey<JukeboxSong> FOREST_SONG_KEY = jukeboxSongKey("forest");
	public static final ResourceKey<JukeboxSong> GOD_SONG_KEY = jukeboxSongKey("god");
	public static final ResourceKey<JukeboxSong> PODCAST_1_SONG_KEY = jukeboxSongKey("podcast-1");
	public static final ResourceKey<JukeboxSong> PODCAST_2_SONG_KEY = jukeboxSongKey("podcast-2");
	public static final ResourceKey<JukeboxSong> PODCAST_3_SONG_KEY = jukeboxSongKey("podcast-3");
	public static final ResourceKey<JukeboxSong> OPERA_SONG_KEY = jukeboxSongKey("opera");
	public static final ResourceKey<JukeboxSong> GOD2_SONG_KEY = jukeboxSongKey("god2");
	public static final ResourceKey<JukeboxSong> PRANKODIUS_SONG_KEY = jukeboxSongKey("prankodius");

	public static final Item R_UKRAINE_DISC = registerMusicDisc("r_ukraine_disc", R_UKRAINE_SONG_KEY);
	public static final Item ECHO_VILLAGER_DISC = registerMusicDisc("echo_villager_disc", ECHO_VILLAGER_SONG_KEY);
	public static final Item SHAMAN_P_DISC = registerMusicDisc("shaman_p_disc", SHAMAN_P_SONG_KEY);
	public static final Item PARA_22_DISC = registerMusicDisc("para-22_disc", PARA_22_SONG_KEY);
	public static final Item FOREST_DISC = registerMusicDisc("forest_disc", FOREST_SONG_KEY);
	public static final Item GOD_DISC = registerMusicDisc("god_disc", GOD_SONG_KEY);
	public static final Item PODCAST_1_DISC = registerMusicDisc("podcast-1_disc", PODCAST_1_SONG_KEY);
	public static final Item PODCAST_2_DISC = registerMusicDisc("podcast-2_disc", PODCAST_2_SONG_KEY);
	public static final Item PODCAST_3_DISC = registerMusicDisc("podcast-3_disc", PODCAST_3_SONG_KEY);
	public static final Item OPERA_DISC = registerMusicDisc("opera_disc", OPERA_SONG_KEY);
	public static final Item GOD2_DISC = registerMusicDisc("god2_disc", GOD2_SONG_KEY);
	public static final Item PRANKODIUS_DISC = registerMusicDisc("prankodius_disc", PRANKODIUS_SONG_KEY);
	public static final Item CHRONICLE_OF_DEPARTURE_BOOK = registerBook("chronicle_of_departure_book");
	public static final Item JUNGLE_TEMPLE_JOURNAL_BOOK = registerBook("jungle_temple_journal_book");
	public static final Item BONES_IN_HELL_BOOK = registerBook("bones_in_hell_book");
	public static final Item PRAYER_IN_THE_SAND_BOOK = registerBook("prayer_in_the_sand_book");
	public static final Item SEA_OF_TURTLES_BOOK = registerBook("sea_of_turtles_book");
	public static final Item BOOK_OF_THE_FOUR_BOOK = registerBook("book_of_the_four_book");
	public static final Item THE_HIDDEN_TRUTH_BOOK = registerBook("the_hidden_truth_book");
	public static final Item WHEAT_FOR_EVERYONE_BOOK = registerBook("wheat_for_everyone_book");
	public static final Item DECLARATION_OF_FREEDOM_BOOK = registerBook("declaration_of_freedom_book");
	public static final Item NEW_ZOMBICHI_CHRONICLE_BOOK = registerBook("new_zombichi_chronicle_book");
	public static final Item OCEAN_ECHOES_BOOK = registerBook("ocean_echoes_book");
	public static final Item SKEPTIC_OF_THE_GARDEN_BOOK = registerBook("skeptic_of_the_garden_book");
	public static final Item WEAKLAND_CAMPAIGN_BOOK = registerBook("weakland_campaign_book");
	public static final Item HISTORY_OF_DISAPPEARANCE_BOOK = registerBook("history_of_disappearance_book");
	public static final Item ALEH_JOURNAL_BOOK = registerBook("aleh_journal_book");

	private ModItems() {
	}

	public static void initialize() {
		ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.TOOLS_AND_UTILITIES).register(entries -> {
			entries.accept(R_UKRAINE_DISC);
			entries.accept(ECHO_VILLAGER_DISC);
			entries.accept(SHAMAN_P_DISC);
			entries.accept(PARA_22_DISC);
			entries.accept(FOREST_DISC);
			entries.accept(GOD_DISC);
			entries.accept(PODCAST_1_DISC);
			entries.accept(PODCAST_2_DISC);
			entries.accept(PODCAST_3_DISC);
			entries.accept(OPERA_DISC);
			entries.accept(GOD2_DISC);
			entries.accept(PRANKODIUS_DISC);
			entries.accept(CHRONICLE_OF_DEPARTURE_BOOK);
			entries.accept(JUNGLE_TEMPLE_JOURNAL_BOOK);
			entries.accept(BONES_IN_HELL_BOOK);
			entries.accept(PRAYER_IN_THE_SAND_BOOK);
			entries.accept(SEA_OF_TURTLES_BOOK);
			entries.accept(BOOK_OF_THE_FOUR_BOOK);
			entries.accept(THE_HIDDEN_TRUTH_BOOK);
			entries.accept(WHEAT_FOR_EVERYONE_BOOK);
			entries.accept(DECLARATION_OF_FREEDOM_BOOK);
			entries.accept(NEW_ZOMBICHI_CHRONICLE_BOOK);
			entries.accept(OCEAN_ECHOES_BOOK);
			entries.accept(SKEPTIC_OF_THE_GARDEN_BOOK);
			entries.accept(WEAKLAND_CAMPAIGN_BOOK);
			entries.accept(HISTORY_OF_DISAPPEARANCE_BOOK);
			entries.accept(ALEH_JOURNAL_BOOK);
		});
	}

	private static Item registerMusicDisc(String name, ResourceKey<JukeboxSong> songKey) {
		return Registry.register(
				BuiltInRegistries.ITEM,
				CustomDiscs.id(name),
				new Item(new Item.Properties().stacksTo(1).jukeboxPlayable(songKey))
		);
	}

	private static Item registerBook(String name) {
		return Registry.register(
				BuiltInRegistries.ITEM,
				CustomDiscs.id(name),
				new CustomWrittenBookItem(new Item.Properties().stacksTo(1))
		);
	}

	private static ResourceKey<JukeboxSong> jukeboxSongKey(String name) {
		return ResourceKey.create(Registries.JUKEBOX_SONG, CustomDiscs.id(name));
	}
}
