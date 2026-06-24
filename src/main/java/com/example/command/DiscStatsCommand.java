package com.example.command;

import com.example.CustomDiscs;
import com.example.book.GeneratedBookState;
import com.example.book.ModBooks;
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
 * drop from a given chest. It rolls the <b>real</b> loot table (including the pools this
 * mod injects globally), so the numbers reflect actual gameplay.
 *
 * <p>Unique books mutate per-world saved state ({@link GeneratedBookState}), so the command
 * snapshots that state before rolling and restores it afterwards — running the test never
 * consumes books from your actual world.
 *
 * <p>Usage: {@code /discstats chest <loot_table_id> [count]} (default 10000 rolls).
 */
public final class DiscStatsCommand {
	private static final int DEFAULT_ROLLS = 10000;
	private static final int MAX_ROLLS = 1_000_000;

	private DiscStatsCommand() {
	}

	public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
		dispatcher.register(Commands.literal("discstats")
				.requires(source -> source.hasPermission(2))
				.then(Commands.literal("chest")
						.then(Commands.argument("table", ResourceLocationArgument.id())
								.executes(ctx -> runChest(ctx, DEFAULT_ROLLS))
								.then(Commands.argument("count", IntegerArgumentType.integer(1, MAX_ROLLS))
										.executes(ctx -> runChest(ctx, IntegerArgumentType.getInteger(ctx, "count")))))));
	}

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

		Stats stats = simulate(level, table, source.getPosition(), count);
		report(source, id.toString(), count, stats);
		return 1;
	}

	// --- core simulation (no permanent side effects) ---

	private static Stats simulate(ServerLevel level, LootTable table, Vec3 origin, int count) {
		GeneratedBookState state = GeneratedBookState.get(level);
		Set<String> snapshot = state.snapshot();
		Stats stats = new Stats();
		try {
			// Pass 1 — per-roll rates. Reset book state before every roll so the book gate is
			// measured cleanly (otherwise the unique-book pool exhausts after a few rolls).
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

			// Pass 2 — collection over `count` openings. No reset: unique books accumulate exactly
			// as they would in a real world, so we see how many distinct books you'd actually get.
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

	private static void report(CommandSourceStack source, String id, int count, Stats stats) {
		source.sendSuccess(() -> Component.literal("§6=== Статистика лутту: §r" + id + " §6(прокруток: " + count + ") ==="), false);

		source.sendSuccess(() -> Component.literal(String.format(
				"§bПлатівки:§r %d / %d = §b%s%%", stats.discHits, count, pct(stats.discHits, count))), false);
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

		source.sendSuccess(() -> Component.literal(String.format(
				"§dКнижки (шанс на відкриття):§r %d / %d = §d%s%%", stats.bookGateHits, count, pct(stats.bookGateHits, count))), false);
		source.sendSuccess(() -> Component.literal(String.format(
				"§dКнижки:§r за %d відкриттів зібрано унікальних: §d%d / %d§r", count, stats.collectedBooks.size(), ModBooks.totalBookCount())), false);
		if (!stats.collectedBooks.isEmpty()) {
			source.sendSuccess(() -> Component.literal("§7  " + String.join(", ", stats.collectedBooks)), false);
		}

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
		return String.format("%.2f", 100.0 * hits / total);
	}

	private static final class Stats {
		int discHits;
		int bookGateHits;
		final TreeMap<String, Integer> discByName = new TreeMap<>();
		final Set<String> collectedBooks = new TreeSet<>();
	}
}
