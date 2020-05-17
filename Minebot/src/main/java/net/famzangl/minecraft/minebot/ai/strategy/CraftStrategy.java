/*******************************************************************************
 * This file is part of Minebot.
 *
 * Minebot is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Minebot is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Minebot.  If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
package net.famzangl.minecraft.minebot.ai.strategy;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.command.AIChatController;
import net.famzangl.minecraft.minebot.ai.enchanting.CloseScreenTask;
import net.famzangl.minecraft.minebot.ai.path.world.BlockSet;
import net.famzangl.minecraft.minebot.ai.path.world.WorldData;
import net.famzangl.minecraft.minebot.ai.scanner.BlockRangeFinder;
import net.famzangl.minecraft.minebot.ai.scanner.BlockRangeScanner;
import net.famzangl.minecraft.minebot.ai.scanner.RangeBlockHandler;
import net.famzangl.minecraft.minebot.ai.task.UseItemOnBlockAtTask;
import net.famzangl.minecraft.minebot.ai.task.WaitTask;
import net.famzangl.minecraft.minebot.ai.task.error.TaskError;
import net.famzangl.minecraft.minebot.ai.task.inventory.ItemWithSubtype;
import net.famzangl.minecraft.minebot.ai.task.inventory.PutOnCraftingTableTask;
import net.famzangl.minecraft.minebot.ai.task.inventory.TakeResultItem;
import net.minecraft.block.Blocks;
import net.minecraft.client.gui.recipebook.RecipeList;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.screen.inventory.CraftingScreen;
import net.minecraft.client.util.ClientRecipeBook;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipePlacer;
import net.minecraft.item.crafting.RecipeItemHelper;
import net.minecraft.util.math.BlockPos;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Use the crafting table.
 * 
 * @author michael
 *
 */
public class CraftStrategy extends PathFinderStrategy {
	private static final Marker MARKER_RECIPE = MarkerManager
			.getMarker("recipe");
	private static final Logger LOGGER = LogManager
			.getLogger(CraftStrategy.class);

	/**
	 * Apply all RecipeWithListAndCount in that order (used for multiple crafting steps)
	 */
	public static final class CraftingPossibility {

		private final ItemWithSubtype[][][] slots = new ItemWithSubtype[3][3][];
		private List<RecipeWithListAndCount> recipesToApply;

		public CraftingPossibility(List<RecipeWithListAndCount> recipesToApply) {
			this.recipesToApply = recipesToApply;
		}

		public List<RecipeWithListAndCount> getRecipesToApply() {
			return recipesToApply;
		}

		@Override
		public String toString() {
			return "CraftingPossibility [slots=" + Arrays.deepToString(slots) + "]";
		}

	}

	public static class RecipePlacement{
		private final ItemStack[][] stacksToPlace;

		public RecipePlacement(int width, int height) {
			this.stacksToPlace = new ItemStack[height][width];
			for (int x = 0; x < width; x++) {
				for (int y = 0; y < height; y++) {
					stacksToPlace[y][x] = new ItemStack(() -> Items.AIR, 0);
				}
			}
		}

		public int getWidht() {
			return stacksToPlace[0].length;
		}

		public int getHeight() {
			return stacksToPlace.length;
		}

		static RecipePlacement of(int size, IRecipe<?> recipe, Iterator<Integer> ingredients, int count) {
			RecipePlacement placement = new RecipePlacement(size, size);

			new IRecipePlacer<Integer>(){
				@Override
				public void setSlotContents(Iterator<Integer> ingredients, int slotIn, int maxAmount, int y, int x) {
					ItemStack itemstack = RecipeItemHelper.unpack(ingredients.next());
					LOGGER.trace("Placing stack at {},{}: {}", x, y, itemstack);
					placement.stacksToPlace[y][x] = itemstack;
				}
			}.placeRecipe(size, size, -1, recipe, ingredients, count);

			return placement;
		}

		@Nonnull
		public ItemStack getStack(int x, int y) {
			return stacksToPlace[y][x];
		}

		@Override
		public String toString() {
			return "RecipePlacement{" +
					"stacksToPlace=" + Arrays.toString(stacksToPlace) +
					'}';
		}

		public int getSlotsWithType(Item item) {
			return (int) Stream.of(stacksToPlace)
					.flatMap(Stream::of)
					.filter(it -> it.getItem() == item)
					.count();
		}
	}

