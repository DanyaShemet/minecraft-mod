# Custom Discs

Small Fabric mod for custom music discs on Minecraft 1.21.1.

## Add a New Disc

1. Put audio in `src/main/resources/assets/custom-discs/sounds/records/my_track.ogg`.
2. Put texture in `src/main/resources/assets/custom-discs/textures/item/my_track_disc.png`.
3. Add an item model in `src/main/resources/assets/custom-discs/models/item/my_track_disc.json`.
4. Add the sound to `src/main/resources/assets/custom-discs/sounds.json`.
5. Add a jukebox song JSON in `src/main/resources/data/custom-discs/jukebox_song/my_track.json`.
6. Register the item in `ModItems`, sound in `ModSoundEvents`, and loot chance in `ModLootTables`.

Change `EXAMPLE_TRACK_DISC_CHANCE` in `ModLootTables` to tune the chest drop chance.
