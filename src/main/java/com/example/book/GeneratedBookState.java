package com.example.book;

import com.example.CustomDiscs;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.HashSet;
import java.util.Set;

public class GeneratedBookState extends SavedData {
	private static final String DATA_NAME = CustomDiscs.MOD_ID + "_generated_books";
	private static final SavedData.Factory<GeneratedBookState> FACTORY = new SavedData.Factory<>(
			GeneratedBookState::new,
			GeneratedBookState::load,
			DataFixTypes.LEVEL
	);

	private final Set<String> generatedBookIds = new HashSet<>();

	public static GeneratedBookState get(ServerLevel level) {
		// Always store on the Overworld's data storage so the "generated books" set is truly global
		// (per-world), not per-dimension. Otherwise the first-book gate would check the Nether's own
		// state — where chronicle_of_departure can never be generated — and nether books would never
		// drop. Using the overworld storage also makes every book genuinely unique across the world.
		return level.getServer().overworld().getDataStorage().computeIfAbsent(FACTORY, DATA_NAME);
	}

	public static GeneratedBookState load(CompoundTag tag, HolderLookup.Provider registries) {
		GeneratedBookState state = new GeneratedBookState();
		ListTag ids = tag.getList("generated", Tag.TAG_STRING);
		for (int i = 0; i < ids.size(); i++) {
			state.generatedBookIds.add(ids.getString(i));
		}
		return state;
	}

	@Override
	public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
		ListTag ids = new ListTag();
		for (String id : generatedBookIds) {
			ids.add(StringTag.valueOf(id));
		}
		tag.put("generated", ids);
		return tag;
	}

	public boolean markGenerated(String id) {
		if (!generatedBookIds.add(id)) {
			return false;
		}

		setDirty();
		return true;
	}

	public boolean isGenerated(String id) {
		return generatedBookIds.contains(id);
	}

	/**
	 * Returns a copy of the currently generated book ids. Used by the /discstats test command
	 * to take a snapshot before a dry-run simulation, so the world state can be restored afterwards.
	 */
	public Set<String> snapshot() {
		return new HashSet<>(generatedBookIds);
	}

	/**
	 * Restores the generated book set to a previously taken {@link #snapshot()}.
	 * This lets the test command roll the real book loot tables without permanently
	 * marking books as generated in the world.
	 */
	public void restore(Set<String> snapshot) {
		generatedBookIds.clear();
		generatedBookIds.addAll(snapshot);
		setDirty();
	}
}