	/**
	 * A recipe and how often it should be used
	 */
	public static class RecipeWithListAndCount{
		private final RecipeList list;
		private final IRecipe<?> recipe;
		private final int count;
		private final RecipeItemHelper helper = new RecipeItemHelper();

		public RecipeWithListAndCount(RecipeList list, IRecipe<?> recipe, int count) {
			this.list = list;
			this.recipe = recipe;
			this.count = count;
		}

		public Optional<RecipePlacement> getPlacement(AIHelper aiHelper) {
			LOGGER.trace(MARKER_RECIPE, "Attempt to place recipe {} ", recipe);

			helper.clear();
			// Now determine all te items we have in the inventory
			aiHelper.getMinecraft().player.inventory.accountStacks(helper);
			int realMaxCount = Math.min(count, helper.getBiggestCraftableStack(recipe, null));
			LOGGER.trace(MARKER_RECIPE, "Requested {} stacks. Attempting to caraft {} stacks.", count, realMaxCount);

			IntList intlist = new IntArrayList();
			if (helper.canCraft(recipe, intlist, realMaxCount)) {
				return Optional.of(RecipePlacement.of(3, recipe, intlist.iterator(), realMaxCount));
			} else {
				return Optional.empty();
			}
		}

		@Override
		public String toString() {
			return "RecipeWithListAndCount{" +
					"list=" + list +
					", recipe=" + recipe +
					", count=" + count +
					'}';
		}
	}

	public static final class CraftingWish {
		private final int amount;
		private final Item item;

		public CraftingWish(int amount, Item item) {
			this.amount = amount;
			this.item = item;
		}

		public List<CraftingPossibility> getPossibility(AIHelper helper) {
			ClientRecipeBook book = helper.getMinecraft().player.getRecipeBook();
			// Minecraft stores a list of list of recipies.
			List<RecipeList> available = book.getRecipes();

			// Now find the one we need
			List<RecipeWithListAndCount> found = new ArrayList<>();
			available.forEach(list ->
					list.getRecipes().forEach(recipe -> {
						ItemStack out = recipe.getRecipeOutput();
						if (out.getItem() == item) {
							found.add(new RecipeWithListAndCount(list, recipe,
									// ceil
									(amount + out.getCount() - 1) / out.getCount()));
						}
					}));

			LOGGER.debug(MARKER_RECIPE, "For crafting {}, the following recipes were found: {}", item, found);


			return found.stream().map(it -> new CraftingPossibility(Collections.singletonList(it))).collect(Collectors.toList());
		}

		@Override
		public String toString() {
			return "CraftingWish [amount=" + amount + ", item=" + item + "]";
		}
	}

	public static class CraftingTableData {

		public final BlockPos pos;

		public CraftingTableData(BlockPos pos) {
			this.pos = pos;
		}

		@Override
		public String toString() {
			return "CraftingTableData [" + pos + "]";
		}
	}

	public final static class CraftingTableHandler extends
			RangeBlockHandler<CraftingTableData> {
		private static final BlockSet IDS = BlockSet.builder().add(Blocks.CRAFTING_TABLE).build();

		private final Hashtable<BlockPos, CraftingTableData> found = new Hashtable<BlockPos, CraftingTableData>();

		@Override
		public BlockSet getIds() {
			return IDS;
		}

		@Override
		public void scanBlock(WorldData world, int id, int x, int y, int z) {
			BlockPos pos = new BlockPos(x, y, z);
			found.put(pos, new CraftingTableData(pos));
		}

		@Override
		protected Collection<Entry<BlockPos, CraftingTableData>> getTargetPositions() {
			return found.entrySet();
		}
	}

	public static class CannotCraftError extends TaskError {

		public CannotCraftError(CraftingWish wish) {
			super("Cannor craft " + wish.item);
		}

		@Override
		public String toString() {
			return "CannotCraftError []";
		}
	}

	public static class CraftingTableFinder extends BlockRangeFinder {

		private final CraftingWish wish;
		CraftingTableHandler craftingTableHandler = new CraftingTableHandler();
		private boolean failed;
		private int oldItemCount = -1;

		public CraftingTableFinder(CraftingWish wish) {
			this.wish = wish;
		}

