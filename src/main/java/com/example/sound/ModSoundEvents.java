package com.example.sound;

import com.example.CustomDiscs;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;

public final class ModSoundEvents {
	public static final SoundEvent R_UKRAINE = register("music_disc.r_ukraine");
	public static final SoundEvent ECHO_VILLAGER = register("music_disc.echo_villager");
	public static final SoundEvent SHAMAN_P = register("music_disc.shaman_p");
	public static final SoundEvent PARA_22 = register("music_disc.para-22");
	public static final SoundEvent FOREST = register("music_disc.forest");
	public static final SoundEvent GOD = register("music_disc.god");
	public static final SoundEvent PODCAST_1 = register("music_disc.podcast-1");
	public static final SoundEvent PODCAST_2 = register("music_disc.podcast-2");
	public static final SoundEvent PODCAST_3 = register("music_disc.podcast-3");
	public static final SoundEvent OPERA = register("music_disc.opera");
	public static final SoundEvent GOD2 = register("music_disc.god2");
	public static final SoundEvent PRANKODIUS = register("music_disc.prankodius");

	private ModSoundEvents() {
	}

	public static void initialize() {
	}

	private static SoundEvent register(String name) {
		return Registry.register(
				BuiltInRegistries.SOUND_EVENT,
				CustomDiscs.id(name),
				SoundEvent.createVariableRangeEvent(CustomDiscs.id(name))
		);
	}
}
