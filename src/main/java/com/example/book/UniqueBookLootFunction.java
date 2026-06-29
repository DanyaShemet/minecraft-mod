package com.example.book;

import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.network.Filterable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.WrittenBookContent;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctions;

import java.util.ArrayList;
import java.util.List;

public record UniqueBookLootFunction(List<BookDefinition> books, float chance) implements LootItemFunction {
	private static final int MAX_WRITTEN_BOOK_TITLE_LENGTH = 32;

	@Override
	public LootItemFunctionType<? extends LootItemFunction> getType() {
		return LootItemFunctions.SET_CUSTOM_DATA;
	}

	@Override
	public ItemStack apply(ItemStack stack, LootContext context) {
		GeneratedBookState state = GeneratedBookState.get(context.getLevel());

		// Gate: the very first book (chronicle_of_departure) must be found before any other book
		// can drop anywhere in the world. While it is still ungenerated, the only book that can
		// drop is the first one — and only in chests where it is eligible — at its own elevated
		// chance. Every other book is suppressed until then.
		List<BookDefinition> availableBooks = new ArrayList<>();
		float effectiveChance;
		if (!state.isGenerated(ModBooks.FIRST_BOOK_ID)) {
			for (BookDefinition book : books) {
				if (book.id().equals(ModBooks.FIRST_BOOK_ID)) {
					availableBooks.add(book);
					break;
				}
			}
			effectiveChance = ModBooks.FIRST_BOOK_CHANCE;
		} else {
			for (BookDefinition book : books) {
				if (!state.isGenerated(book.id())) {
					availableBooks.add(book);
				}
			}
			effectiveChance = chance;
		}

		if (availableBooks.isEmpty()) {
			return ItemStack.EMPTY;
		}

		// Probability roll is done here (not as a pool-level condition) so the chance can depend
		// on world state: 75% for the first book, the chest's normal chance for everything after.
		if (context.getRandom().nextFloat() >= effectiveChance) {
			return ItemStack.EMPTY;
		}

		BookDefinition book = availableBooks.get(context.getRandom().nextInt(availableBooks.size()));
		state.markGenerated(book.id());
		ItemStack bookStack = new ItemStack(book.item());
		bookStack.set(DataComponents.ITEM_NAME, Component.literal(book.title()));
		bookStack.set(DataComponents.WRITTEN_BOOK_CONTENT, createContent(book));
		return bookStack;
	}

	private static WrittenBookContent createContent(BookDefinition book) {
		List<Filterable<Component>> pages = book.pages().stream()
				.<Filterable<Component>>map(page -> Filterable.passThrough(Component.literal(page)))
				.toList();

		return new WrittenBookContent(
				Filterable.passThrough(safeBookTitle(book.title())),
				book.author(),
				0,
				pages,
				true
		);
	}

	private static String safeBookTitle(String title) {
		if (title.length() <= MAX_WRITTEN_BOOK_TITLE_LENGTH) {
			return title;
		}

		return title.substring(0, MAX_WRITTEN_BOOK_TITLE_LENGTH);
	}
}
