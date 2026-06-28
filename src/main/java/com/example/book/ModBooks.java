package com.example.book;

import com.example.item.ModItems;
import com.example.loot.ModLootTables;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public final class ModBooks {
	// Default book chance: stronghold, villages, dungeons and the global fallback.
	private static final float BOOK_CHEST_CHANCE = 0.06F;
	// Elemental/themed books in their own themed places (desert, jungle, ocean). Nether stays default.
	private static final float THEMED_BOOK_CHANCE = 0.10F;
	// Ancient city: a second backup like stronghold (all books except nether), but rarer.
	private static final float ANCIENT_CITY_BOOK_CHANCE = 0.05F;
	private static final ResourceKey<LootTable> SIMPLE_DUNGEON_CHEST = chest("simple_dungeon");

	private static final Set<ResourceKey<LootTable>> VILLAGE_CHESTS = Set.of(
			chest("village/village_armorer"),
			chest("village/village_butcher"),
			chest("village/village_cartographer"),
			chest("village/village_desert_house"),
			chest("village/village_fisher"),
			chest("village/village_fletcher"),
			chest("village/village_mason"),
			chest("village/village_plains_house"),
			chest("village/village_savanna_house"),
			chest("village/village_shepherd"),
			chest("village/village_snowy_house"),
			chest("village/village_taiga_house"),
			chest("village/village_tannery"),
			chest("village/village_temple"),
			chest("village/village_toolsmith"),
			chest("village/village_weaponsmith"),
			// Woodland Mansion + Dungeons & Taverns illager mansion (its override): share the village book pool.
			chest("woodland_mansion"),
			chest("illager_mansion/generic"),
			chest("illager_mansion/secret_room")
	);

	private static final Set<ResourceKey<LootTable>> BASTION_CHESTS = Set.of(
			chest("bastion_bridge"),
			chest("bastion_hoglin_stable"),
			chest("bastion_other"),
			chest("bastion_treasure")
	);

	private static final Set<ResourceKey<LootTable>> SHIPWRECK_CHESTS = Set.of(
			chest("shipwreck_map"),
			chest("shipwreck_supply"),
			chest("shipwreck_treasure")
	);

	private static final Set<ResourceKey<LootTable>> OCEAN_RUIN_CHESTS = Set.of(
			chest("underwater_ruin_big"),
			chest("underwater_ruin_small")
	);

	// YUNG's Better Desert Temples
	private static final Set<ResourceKey<LootTable>> BETTER_DESERT_TEMPLE_CHESTS = Set.of(
			table("betterdeserttemples", "chests/tomb"),
			table("betterdeserttemples", "chests/tomb_pharaoh"),
			table("betterdeserttemples", "chests/pharaoh_hidden"),
			table("betterdeserttemples", "chests/library"),
			table("betterdeserttemples", "chests/lab"),
			table("betterdeserttemples", "chests/storage"),
			table("betterdeserttemples", "chests/food_storage"),
			table("betterdeserttemples", "chests/statue"),
			table("betterdeserttemples", "chests/wardrobe"),
			table("betterdeserttemples", "chests/pot")
	);

	// YUNG's Better Jungle Temples
	private static final Set<ResourceKey<LootTable>> BETTER_JUNGLE_TEMPLE_CHESTS = Set.of(
			table("betterjungletemples", "chests/treasure"),
			table("betterjungletemples", "chests/campsite")
	);

	// YUNG's Better Ocean Monuments
	private static final Set<ResourceKey<LootTable>> BETTER_OCEAN_MONUMENT_CHESTS = Set.of(
			table("betteroceanmonuments", "chests/upper_side_chamber")
	);

	// YUNG's Better Nether Fortresses
	private static final Set<ResourceKey<LootTable>> BETTER_FORTRESS_CHESTS = Set.of(
			table("betterfortresses", "chests/hall"),
			table("betterfortresses", "chests/keep"),
			table("betterfortresses", "chests/quarters"),
			table("betterfortresses", "chests/storage"),
			table("betterfortresses", "chests/beacon"),
			table("betterfortresses", "chests/worship"),
			table("betterfortresses", "chests/puzzle"),
			table("betterfortresses", "chests/obsidian"),
			table("betterfortresses", "chests/extra")
	);

	// YUNG's Better Dungeons
	private static final Set<ResourceKey<LootTable>> BETTER_DUNGEON_CHESTS = Set.of(
			table("betterdungeons", "skeleton_dungeon/chests/common"),
			table("betterdungeons", "skeleton_dungeon/chests/middle"),
			table("betterdungeons", "small_dungeon/chests/loot_piles"),
			table("betterdungeons", "small_nether_dungeon/chests/common"),
			table("betterdungeons", "spider_dungeon/chests/egg_room"),
			table("betterdungeons", "zombie_dungeon/chests/common"),
			table("betterdungeons", "zombie_dungeon/chests/special"),
			table("betterdungeons", "zombie_dungeon/chests/tombstone")
	);

	// YUNG's Better Witch Huts
	private static final Set<ResourceKey<LootTable>> BETTER_WITCH_HUT_CHESTS = Set.of(
			table("betterwitchhuts", "chests/hut_0")
	);

	// --- Themed chests from Structory and Dungeons & Taverns (nova_structures) ---

	// Desert-themed
	private static final Set<ResourceKey<LootTable>> EXTRA_DESERT_CHESTS = Set.of(
			table("structory", "outcast/bandit/desert"),
			table("structory", "outcast/bandit/desert_copper"),
			table("nova_structures", "chests/desert_ruins/desert_ruin_grave"),
			table("nova_structures", "chests/desert_ruins/desert_ruin_house"),
			table("nova_structures", "chests/desert_ruins/desert_ruin_lesser_treasure"),
			table("nova_structures", "chests/desert_ruins/desert_ruin_main_temple")
	);

	// Jungle-themed
	private static final Set<ResourceKey<LootTable>> EXTRA_JUNGLE_CHESTS = Set.of(
			table("nova_structures", "chests/jungle_ruins/jungle_ruins_house"),
			table("nova_structures", "chests/jungle_ruins/jungle_ruins_main_temple"),
			table("nova_structures", "chests/jungle_ruins/jungle_ruins_main_temple_wild"),
			chest("village/village_jungle_house")
	);

	// Ocean/water-themed
	private static final Set<ResourceKey<LootTable>> EXTRA_OCEAN_CHESTS = Set.of(
			table("structory", "outcast/boat/loot"),
			table("nova_structures", "chests/conduit_ruin/conduit_ruin_big"),
			table("nova_structures", "chests/conduit_ruin/conduit_ruin_main"),
			table("nova_structures", "chests/conduit_ruin/conduit_ruin_small"),
			table("nova_structures", "chests/trident_trial_monument/ttm_common_vault"),
			table("nova_structures", "chests/trident_trial_monument/ttm_boss_vault_heart"),
			table("nova_structures", "chests/trident_trial_monument/ttm_boss_vault_trident")
	);

	// Nether-themed (includes Dungeons & Taverns' nether fortress override, for parity)
	private static final Set<ResourceKey<LootTable>> EXTRA_NETHER_CHESTS = Set.of(
			chest("nether_fortress/fort_inside"),
			chest("nether_fortress/fort_inside_generic"),
			table("nova_structures", "chests/nether_keep/vault_keep"),
			table("nova_structures", "chests/nether_port/nether_port_chest"),
			table("nova_structures", "chests/nether_skeleton_tower/skeleton_tower_chest"),
			table("nova_structures", "chests/piglin_camp/chest"),
			table("nova_structures", "chests/piglin_donjon/vault_piglin_donjon"),
			table("nova_structures", "chests/piglin_outstation/outstation_treasure")
	);

	// Chests that host the general (non-themed) book set, like vanilla simple dungeons
	private static final Set<ResourceKey<LootTable>> GENERAL_BOOK_CHESTS = merge(BETTER_DUNGEON_CHESTS, BETTER_WITCH_HUT_CHESTS);

	// --- Gap structures (Dungeons & Taverns) that had no themed match: get only the general overworld books ---

	// Notable rooms of the Witch Villa
	private static final Set<ResourceKey<LootTable>> WITCH_VILLA_CHESTS = Set.of(
			table("nova_structures", "chests/witch_villa/music_room"),
			table("nova_structures", "chests/witch_villa/library"),
			table("nova_structures", "chests/witch_villa/lab"),
			table("nova_structures", "chests/witch_villa/potion_brewing"),
			table("nova_structures", "chests/witch_villa/slime_room")
	);

	// All gap chests that host the general overworld book set
	private static final Set<ResourceKey<LootTable>> OVERWORLD_GENERAL_CHESTS = mergeAll(
			WITCH_VILLA_CHESTS,
			Set.of(
					table("nova_structures", "chests/mangrove_witchhud"),
					table("nova_structures", "chests/badland_miner_outpost"),
					table("nova_structures", "chests/badland_miner_outpost_forge"),
					table("nova_structures", "chests/badland_miner_outpost_towers"),
					table("nova_structures", "chests/bunker_altar"),
					table("nova_structures", "chests/illager_camp"),
					table("nova_structures", "chests/firewatch_tower"),
					table("nova_structures", "chests/undead_crypts_grave"),
					table("nova_structures", "chests/tavern_quest"),
					table("nova_structures", "chests/village/village_birch_house")
			)
	);

	private static final BookDefinition CHRONICLE_OF_DEPARTURE = new BookDefinition(
			"chronicle_of_departure",
			"Про Відхід Чотирьох",
			"Літописець Дебела Незламна",
			ModItems.CHRONICLE_OF_DEPARTURE_BOOK,
			1.0F,
			VILLAGE_CHESTS,
			List.of(
					"У п'ятий рік після Відходу записую ці слова, аби майбутні покоління пам'ятали те, що ще пам'ятаємо ми.\n\nМинуло п'ять зим відтоді, як Четверо покинули світ.",
					"Потужніч, Творець Міст і Законів, більше не ходить серед людей.\n\nКажуть, він знав кожну дорогу, кожне поселення і майже кожну людину на ім'я.",
					"За його часів ніхто не залишався без допомоги надовго.\n\nПісля його Відходу світ не зруйнувався одразу.\n\nТа люди почали помічати, що більше немає того, хто об'єднував усіх.",
					"PARA_22, Володар Каменю, не повернувся до Казадуму.\n\nВеликі дороги, що колись пов'язували краї, залишилися без нагляду.\n\nКамінь стоїть міцно, але руки, що його клали, зникли.",
					"Sleepwalking, Мати Лісів і Річок, більше не ходить серед своїх садів.\n\nДерева ростуть і нині, та старі друїди кажуть, що світ став тихішим.",
					"Minecramet, Великий Дослідник, також не повернувся.\n\nЙого карти розійшлися світом, але ніхто не знає, чи всі вони були знайдені.",
					"Багато хто досі чекає їхнього повернення.\n\nБагато хто вже втратив надію.\n\nЧерез це народ розділився.",
					"Одні кажуть, що Четверо були богами і колись повернуться.\n\nІнші кажуть, що вони були лише великими смертними, а їхня епоха завершилася.",
					"Та незалежно від віри, всі погоджуються в одному:\n\nСвіт змінився.\n\nСьогодні ж ми готуємо Перший Похід.",
					"Двадцять сім добровольців вирушать старою західною дорогою.\n\nМи шукатимемо покинуті поселення, забуті архіви та сховані скарбниці.",
					"Якщо залишилися книги - ми повернемо їх людям.\n\nЯкщо залишилися карти - ми продовжимо їхні шляхи.\n\nЯкщо залишилися скарби - вони стануть надбанням усіх.",
					"А якщо знайдемо сліди Чотирьох...\n\nто світ отримає відповідь, якої чекає вже п'ять років.\n\nНехай цей запис переживе нас, якщо ми не повернемося."
			)
	);

	private static final BookDefinition JUNGLE_TEMPLE_JOURNAL = new BookDefinition(
			"jungle_temple_journal",
			"Записки з Південних Джунглів",
			"Томас Редвуд",
			ModItems.JUNGLE_TEMPLE_JOURNAL_BOOK,
			1.0F,
			mergeAll(Set.of(chest("jungle_temple")), BETTER_JUNGLE_TEMPLE_CHESTS, EXTRA_JUNGLE_CHESTS),
			List.of(
					"151 рік після Відходу.\n\nНарешті ми досягли Південних Джунглів.\n\nМісцеві провідники відмовилися йти далі.",
					"Вони називають це місце Храмом Картографа.\n\nКажуть, тут Великий Шукач сховав знання про весь світ.",
					"Старі легенди стверджують, що стіни храму колись були вкриті картами земель, яких більше не існує.",
					"Я не вірю в ці казки.\n\nЯкби такі карти існували, їх давно б винесли мисливці за скарбами.",
					"Та сьогодні ми знайшли дивне кам'яне приміщення.\n\nНа стіні було висічено знак компаса.",
					"Під ним лежав уламок паперу.\n\nУсе, що вдалося прочитати:\n\n'...на захід від великої дороги...'",
					"Велика дорога.\n\nТак колись називали шляхи, збудовані PARA_22.\n\nНавіть через століття їх пам'ятають усі.",
					"Дивно.\n\nЧим більше я подорожую, тим частіше зустрічаю сліди Чотирьох.",
					"Їхні міста зникли.\nЇхні домівки зникли.\n\nАле весь світ досі живе серед речей, які вони залишили після себе.",
					"Завтра ми відкриємо нижній зал храму.\n\nЯкщо цей щоденник знайдуть без мене - значить легенди були правдивими."
			)
	);

	private static final BookDefinition BONES_IN_HELL = new BookDefinition(
			"bones_in_hell",
			"Кістки в Пеклі",
			"Професор Еліас Вольтер",
			ModItems.BONES_IN_HELL_BOOK,
			1.0F,
			mergeAll(Set.of(chest("nether_bridge")), BASTION_CHESTS, BETTER_FORTRESS_CHESTS, EXTRA_NETHER_CHESTS),
			List.of(
					"320 рік після Відходу.\n\nНаша експедиція прибула до руїн старої фортеці в Пеклі.\n\nМетою було вивчення піглінів та їхньої історії.",
					"На третій день розкопок ми виявили зал, завалений чорним каменем.\n\nПід ним знаходилися кістки невідомого походження.",
					"Зразки були занадто великими для звичайної людини.\n\nАле й до жодної відомої істоти вони також не належали.",
					"Поруч було знайдено залишки залізних інструментів.\n\nНадзвичайно старих.\n\nСтарших за більшість людських поселень.",
					"На одному з уламків зберігся дивний знак.\n\nЧотири зігнуті лінії, що перехрещуються в центрі",
					"\n\nСимвол, що часто зустрічається в легендах про PARA_22.",
					"Місцеві провідники негайно відмовилися продовжувати роботу.\n\nВони були переконані, що ми потривожили могилу Володаря Каменю.",
					"Згідно зі старими переказами, PARA_22 вів війну проти піглінів задовго до Відходу.\n\nЖодних доказів цього ніколи не існувало.",
					"Принаймні до сьогодні.",
					"Я залишаюся скептиком.\n\nОдна знахідка не може підтвердити легенду, якій понад три століття.",
					"Та я не можу пояснити інше.\n\nЧому навколо кісток лежать сотні іржавих золотих мечів піглінів.",
					"І чому всі вони спрямовані в один бік.\n\nНаче їхні власники билися проти одного ворога.",
					"Завтра ми відкриємо останню камеру комплексу.\n\nЯкщо ця книга обірветься тут, значить ми знайшли щось значно важливіше за кістки."
			)
	);

	private static final BookDefinition PRAYER_IN_THE_SAND = new BookDefinition(
			"prayer_in_the_sand",
			"Молитва Серед Пісків",
			"Священик Матвій",
			ModItems.PRAYER_IN_THE_SAND_BOOK,
			1.0F,
			mergeAll(Set.of(chest("desert_pyramid")), BETTER_DESERT_TEMPLE_CHESTS, EXTRA_DESERT_CHESTS),
			List.of(
					"74 день нашої подорожі.\n\nПровізія майже закінчилася.\n\nДехто з людей уже говорить про повернення.",
					"Ми шукали нові землі за Великою Пустелею.\n\nТа натомість знайшли лише пісок.",
					"Сьогодні помер старий коваль.\n\nПеред смертю він сказав, що ми заблукали ще багато тижнів тому.",
					"Вночі люди почали сваритися.\n\nДехто звинувачує мене.\n\nІ, можливо, справедливо.",
					"Я піднявся на дюну і вперше за багато років звернувся до одного з Чотирьох.",
					"\"Minecramet, Шукачу Шляхів.\nЯкщо ти досі дивишся на цей світ - покажи нам дорогу.\"",
					"Відповіді не було.\n\nЛише вітер і пісок.",
					"Та на світанку один хлопчик знайшов старий кам'яний стовп.",
					"На ньому був висічений знак компаса.\n\nТакий самий, як у старих книгах про Великого Дослідника.",
					"Ми рушили на північ.\n\nЧерез три дні знайшли річку.",
					"Я не знаю, чи почув він мою молитву.\n\nАле сьогодні ніхто з нас не помер.",
					"І цього мені достатньо."
			)
	);

	private static final BookDefinition SEA_OF_TURTLES = new BookDefinition(
			"sea_of_turtles",
			"Море Черепах",
			"Капітан Ерік Сольвейг",
			ModItems.SEA_OF_TURTLES_BOOK,
			1.0F,
			mergeAll(SHIPWRECK_CHESTS, OCEAN_RUIN_CHESTS, BETTER_OCEAN_MONUMENT_CHESTS, EXTRA_OCEAN_CHESTS),
			List.of(
					"455 рік після Відходу.\n\nНа сьомий день плавання ми помітили дивну споруду серед моря.",
					"Спершу я вирішив, що це звичайні руїни.\n\nТа коли ми наблизилися, побачили сотні черепах.",
					"Вони плавали навколо споруди колами.\n\nСпокійно.\n\nНаче чекали на щось.",
					"У воді не було водоростей.\n\nНе було риби.\n\nНе було нічого, що могло б привабити таку кількість тварин.",
					"Під водою ми знайшли храм.\n\nЙого стіни були вкриті зеленим каменем і коралами.",
					"Жодних написів.\n\nЖодних скарбів.\n\nЛише тиша.",
					"За кількасот метрів від храму стояли залишки іншої споруди.",
					"Висока кам'яна вежа.\n\nВона нагадувала маяк, хоча жодних ознак вогню чи механізмів ми не знайшли.",
					"Один із моряків присягався, що бачив на її вершині дерево.",
					"Я піднявся нагору особисто.\n\nДерева там не було.",
					"Проте серед каміння я знайшов старий уламок мозаїки.\n\nНа ньому була зображена черепаха, оточена квітами.",
					"Коли ми відпливали, черепахи залишилися біля храму.\n\nУсі до останньої.\n\nНаче це місце досі належало комусь іншому."
			)
	);

	private static final BookDefinition BOOK_OF_THE_FOUR = new BookDefinition(
			"book_of_the_four",
			"Книга Чотирьох - Справжня Історія",
			"Верховний Жрець Августин",
			ModItems.BOOK_OF_THE_FOUR_BOOK,
			1.0F,
			VILLAGE_CHESTS,
			List.of(
					"На початку існувала лише Порожнеча.\n\nІ тоді прийшли Четверо.",
					"Потужніч узяв землю в долоні та надав їй форму.\n\nТак народилися рівнини, ліси та моря.",
					"PARA_22 ударив своїм молотом по світу.\n\nІ з надр землі постали гори, печери та кам'яні хребти.",
					"Sleepwalking вдихнула життя в створене.\n\nІ кожна травинка, кожне дерево та кожна тварина почали свій шлях.",
					"Minecramet піднявся над небом і побачив усі дороги минулого та майбутнього.",
					"Тому жодна стежка не могла бути втрачена без його відома.",
					"Чотири століття вони правили світом у мирі та злагоді.",
					"Та одного дня пітьма піднялася з глибин Пекла.",
					"PARA_22 самотужки стримував її сорок днів і сорок ночей.\n\nЙого молот розбивав цілі фортеці ворогів.",
					"Minecramet вів народи через бурі та океани.\n\nЙого зоря сяяла над усіма кораблями.",
					"Sleepwalking зупиняла посухи та наказувала лісам рости там, де була пустка.",
					"Потужніч чув молитви кожної живої душі одночасно.\n\nІ жоден прохач не залишався без відповіді.",
					"Коли ж настав час Відходу, Четверо не померли.",
					"Вони піднялися за межі світу, щоб одного дня повернутися в годину найбільшої потреби.",
					"І тому ми чекаємо.\n\nБо жодна епоха не триває вічно, а шлях Чотирьох ще не завершено."
			)
	);

	private static final BookDefinition THE_HIDDEN_TRUTH = new BookDefinition(
			"the_hidden_truth",
			"Правда, Яку Вони Приховують",
			"Борис Чорнопис",
			ModItems.THE_HIDDEN_TRUTH_BOOK,
			1.0F,
			merge(VILLAGE_CHESTS, Set.of(chest("abandoned_mineshaft"))),
			List.of(
					"Якщо ти читаєш цю книгу, значить жерці Чотирьох ще не знайшли тебе.",
					"Нас вчили, що Потужніч був добрим.\n\nТоді поясни мені, куди зникло селище Березівка?",
					"Усього за одну ніч зникли тридцять сім селян.\n\nЖодної боротьби.\n\nЖодних слідів.",
					"Офіційна історія говорить про набіг розбійників.\n\nЦікаво, що жодного тіла так і не знайшли.",
					"Моя теорія проста.\n\nПотужніч наказав переселити їх до Потужляндії для своїх таємних проєктів.",
					"А тепер поговоримо про Minecramet.",
					"Чи не дивно, що всі великі голоди починалися після його експедицій?",
					"Нам кажуть, що він був великим дослідником.\n\nЯ ж вважаю, що він рахував людей так само, як рахував карти.",
					"У старих архівах згадується його фраза:\n\n'Світ має межі.'",
					"А що як він боявся перенаселення?\n\nЩо як деякі катастрофи були зовсім не випадковими?",
					"Звісно, жодних доказів у мене немає.\n\nБо всі докази давно зникли.",
					"Саме так вони і працюють."
			)
	);

	private static final BookDefinition WHEAT_FOR_EVERYONE = new BookDefinition(
			"wheat_for_everyone",
			"Про Правильне Вирощування Пшениці",
			"Іван Колос",
			ModItems.WHEAT_FOR_EVERYONE_BOOK,
			1.0F,
			VILLAGE_CHESTS,
			List.of(
					"Якщо ви читаєте цю книгу, значить вам потрібна пшениця.\n\nАбо вам просто нічим зайнятися.",
					"Пшениця найкраще росте на добре зволоженій землі.\n\nНе нехтуйте каналами та грядками.",
					"Пам'ятайте:\n\nодне відро води може напоїти значно більше землі, ніж здається на перший погляд.",
					"Не поспішайте збирати врожай.\n\nЗелена пшениця приносить лише розчарування.",
					"Якщо поруч живуть кури - будуйте паркан.\n\nЯкщо поруч живуть сусіди - теж будуйте паркан.",
					"Частину врожаю завжди залишайте на насіння.\n\nФермер без запасів довго фермером не буде.",
					"Під час неврожаю обмінюйте хліб на інструменти.\n\nПід час врожаю обмінюйте інструменти на хліб.",
					"Якщо вам здається, що поле достатньо велике - зробіть його вдвічі більшим.",
					"Більшість проблем держави можна вирішити хорошим урожаєм.\n\nРешту проблем - ще кращим урожаєм.",
					"Якщо жодна з цих порад не допомогла, спробуйте прочитати книгу ще раз.\n\nМожливо, ви пропустили найважливіше.",
					"З повагою,\n\n Михайло Чиновник"
			)
	);

	private static final BookDefinition DECLARATION_OF_FREEDOM = new BookDefinition(
			"declaration_of_freedom",
			"Декларація Вільних Земель",
			"Маршал Грегор Лісний",
			ModItems.DECLARATION_OF_FREEDOM_BOOK,
			1.0F,
			VILLAGE_CHESTS,
			List.of(
					"Другий рік після Відходу.\n\nЯ пишу цей документ від імені Тимчасової Ради Потужляндії.",
					"Майже два роки ми чекали на повернення Чотирьох.\n\nМайже два роки ми робили вигляд, що нічого не змінилося.",
					"Та настав час дивитися правді в очі.\n\nНіхто не повернувся.",
					"Більшість доріг занепадає.\n\nПоселення дедалі рідше отримують допомогу від столиці.",
					"Ми більше не можемо вимагати від людей підкорятися владі, якої не існує.",
					"Тому від сьогодні всі провінції Потужляндії оголошуються вільними.",
					"Кожне місто, кожне село та кожна громада мають право самостійно обирати власних керівників.",
					"Податки до центральної скарбниці скасовуються.\n\nМісцеві запаси залишаються місцевим жителям.",
					"Армія Потужляндії розпускається.\n\nУсі воїни звільняються від присяги.",
					"Ми не відкидаємо спадщину Потужніча.\n\nНавпаки, ми намагаємося зберегти те, що ще можливо зберегти.",
					"Нехай майбутні покоління не кажуть, що ми дозволили державі загинути через страх перед змінами.",
					"Віднині наші землі вільні.\n\nІ нехай історія сама розсудить, чи був цей день початком занепаду чи початком нового світу."
			)
	);

	private static final BookDefinition NEW_ZOMBICHI_CHRONICLE = new BookDefinition(
			"new_zombichi_chronicle",
			"Про Стіну і Борг",
			"Староста Петро Новозомбічний",
			ModItems.NEW_ZOMBICHI_CHRONICLE_BOOK,
			1.0F,
			VILLAGE_CHESTS,
			List.of(
					"204 рік після Відходу.\n\nЯ записую ці слова для тих, хто прийде після нас.",
					"Наше село існує лише завдяки Чотирьом.\n\nПро це пам'ятає кожна родина Нових Зомбічів.",
					"Колись ми були приречені.\n\nЗомбі щодня забирали людей, а врожаїв не вистачало навіть на зиму.",
					"Тоді прийшли вони.\n\nМандрівники.\n\nБудівничі.\n\nРятівники.",
					"Вони звели кам'яну стіну навколо села.\n\nТаку міцну, що вона стоїть і сьогодні.",
					"Багато разів нам пропонували її перебудувати.\n\nБагато разів нам пропонували її розширити.",
					"Ми завжди відмовлялися.",
					"Кожна тріщина цієї стіни є частиною нашої історії.\n\nМи не маємо права її змінювати.",
					"Щороку ми залишаємо біля головних воріт товари для торгівлі.",
					"Пшеницю.\nРибу.\nСмарагди.\n\nНа випадок, якщо вони повернуться.",
					"Минуло вже понад два століття.\n\nТа склади біля воріт досі не порожні.",
					"І якщо одного дня Четверо знову прийдуть до Нових Зомбічів,\n\nми будемо готові зустріти старих друзів."
			)
	);

	private static final BookDefinition OCEAN_ECHOES = new BookDefinition(
			"ocean_echoes",
			"Знахідка на Дні",
			"Водолаз Артур Сивий",
			ModItems.OCEAN_ECHOES_BOOK,
			1.0F,
			mergeAll(SHIPWRECK_CHESTS, OCEAN_RUIN_CHESTS, BETTER_OCEAN_MONUMENT_CHESTS, EXTRA_OCEAN_CHESTS),
			List.of(
					"487 рік після Відходу.\n\nНа глибині сорока двох блоків ми виявили човен.",
					"Судно лежало на боці серед мулу.\n\nОзнак вантажу майже не залишилося.",
					"У човні знаходилися рештки однієї людини.",
					"Кістяк зберігся напрочуд добре.\n\nНаче море не наважувалося його торкатися.",
					"Серед особистих речей було знайдено декілька невеликих полотняних мішечків.",
					"Усередині містилася суха коричнева речовина невідомого походження.\n\nЗапаху вона не мала.",
					"Один із учених припустив, що це був лікувальний засіб.\n\nІнший вважає, що це частина давнього ритуалу.",
					"Під час огляду решток ми почули звук.",
					"Він долинав із темряви за межами світла наших ліхтарів.",
					"Спочатку тихо.\n\nПотім голосніше.",
					"\"ЄЖИ\"\n\nЛише одне слово.",
					"Ми негайно завершили дослідження та піднялися на поверхню.\n\nНаступного дня човен більше не вдалося знайти."
			)
	);

	private static final BookDefinition SKEPTIC_OF_THE_GARDEN = new BookDefinition(
			"skeptic_of_the_garden",
			"Проти Саду Брехні",
			"Дмитро Безвірний",
			ModItems.SKEPTIC_OF_THE_GARDEN_BOOK,
			1.0F,
			VILLAGE_CHESTS,
			List.of(
					"Скільки ще ми будемо повторювати одні й ті самі казки?",
					"Щороку жерці розповідають нам, що Sleepwalking створила всі великі сади, храми та святилища світу.",
					"Тоді дозвольте поставити просте питання.",
					"Де саме чотириста років тому люди взяли призмарин для океанських храмів?",
					"Де вони знайшли корали з далеких морів?\n\nХто доставив їх через пів світу?",
					"А може ми маємо повірити, що каміння саме вилетіло з океану та склалося у стіни?",
					"Щоразу, коли історики не можуть пояснити походження будівлі, вони вимовляють одне слово:\n\n'Sleepwalking'.",
					"Дуже зручно.\n\nНе треба шукати відповідей.\n\nНе треба досліджувати минуле.",
					"Лише назви будь-яку загадку дивом, і роботу завершено.",
					"Я вважаю, що за всіма цими спорудами стояли люди.\n\nТисячі людей.",
					"Забуті інженери.\n\nБудівельники.\n\nМореплавці.\n\nТі, чиї імена ми давно втратили.",
					"І поки ми продовжуємо поклонятися легендам, справжні творці залишаються похованими під ними."
			)
	);

	private static final BookDefinition WEAKLAND_CAMPAIGN = new BookDefinition(
			"weakland_campaign",
			"Лист до Виборців Слабкістьлядії",
			"Микола Каменюк",
			ModItems.WEAKLAND_CAMPAIGN_BOOK,
			1.0F,
			merge(VILLAGE_CHESTS, Set.of(customChest("city_hall"))),
			List.of(
					"До мешканців Слабкістьлядії.\n\nЯк кандидат на посаду мера я зобов'язаний бути чесним.",
					"Ми маємо проблеми.\n\nІ жодна з них не зникне сама по собі.",
					"Наші поля занедбані.\n\nНаші дороги руйнуються.\n\nНаші склади порожніють.",
					"Та щоразу, коли я пропоную рішення, знаходиться хтось, хто каже:",
					"\"Навіщо будувати дорогу зараз?\n\nPARA_22 повернеться і побудує кращу.\"",
					"Коли я пропоную новий торговий шлях, мені відповідають:",
					"\"Minecramet уже давно знає правильний напрямок.\"",
					"Коли я кажу, що нам потрібні нові мости, люди питають, чи не залишив їх десь Потужніч.",
					"Я поважаю нашу історію.\n\nАле історія не ремонтує дороги.",
					"Історія не збирає врожай.\n\nІсторія не будує будинки.",
					"Можливо Четверо повернуться.\n\nМожливо ні.",
					"Але поки ми чекаємо на героїв минулого, наше місто належить людям сьогодення.",
					"Тому я прошу лише про одне:\n\nДавайте хоча б спробуємо врятувати Слабкістьлядію самостійно."
			)
	);

	private static final BookDefinition HISTORY_OF_DISAPPEARANCE = new BookDefinition(
			"history_of_disappearance",
			"Зникнення та Народження Нового Світу",
			"Професор Іван Кирка Молодший",
			ModItems.HISTORY_OF_DISAPPEARANCE_BOOK,
			1.0F,
			VILLAGE_CHESTS,
			List.of(
					"Зникнення Чотирьох вважається найважливішою подією відомої історії.\n\nУсі сучасні держави так чи інакше походять від наслідків цієї події.",
					"До Зникнення більшість поселень були пов'язані спільними дорогами, торговими маршрутами та політичними угодами.",
					"Перші роки після Відходу характеризувалися невизначеністю.\n\nБільшість людей вважала, що Четверо незабаром повернуться.",
					"Саме тому протягом майже двох років значна частина адміністративної системи продовжувала існувати без реального керівництва.",
					"Після усвідомлення того, що повернення може ніколи не відбутися, почався процес розпаду великих державних утворень.",
					"Одночасно з цим виник Великий Похід.\n\nТисячі людей вирушили шукати сліди Чотирьох та їхню спадщину.",
					"Багато сучасних міст були засновані саме експедиціями Великого Походу.\n\nДеякі з них існують і сьогодні.",
					"У цей період почалося активне заселення нових територій.\n\nЗ'явилися десятки незалежних громад та торгових союзів.",
					"Зникнення також призвело до виникнення нового історичного явища — культу пошуку.",
					"На відміну від попередніх поколінь, люди більше не прагнули зберігати світ.\n\nВони прагнули його досліджувати.",
					"Протягом наступних століть були знайдені сотні руїн, архівів, храмів, шахт та покинутих поселень.",
					"Саме в цей час сформувалися археологія, сучасна картографія та більшість історичних наук.",
					"Паралельно розвивалися релігійні рухи.\n\nУ різних регіонах Четверо почали сприйматися як боги, святі або легендарні герої.",
					"Цікаво, що в ранніх джерелах вони майже завжди описуються як звичайні люди, тоді як пізніші тексти часто наділяють їх надприродними здібностями.",
					"Більшість сучасних істориків погоджується, що саме Зникнення стало початком Нового Світу.\n\nПодії до нього та після нього належать до різних історичних епох."
			)
	);

	private static final BookDefinition ALEH_JOURNAL = new BookDefinition(
			"aleh_journal",
			"Нотатки з Великого Походу",
			"Алег Атрижка",
			ModItems.ALEH_JOURNAL_BOOK,
			1.0F,
			VILLAGE_CHESTS,
			List.of(
					"6 рік після підходу. \n\nСьогодні знову дощ.\n\nМої черевики остаточно здалися.",
					"Григорій каже, що це добра прикмета.\n\nЯ думаю, що Григорій просто давно не бачив сухих черевиків.",
					"Ми втратили два дні через болото.\n\nХарчів вистачить ще приблизно на тиждень.",
					"Ніхто не скаржиться.\n\nАбо просто вже немає сил скаржитися.",
					"Учора біля вогню згадували Потужніча.\n\nГригорій розповідав, як той допоміг відбудувати їхній млин після пожежі.",
					"Дивно.\n\nМи вирушили в похід шукати Четверьох, а більшість вечорів просто згадуємо старі історії про них.",
					"Я досі пам'ятаю, як бачив Minecramet на ярмарку.\n\nТоді ніхто не думав, що колись це стане спогадом для онуків.",
					"Найважче не голод і не холод.\n\nНайважче не знати, чи є сенс у тому, що ми робимо.",
					"Іноді здається, що ми запізнилися.\n\nЩо треба було вирушати раніше.",
					"А іноді навпаки.\n\nЩо ми перші люди, які наважилися шукати відповіді.",
					"Сьогодні ми знайшли стару кам'яну дорогу.\n\nТаку які будував PARA_22.",
					"І раптом усі пішли швидше.\n\nНаче сама дорога нагадала нам, чому ми тут."
			)
	);

	private static final BookDefinition MY_STRUGGLE = new BookDefinition(
			"my_struggle",
			"Моя боротьба",
			"Minecramet",
			ModItems.MY_STRUGGLE_BOOK,
			1.0F,
			VILLAGE_CHESTS,
			List.of(
					"Найбільша помилка людей — думати, що світ безмежний.\n\nБудь-яка карта колись закінчується.",
					"Я бачив більше земель, ніж будь-хто до мене.\n\nІ тому знаю: межі світу існують незалежно від того, хочемо ми цього чи ні.",
					"Не кожна шахта повинна бути відома всім.\n\nНе кожна дорога має бути нанесена на карту.\n\nНе кожне знання повинно бути відкритим.",
					"Знання — це не право.\n\nЗнання — це відповідальність.\n\nНепідготовлена людина може зруйнувати більше, ніж армія.",
					"Люди бояться, коли їм кажуть, що ресурсів недостатньо.\n\nТа ще більше вони бояться почути правду про майбутнє.",
					"Правитель повинен думати не про сьогодні.\n\nНе про власне життя.\n\nНавіть не про життя своїх дітей.",
					"Він повинен думати про людей, які народяться через сто і двісті років.\n\nСаме вони житимуть із наслідками наших рішень.",
					"Якщо сьогодні не обмежити використання ресурсів, завтра доведеться обмежувати саме життя.",
					"Тому деякі рішення здаються жорстокими лише тим, хто бачить один день.\n\nЯ змушений бачити століття.",
					"Якщо колись дороги приведуть народи саме туди, куди я передбачав...\n\nЯкщо поселення виростуть там, де я позначив на картах...",
					"...а люди знову сперечатимуться через ті самі землі та ті самі ресурси,\n\nзначить я не помилився у своїх розрахунках.",
					"Одного дня мене назвуть або тираном, або богом.\n\nНасправді ж я лише намагався виграти час для світу."
			)
	);

	private static final BookDefinition ON_ROADS_AND_STONE = new BookDefinition(
			"on_roads_and_stone",
			"Про Дороги і Камінь",
			"PARA_22",
			ModItems.ON_ROADS_AND_STONE_BOOK,
			1.0F,
			VILLAGE_CHESTS,
			List.of(
					"Камінь не знає жалю.\n\nЯкщо його покласти неправильно — він упаде.\n\nІ байдуже, хто саме стоїть унизу.",
					"Багато хто думає, що найважче — видовбати камінь.\n\nНі.\n\nНайважче — вирішити, куди його покласти.",
					"Дорогу не будують для себе.\n\nЇї будують для людей, яких ти ніколи не зустрінеш.",
					"Якщо сьогодні зекономиш на одному блоці, через багато років хтось заплатить за це власним життям.",
					"Коли ти будуєш дорогу між двома містами, третє завжди запитає, чому не до нього.",
					"На це питання немає правильної відповіді.\n\nЄ лише відповідальність за власний вибір.",
					"Будівничий не може чекати, поки всі погодяться.\n\nЯкби ми чекали, дороги не з'явилися б ніколи.",
					"Та це не означає, що люди повинні мовчати.\n\nІноді вони бачать те, чого не бачиш ти.",
					"Я завжди слухаю тих, хто працює поруч.\n\nБо стіна падає не через слова.\n\nВона падає через погано покладений камінь.",
					"Не кожне рішення сподобається людям.\n\nТа якщо воно збереже міст через сто років — його варто було прийняти.",
					"Хорошу дорогу перестають помічати.\n\nПогану згадують щодня.",
					"Якщо після мене люди все ще ходитимуть цими шляхами, значить я будував не дарма."
			)
	);

	private static final BookDefinition ON_GARDENS_AND_TIME = new BookDefinition(
			"on_gardens_and_time",
			"Про Сади і Час",
			"Sleepwalking",
			ModItems.ON_GARDENS_AND_TIME_BOOK,
			1.0F,
			VILLAGE_CHESTS,
			List.of(
					"Люди часто питають мене, як виростити великий сад.\n\nВони ставлять неправильне питання.",
					"Правильне питання звучить інакше:\n\nЯким буде це місце через сто років?",
					"Місто починається не з першого будинку.\n\nВоно починається з першого дерева, яке ти вирішив не рубати.",
					"Я завжди просила будівничих спочатку пройтися місцевістю пішки.\n\nЗрозуміти, куди тече вода, де дме вітер і де вже народилося життя.",
					"Не можна будувати місто проти річки.\n\nРано чи пізно вона нагадає, що була тут раніше за нас.",
					"Мені часто дорікали, що я витрачаю час на сади, ставки та дерева.\n\nТа людина не може жити серед самого лише каменю.",
					"PARA_22 будував дороги.\n\nЯ садила дерева вздовж них.\n\nБо дорога без тіні швидко стає дорогою без мандрівників.",
					"Потужніч говорив про людей.\n\nMinecramet — про карти.\n\nЯ ж завжди думала про місця, куди ці люди повертатимуться додому.",
					"Не кожен клаптик землі потрібно забудувати.\n\nІноді найцінніше рішення — залишити простір порожнім.",
					"Дерево не знає, хто його посадив.\n\nВоно пам'ятає лише того, хто дозволив йому вирости.",
					"Можливо одного дня наші міста стануть руїнами.\n\nДороги заростуть травою, а площі вкриються квітами.",
					"Якщо навіть тоді люди скажуть:\n\n'Тут було гарне місце для життя.'\n\nЗначить ми будували правильно."
			)
	);

	private static final List<BookDefinition> BOOKS = List.of(
			CHRONICLE_OF_DEPARTURE,
			JUNGLE_TEMPLE_JOURNAL,
			BONES_IN_HELL,
			PRAYER_IN_THE_SAND,
			SEA_OF_TURTLES,
			BOOK_OF_THE_FOUR,
			THE_HIDDEN_TRUTH,
			WHEAT_FOR_EVERYONE,
			DECLARATION_OF_FREEDOM,
			NEW_ZOMBICHI_CHRONICLE,
			OCEAN_ECHOES,
			SKEPTIC_OF_THE_GARDEN,
			WEAKLAND_CAMPAIGN,
			HISTORY_OF_DISAPPEARANCE,
			ALEH_JOURNAL,
			MY_STRUGGLE,
			ON_ROADS_AND_STONE,
			ON_GARDENS_AND_TIME
	);

	private ModBooks() {
	}

	public static void initialize() {
		LootTableEvents.MODIFY.register((key, tableBuilder, source, registries) -> {
			// Only inject into chest-type tables, but into EVERY one of them (any namespace/mod),
			// minus the shared blacklist.
			if (!source.isBuiltin() || !ModLootTables.isInjectableChest(key, tableBuilder)) {
				return;
			}

			List<BookDefinition> books = matchingBooks(key);
			float chance;
			if (!books.isEmpty()) {
				// Themed / known chest: keep its specific book pool and chance.
				chance = bookChanceFor(key);
			} else {
				// Global fallback: any other chest (incl. modded ones we don't list, e.g. Stellarity)
				// gets the general overworld book set, so no chest is ever missed. Themed books stay
				// exclusive to their themed chests because they are excluded from this set.
				books = generalOverworldBooks();
				chance = BOOK_CHEST_CHANCE;
			}

			if (!books.isEmpty()) {
				tableBuilder.pool(createBookPool(books, chance).build());
			}
		});
	}

	/** The general "overworld" books used as the global fallback for any unlisted chest. */
	private static List<BookDefinition> generalOverworldBooks() {
		List<BookDefinition> books = new ArrayList<>();
		for (BookDefinition book : BOOKS) {
			if (isOverworldGeneral(book)) {
				books.add(book);
			}
		}
		return books;
	}

	/** The books that this mod can inject into the given loot table (empty if none). */
	public static List<BookDefinition> matchingBooks(ResourceKey<LootTable> key) {
		boolean generalChest = SIMPLE_DUNGEON_CHEST.equals(key) || GENERAL_BOOK_CHESTS.contains(key);
		boolean backupChest = isStrongholdChest(key) || isAncientCityChest(key);
		boolean overworldGeneralChest = OVERWORLD_GENERAL_CHESTS.contains(key);

		List<BookDefinition> matchingBooks = new ArrayList<>();
		for (BookDefinition book : BOOKS) {
			// Звичайні данжі отримують лише загальні книжки — жодної стихійної (тематичної).
			boolean general = generalChest && canSpawnInSimpleDungeon(book);
			// Запасний варіант (stronghold і ancient city): усі книжки, окрім незерської.
			boolean backup = backupChest && !book.id().equals("bones_in_hell");
			if (book.lootTables().contains(key)
					|| backup
					|| general
					|| overworldGeneralChest && isOverworldGeneral(book)) {
				matchingBooks.add(book);
			}
		}

		return matchingBooks;
	}

	/** Total number of distinct books the mod can generate across the whole world. */
	public static int totalBookCount() {
		return BOOKS.size();
	}

	private static float bookChanceFor(ResourceKey<LootTable> key) {
		if (isAncientCityChest(key)) {
			return ANCIENT_CITY_BOOK_CHANCE;
		}
		// Themed places (desert/jungle/ocean) use the themed chance; everything else
		// (stronghold / villages / dungeons / fallback / nether) uses the default.
		return isThemedChest(key) ? THEMED_BOOK_CHANCE : BOOK_CHEST_CHANCE;
	}

	// Detect strongholds by path so the rule works for vanilla (minecraft:chests/stronghold_*),
	// Stellarity (stellarity:stronghold/*) and any other mod that overhauls strongholds.
	private static boolean isStrongholdChest(ResourceKey<LootTable> key) {
		return key.location().getPath().contains("stronghold");
	}

	// Ancient city: detected by path so vanilla and any modded variant both count.
	private static boolean isAncientCityChest(ResourceKey<LootTable> key) {
		return key.location().getPath().contains("ancient_city");
	}

	// A "themed place" is any chest listed in an elemental book's own lootTables
	// (desert pyramid, jungle/ocean ruins, ancient_city, etc.). The nether book is excluded —
	// nether chests keep the default chance.
	private static boolean isThemedChest(ResourceKey<LootTable> key) {
		for (BookDefinition book : BOOKS) {
			if (!isOverworldGeneral(book)
					&& !book.id().equals("bones_in_hell")
					&& book.lootTables().contains(key)) {
				return true;
			}
		}
		return false;
	}

	private static LootPool.Builder createBookPool(List<BookDefinition> books, float chance) {
		LootPool.Builder pool = LootPool.lootPool()
				.setRolls(ConstantValue.exactly(1.0F))
				.when(LootItemRandomChanceCondition.randomChance(chance));

		for (BookDefinition book : books) {
			pool.add(LootItem.lootTableItem(book.item()).apply(() -> new UniqueBookLootFunction(books)));
			break;
		}

		return pool;
	}

	private static ResourceKey<LootTable> chest(String name) {
		return ResourceKey.create(Registries.LOOT_TABLE, ResourceLocation.fromNamespaceAndPath("minecraft", "chests/" + name));
	}

	private static ResourceKey<LootTable> customChest(String name) {
		return ResourceKey.create(Registries.LOOT_TABLE, ResourceLocation.fromNamespaceAndPath("custom-discs", "chests/" + name));
	}

	private static ResourceKey<LootTable> table(String namespace, String path) {
		return ResourceKey.create(Registries.LOOT_TABLE, ResourceLocation.fromNamespaceAndPath(namespace, path));
	}

	// General dungeons (simple dungeon, YUNG's dungeons, witch huts) get only the non-themed books.
	// Every elemental/themed book (nether, ocean, jungle, desert) is excluded here.
	private static boolean canSpawnInSimpleDungeon(BookDefinition book) {
		return !book.id().equals("bones_in_hell")
				&& !book.id().equals("sea_of_turtles")
				&& !book.id().equals("ocean_echoes")
				&& !book.id().equals("jungle_temple_journal")
				&& !book.id().equals("prayer_in_the_sand");
	}

	// General "overworld" books only: excludes the nether, ocean, desert and jungle themed books.
	private static boolean isOverworldGeneral(BookDefinition book) {
		String id = book.id();
		return !id.equals("bones_in_hell")
				&& !id.equals("sea_of_turtles")
				&& !id.equals("ocean_echoes")
				&& !id.equals("jungle_temple_journal")
				&& !id.equals("prayer_in_the_sand");
	}

	private static Set<ResourceKey<LootTable>> merge(Set<ResourceKey<LootTable>> first, Set<ResourceKey<LootTable>> second) {
		Set<ResourceKey<LootTable>> result = new java.util.HashSet<>(first);
		result.addAll(second);
		return Set.copyOf(result);
	}

	@SafeVarargs
	private static Set<ResourceKey<LootTable>> mergeAll(Set<ResourceKey<LootTable>>... sets) {
		Set<ResourceKey<LootTable>> result = new java.util.HashSet<>();
		for (Set<ResourceKey<LootTable>> set : sets) {
			result.addAll(set);
		}
		return Set.copyOf(result);
	}
}
