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

public record UniqueBookLootFunction(List<BookDefinition> books) implements LootItemFunction {
	private static final int MAX_WRITTEN_BOOK_TITLE_LENGTH = 32;

	@Override
	public LootItemFunctionType<? extends LootItemFunction> getType() {
		return LootItemFunctions.SET_CUSTOM_DATA;
	}

	@Override
	public ItemStack apply(ItemStack stack, LootContext context) {
		GeneratedBookState state = GeneratedBookState.get(context.getLevel());
		List<BookDefinition> availableBooks = new ArrayList<>();
		for (BookDefinition book : books) {
			if (!state.isGenerated(book.id())) {
				availableBooks.add(book);
			}
		}

		if (availableBooks.isEmpty()) {
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