		@Override
		protected boolean runSearch(BlockPos playerPosition) {
			int missing = getMissing();
			if (failed || missing <= 0) {
				return true;
			}
			return super.runSearch(playerPosition);
		}

		/**
		 * Might be negative.
		 * 
		 * @return
		 */
		private int getMissing() {
			int itemCount = countInInventory(wish.item);
			if (oldItemCount < 0) {
				oldItemCount = itemCount;
			}
			return oldItemCount + wish.amount - itemCount;
		}

		@Override
		protected BlockRangeScanner constructScanner(BlockPos playerPosition) {
			BlockRangeScanner scanner = super.constructScanner(playerPosition);
			scanner.addHandler(craftingTableHandler);
			return scanner;
		}

		@Override
		protected float rateDestination(int distance, int x, int y, int z) {
			ArrayList<CraftingTableData> tables = craftingTableHandler
					.getReachableForPos(new BlockPos(x, y, z));
			return !failed && tables != null && tables.size() > 0 ? distance
					: -1;
		}

		@Override
		protected void addTasksForTarget(BlockPos currentPos) {
			ArrayList<CraftingTableData> tables = craftingTableHandler
					.getReachableForPos(currentPos);
			CraftingTableData table = tables.get(0);
			List<CraftingPossibility> possibilities = wish.getPossibility(helper);
			LOGGER.trace("Crafting one of: " + possibilities);

			Optional<RecipePlacement> grid = possibilities
				.stream()
					// For now, only support one recipe. TODO: Find out how to best support multiple of them
					.map(p -> p.getRecipesToApply().get(0))
					.map(p -> p.getPlacement(helper))
					.flatMap(optional -> optional.isPresent() ? Stream.of(optional.get()) : Stream.of())
					.findAny();
			if (!grid.isPresent()) {
				failed = true;
				AIChatController.addChatLine("Could not find a way to craft this item");
				return;
			}
			
			LOGGER.trace(MARKER_RECIPE, "Crafting: {}", grid.get());

			addTask(new UseItemOnBlockAtTask(table.pos) {
				@Override
				protected boolean isBlockAllowed(AIHelper aiHelper, BlockPos pos) {
					return aiHelper.getBlock(pos) == Blocks.CRAFTING_TABLE;
				}

				@Override
				public boolean isFinished(AIHelper aiHelper) {
					return super.isFinished(aiHelper)
							&& aiHelper.getMinecraft().currentScreen instanceof CraftingScreen;
				}
			});
			addCraftTaks(grid.get());
			addTask(new WaitTask(5));
			addTask(new TakeResultItem(CraftingScreen.class, 0));
			addTask(new WaitTask(5));
			addTask(new CloseScreenTask());
		}

		private void addCraftTaks(RecipePlacement grid) {
			int missing = getMissing();
			if (missing <= 0) {
				return;
			}
			for (int x = 0; x < 3; x++) {
				for (int y = 0; y < 3; y++) {
					ItemStack stack = grid.getStack(x, y);
					if (stack.getItem() != Items.AIR) {
						int inventoryTotal = countInInventory(stack.getItem());
						int slotsWithThatType = grid.getSlotsWithType(stack.getItem());
						int itemCount = Math.min(inventoryTotal / slotsWithThatType,
								missing);
						addTask(new PutOnCraftingTableTask(y * 3 + x,
								stack.getItem(), itemCount));
						addTask(new WaitTask(3));
					}
				}
			}
		}

		private int countInInventory(Item itemWithSubtype) {
			int count = 0;
			for (ItemStack stack : helper.getMinecraft().player.inventory.mainInventory) {
				if (itemWithSubtype.equals(stack.getItem())) {
					count += stack.getCount();
				}
			}
			return count;
		}

		@Override
		public String toString() {
			return "CraftingTableFinder [wish=" + wish + ", failed=" + failed
					+ ", oldItemCount=" + oldItemCount + "]";
		}
	}

	public CraftStrategy(int amount, Item item) {
		super(new CraftingTableFinder(new CraftingWish(amount, item)),
				"Crafting");
	}

	@Override
	public void searchTasks(AIHelper helper) {
		// If chest open, close it.
		if (helper.getMinecraft().currentScreen instanceof ContainerScreen) {
			addTask(new CloseScreenTask());
		}
		super.searchTasks(helper);
	}

	@Override
	public String getDescription(AIHelper helper) {
		return "Craft items.";
	}

}
