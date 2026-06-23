package com.example.command;

import com.example.CustomDiscs;
import com.example.book.GeneratedBookState;
import com.example.book.ModBooks;
import com.example.loot.ModLootTables;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * In-game test command for measuring how often the mod's music discs and unique books
 * drop from chests. It rolls the <b>real</b> loot tables (including the pools this mod
 * injects), so the numbers reflect actual gameplay.
 *
 * <p>Because unique books mutate per-world saved state ({@link GeneratedBookState}), the
 * command snapshots that state before rolling and restores it afterwards — running the
 * test never consumes books from your actual world.
 *
 * <p>Usage:
 * <pre>
 *   /discstats chest &lt;loot_table_id&gt; [count]   measure one chest (default 10000 rolls)
 *   /discstats all [count]                       measure every chest the mod touches (default 1000)
 * </pre>
 */
public final class DiscStatsCommand {
	private static final int DEFAULT_CHEST_ROLLS = 10000;
	private static final int DEFAULT_ALL_ROLLS = 1000;
	private static final int MAX_ROLLS = 1_000_000;

	private DiscStatsCommand() {
	}

	public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
		dispatcher.register(Commands.literal("discstats")
				.requires(source -> source.hasPermission(2))
				.then(Commands.literal("chest")
						.then(Commands.argument("table", ResourceLocationArgument.id())
								.executes(ctx -> runChest(ctx, DEFAULT_CHEST_ROLLS))
								.then(Commands.argument("count", IntegerArgumentType.integer(1, MAX_ROLLS))
										.executes(ctx -> runChest(ctx, IntegerArgumentType.getInteger(ctx, "count"))))))
				.then(Commands.literal("all")
						.executes(ctx -> runAll(ctx, DEFAULT_ALL_ROLLS))
						.then(Commands.argument("count", IntegerArgumentType.integer(1, MAX_ROLLS))
								.executes(ctx -> runAll(ctx, IntegerArgumentType.getInteger(ctx, "count"))))));
	}

	// --- /discstats chest <id> [count] ---

	private static int runChest(CommandContext<CommandSourceStack> ctx, int count) {
		CommandSourceStack source = ctx.getSource();
		ResourceLocation id = ResourceLocationArgument.getId(ctx, "table");
		ResourceKey<LootTable> key = ResourceKey.create(Registries.LOOT_TABLE, id);
		ServerLevel level = source.getLevel();

		LootTable table = level.getServer().reloadableRegistries().getLootTable(key);
		if (table == LootTable.EMPTY) {
			source.sendFailure(Component.literal("Loot-таблицю не знайдено: " + id
					+ " (перевір ID; модові таблиці існують лише якщо відповідний мод/структура завантажені)"));
			return 0;
		}

		boolean discChest = ModLootTables.isDiscChest(key);
		boolean bookChest = !ModBooks.matchingBooks(key).isEmpty();
		if (!discChest && !bookChest) {
			source.sendSuccess(() -> Component.literal(
					"§e⚠ Цей сундук мод не змінює — платівки та книжки сюди не додаються."), false);
		}

		Stats stats = simulate(level, table, key, source.getPosition(), count);
		report(source, id.toString(), key, count, stats);
		return 1;
	}

	// --- /discstats all [count] ---

	private static int runAll(CommandContext<CommandSourceStack> ctx, int count) {
		CommandSourceStack source = ctx.getSource();
		ServerLevel level = source.getLevel();
		Vec3 origin = source.getPosition();

		Set<ResourceKey<LootTable>> union = new TreeSet<>(
				(a, b) -> a.location().toString().compareTo(b.location().toString()));
		union.addAll(ModLootTables.affectedDiscChests());
		union.addAll(ModBooks.affectedBookChests());

		source.sendSuccess(() -> Component.literal(
				"§6=== /discstats all — " + union.size() + " сундуків, по " + count + " відкриттів кожен ==="), false);

		GeneratedBookState state = GeneratedBookState.get(level);
		Set<String> snapshot = state.snapshot();
		try {
			int skipped = 0;
			long grandDiscs = 0;
			Set<String> collectedAll = new TreeSet<>();

			for (ResourceKey<LootTable> key : union) {
				LootTable table = level.getServer().reloadableRegistries().getLootTable(key);
				if (table == LootTable.EMPTY) {
					skipped++;
					continue;
				}

				Stats s = simulate(level, table, key, origin, count);
				grandDiscs += s.discHits;
				collectedAll.addAll(s.collectedBooks);

				String line = String.format("§7%s §r§b%s%%§7 диск, §d%s%%§7 книжка§r",
						trimNamespace(key.location().toString()),
						pct(s.discHits, count), pct(s.bookGateHits, count));
				source.sendSuccess(() -> Component.literal(line), false);
			}

			long totalDiscsF = grandDiscs;
			int collectedF = collectedAll.size();
			int skippedF = skipped;
			source.sendSuccess(() -> Component.literal(String.format(
					"§6РАЗОМ:§r платівок випало §b%d§r, унікальних книжок зібрано §d%d / %d§r%s",
					totalDiscsF, collectedF, ModBooks.totalBookCount(),
					skippedF > 0 ? " §8(пропущено " + skippedF + " незавантажених таблиць)" : "")), false);
		} finally {
			state.restore(snapshot);
		}

		source.sendSuccess(() -> Component.literal("§a✔ Стан світу не змінено (book state відновлено)."), false);
		return 1;
	}

	// --- core simulation (no permanent side effects) ---

	private static Stats simulate(ServerLevel level, LootTable table, ResourceKey<LootTable> key, Vec3 origin, int count) {
		GeneratedBookState state = GeneratedBookState.get(level);
		Set<String> snapshot = state.snapshot();
		Stats stats = new Stats();
		try {
			// Pass 1 — per-roll rates. Reset book state before every roll so the 10% book
			// gate is measured cleanly (otherwise the unique-book pool exhausts after a few rolls).
			for (int i = 0; i < count; i++) {
				state.restore(snapshot);
				boolean disc = false;
				boolean book = false;
				for (ItemStack stack : rollOnce(level, table, origin)) {
					Item item = stack.getItem();
					if (isDisc(item)) {
						disc = true;
						stats.discByName.merge(pathOf(item), 1, Integer::sum);
					} else if (isBook(item)) {
						book = true;
					}
				}
				if (disc) {
					stats.discHits++;
				}
				if (book) {
					stats.bookGateHits++;
				}
			}

			// Pass 2 — collection over `count` openings. No reset: unique books accumulate
			// exactly as they would in a real world, so we see how many distinct books you'd get.
			state.restore(snapshot);
			for (int i = 0; i < count; i++) {
				for (ItemStack stack : rollOnce(level, table, origin)) {
					if (isBook(stack.getItem())) {
						stats.collectedBooks.add(pathOf(stack.getItem()));
					}
				}
			}
		} finally {
			state.restore(snapshot);
		}
		return stats;
	}

	private static List<ItemStack> rollOnce(ServerLevel level, LootTable table, Vec3 origin) {
		LootParams params = new LootParams.Builder(level)
				.withParameter(LootContextParams.ORIGIN, origin)
				.create(LootContextParamSets.CHEST);
		return table.getRandomItems(params);
	}

	private static void report(CommandSourceStack source, String id, ResourceKey<LootTable> key, int count, Stats stats) {
		source.sendSuccess(() -> Component.literal("§6=== Статистика лутту: §r" + id + " §6(прокруток: " + count + ") ==="), false);

		float discChance = ModLootTables.discChanceForChest(key);
		source.sendSuccess(() -> Component.literal(String.format(
				"§bПлатівки:§r %d / %d = §b%s%%§r  (налаштовано: %s%%)",
				stats.discHits, count, pct(stats.discHits, count), fmt(discChance * 100))), false);
		if (!stats.discByName.isEmpty()) {
			StringBuilder sb = new StringBuilder("§7  розподіл:§r ");
			boolean first = true;
			for (var e : stats.discByName.entrySet()) {
				if (!first) {
					sb.append(", ");
				}
				sb.append(e.getKey()).append(' ').append(e.getValue());
				first = false;
			}
			String discLine = sb.toString();
			source.sendSuccess(() -> Component.literal(discLine), false);
		}

		float bookChance = ModBooks.bookChanceForChest(key);
		int eligible = ModBooks.matchingBooks(key).size();
		source.sendSuccess(() -> Component.literal(String.format(
				"§dКнижки (шанс на відкриття):§r %d / %d = §d%s%%§r  (налаштовано: %s%%)",
				stats.bookGateHits, count, pct(stats.bookGateHits, count), fmt(bookChance * 100))), false);
		source.sendSuccess(() -> Component.literal(String.format(
				"§dКнижки:§r у цьому сундуку доступно §d%d§r типів; за %d відкриттів зібрано унікальних: §d%d§r",
				eligible, count, stats.collectedBooks.size())), false);
		source.sendSuccess(() -> Component.literal(
				"§8  (книжки унікальні — кожна випадає лише раз за світ)"), false);

		source.sendSuccess(() -> Component.literal("§a✔ Стан світу не змінено (book state відновлено)."), false);
	}

	// --- helpers ---

	private static boolean isDisc(Item item) {
		ResourceLocation id = BuiltInRegistries.ITEM.getKey(item);
		return id.getNamespace().equals(CustomDiscs.MOD_ID) && id.getPath().endsWith("_disc");
	}

	private static boolean isBook(Item item) {
		ResourceLocation id = BuiltInRegistries.ITEM.getKey(item);
		return id.getNamespace().equals(CustomDiscs.MOD_ID) && id.getPath().endsWith("_book");
	}

	private static String pathOf(Item item) {
		return BuiltInRegistries.ITEM.getKey(item).getPath();
	}

	private static String pct(long hits, int total) {
		return fmt(100.0 * hits / total);
	}

	private static String fmt(double value) {
		return String.format("%.2f", value);
	}

	private static String trimNamespace(String id) {
		return id.startsWith("minecraft:") ? id.substring("minecraft:".length()) : id;
	}

	private static final class Stats {
		int discHits;
		int bookGateHits;
		final TreeMap<String, Integer> discByName = new TreeMap<>();
		final Set<String> collectedBooks = new TreeSet<>();
	}
}
