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

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.command.BlockWithDataOrDontcare;
import net.famzangl.minecraft.minebot.ai.enchanting.CloseScreenTask;
import net.famzangl.minecraft.minebot.ai.path.world.BlockSet;
import net.famzangl.minecraft.minebot.ai.path.world.WorldData;
import net.famzangl.minecraft.minebot.ai.scanner.BlockRangeFinder;
import net.famzangl.minecraft.minebot.ai.scanner.BlockRangeScanner;
import net.famzangl.minecraft.minebot.ai.scanner.RangeBlockHandler;
import net.famzangl.minecraft.minebot.ai.task.UseItemOnBlockAtTask;
import net.famzangl.minecraft.minebot.ai.task.WaitTask;
import net.famzangl.minecraft.minebot.ai.task.error.TaskError;
import net.famzangl.minecraft.minebot.ai.task.inventory.ItemCountList;
import net.famzangl.minecraft.minebot.ai.task.inventory.ItemWithSubtype;
import net.famzangl.minecraft.minebot.ai.task.inventory.PutOnCraftingTableTask;
import net.famzangl.minecraft.minebot.ai.task.inventory.TakeResultItem;
import net.famzangl.minecraft.minebot.ai.utils.PrivateFieldUtils;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiCrafting;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.oredict.ShapedOreRecipe;

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

	public static final class CraftingPossibility {
		public static final int SUBTYPE_IGNORED = 32767;

		private final ItemWithSubtype[][][] slots = new ItemWithSubtype[3][3][];

		public CraftingPossibility(IRecipe recipe) {
			LOGGER.trace(MARKER_RECIPE, "Parsing recipe: " + recipe);
			if (recipe instanceof ShapedRecipes) {
				ShapedRecipes shapedRecipes = (ShapedRecipes) recipe;
				LOGGER.trace(MARKER_RECIPE, "Interpreting ShapedRecipes: "
						+ shapedRecipes.getRecipeOutput().getItem()
								.getUnlocalizedName());
				int width = shapedRecipes.recipeWidth;
				int height = shapedRecipes.recipeHeight;

				LOGGER.trace(MARKER_RECIPE, "Found items of size " + width
						+ "x" + height + ": " + shapedRecipes.recipeItems);
				for (int x = 0; x < width; x++) {
					for (int y = 0; y < height; y++) {
						Ingredient itemStack = shapedRecipes.recipeItems.get(x + y * width);
						if (itemStack != null && itemStack.getMatchingStacks().length > 0) {
							this.slots[x][y] = Stream.of(itemStack.getMatchingStacks())
									.map(ItemWithSubtype::new)
									.toArray(ItemWithSubtype[]::new);
						}
					}
				}
				LOGGER.trace(MARKER_RECIPE, "Slots " + Arrays.toString(slots));
			} else if (recipe instanceof ShapedOreRecipe) {
				ShapedOreRecipe shapedRecipes = (ShapedOreRecipe) recipe;
				try {
					// Width is the first integer field.
					int width = PrivateFieldUtils.getField(shapedRecipes, ShapedOreRecipe.class, Integer.TYPE).getInt(shapedRecipes);
					for (int x = 0; x < width; x++) {
						int height = shapedRecipes.getHeight();
						for (int y = 0; y < height; y++) {
							//TODO: Test
							Object itemStack = shapedRecipes.getIngredients().get(x + y
									* width);
							if (itemStack instanceof ItemStack) {
								this.slots[x][y] = new ItemWithSubtype[] { new ItemWithSubtype(
										(ItemStack) itemStack) };
							} else if (itemStack instanceof List) {
								List<ItemStack> list = (List<ItemStack>) itemStack;
								this.slots[x][y] = list.stream()
										.map(ItemWithSubtype::new)
										.toArray(ItemWithSubtype[]::new);
							} else if (itemStack != null) {
								LOGGER.error(MARKER_RECIPE, "Cannot handle " + itemStack.getClass());
								throw new IllegalArgumentException("Cannot handle " + itemStack.getClass());
							}
						}
					}
				} catch (SecurityException e) {
					throw new IllegalArgumentException("Cannot access " + recipe);
				} catch (IllegalAccessException e) {
					throw new IllegalArgumentException("Cannot access " + recipe);
				}
			} else {
				LOGGER.error(MARKER_RECIPE,
						"An item recipe has been found but the item cannot be crafted. The class "
								+ recipe.getClass().getCanonicalName()
								+ " cannot be understood.");
				throw new IllegalArgumentException("Cannot (yet) craft " + recipe);
			}
		}

		public ItemCountList getRequiredItems(int count) {
			ItemCountList list = new ItemCountList();
			for (ItemWithSubtype[][] subtypes : slots) {
				for (ItemWithSubtype[] subtype : subtypes) {
					list.add(subtype[0], count);
				}
			}
			LOGGER.trace(MARKER_RECIPE, "Items required for " + this + ": "
					+ list);
			return list;
		}

		public boolean goodForPosition(ItemWithSubtype item, int x, int y) {
			if (slots[x][y] != null) {
				if (item == null) {
					return false;
				} else {
					ItemWithSubtype genericItem = item.withSubtype(SUBTYPE_IGNORED);
					return Stream.of(slots[x][y]).anyMatch(
									slot -> slot.equals(item) || slot.equals(genericItem)
								);
				}
			} else {
				return item == null;
			}
		}

		@Override
		public String toString() {
			return "CraftingPossibility [slots=" + Arrays.deepToString(slots) + "]";
		}

	}

	public static final class CraftingWish {
		private final int amount;
		private final ItemWithSubtype item;

		public CraftingWish(int amount, ItemWithSubtype item) {
			this.amount = amount;
			this.item = item;
		}

		public List<CraftingPossibility> getPossibility() {
			try {
				return CraftingManager.REGISTRY.getKeys().stream()
						.map(id -> CraftingManager.REGISTRY.getObject(id))
						.filter(recipe -> {
							ItemStack out = recipe.getRecipeOutput();
							return out != null && new ItemWithSubtype(out).equals(item);
						})
						.map(CraftingPossibility::new)
						.collect(Collectors.toList());
			} catch (IllegalArgumentException e) {
				System.err.println("Cannot craft: " + e.getMessage());
				return Collections.emptyList();
			}
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
		private static final BlockSet IDS = new BlockSet(Blocks.CRAFTING_TABLE);

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
			List<CraftingPossibility> possibilities = wish.getPossibility();
			LOGGER.trace("Crafting one of: " + possibilities);

			ItemWithSubtype[][] grid = getCraftablePossibility(helper,
					possibilities);
			if (grid == null) {
				failed = true;
				System.err.println("Could not find any way to craft this.");
				// FIXME: Desync. Error.
				return;
			}
			
			LOGGER.trace(MARKER_RECIPE, "Crafting: " + Arrays.deepToString(grid));

			addTask(new UseItemOnBlockAtTask(table.pos) {
				@Override
				protected boolean isBlockAllowed(AIHelper aiHelper, BlockPos pos) {
					return aiHelper.getBlock(pos) == Blocks.CRAFTING_TABLE;
				}

				@Override
				public boolean isFinished(AIHelper aiHelper) {
					return super.isFinished(aiHelper)
							&& aiHelper.getMinecraft().currentScreen instanceof GuiCrafting;
				}
			});
			addCraftTaks(grid);
			addTask(new WaitTask(5));
			addTask(new TakeResultItem(GuiCrafting.class, 0));
			addTask(new WaitTask(5));
			addTask(new CloseScreenTask());
		}

		private void addCraftTaks(ItemWithSubtype[][] grid) {
			int missing = getMissing();
			if (missing <= 0) {
				return;
			}
			for (int x = 0; x < 3; x++) {
				for (int y = 0; y < 3; y++) {
					if (grid[x][y] != null) {
						int inventoryTotal = countInInventory(grid[x][y]);
						int slotCount = countInGrid(grid, grid[x][y]);
						int itemCount = Math.min(inventoryTotal / slotCount,
								missing);
						addTask(new PutOnCraftingTableTask(y * 3 + x,
								grid[x][y], itemCount));
						addTask(new WaitTask(3));
					}
				}
			}
		}

		private int countInGrid(ItemWithSubtype[][] grid,
				ItemWithSubtype itemWithSubtype) {
			int count = 0;
			for (ItemWithSubtype[] ss : grid) {
				for (ItemWithSubtype s : ss) {
					if (itemWithSubtype.equals(s)) {
						count++;
					}
				}
			}
			return count;
		}

		private int countInInventory(ItemWithSubtype itemWithSubtype) {
			int count = 0;
			for (ItemStack stack : helper.getMinecraft().player.inventory.mainInventory) {
				if (itemWithSubtype.equals(ItemWithSubtype.fromStack(stack))) {
					count += stack.getMaxStackSize();
				}
			}
			return count;
		}

		/**
		 * Gets an array of items that specifies how they need to be placed on
		 * the crafting grid.
		 * 
		 * @param aiHelper
		 * @param possibilities
		 * @return
		 */
		private ItemWithSubtype[][] getCraftablePossibility(AIHelper aiHelper,
				List<CraftingPossibility> possibilities) {
			for (CraftingPossibility possibility : possibilities) {
				ItemWithSubtype[][] assignedSlots = new ItemWithSubtype[3][3];
				// TODO: Order this in a better way. We need to have multiples of our item count first.
				for (ItemStack stack : aiHelper.getMinecraft().player.inventory.mainInventory) {
					if (stack == null) {
						continue;
					}
					ItemWithSubtype item = new ItemWithSubtype(stack);
					int leftOver = stack.getMaxStackSize();
					for (int x = 0; x < 3 && leftOver > 0; x++) {
						for (int y = 0; y < 3 && leftOver > 0; y++) {
							if (possibility.goodForPosition(item, x, y)
									&& assignedSlots[x][y] == null) {
								assignedSlots[x][y] = item;
								leftOver--;
								LOGGER.trace("Placing at " + x + "," + y + ": "
										+ item);
							}
						}
					}
				}
				boolean allGood = true;
				for (int x = 0; x < 3; x++) {
					for (int y = 0; y < 3; y++) {
						if (!possibility.goodForPosition(assignedSlots[x][y], x, y)) {
							allGood = false;
							LOGGER.warn(MARKER_RECIPE, "Placed wrong item at "
									+ x + "," + y + ": " + assignedSlots[x][y]);
						}
					}
				}
				if (allGood) {
					return assignedSlots;
				}
			}
			LOGGER.warn("Could not find any way to craft any of "
					+ possibilities);
			return null;
		}

		@Override
		public String toString() {
			return "CraftingTableFinder [wish=" + wish + ", failed=" + failed
					+ ", oldItemCount=" + oldItemCount + "]";
		}
	}

	public CraftStrategy(int amount, int itemId, int subtype) {
		this(amount, new ItemWithSubtype(itemId, subtype));
	}

	public CraftStrategy(int amount, ItemWithSubtype item) {
		super(new CraftingTableFinder(new CraftingWish(amount, item)),
				"Crafting");
	}

	public CraftStrategy(int amount, BlockWithDataOrDontcare itemType) {
		this(amount, itemType.getItemType());
	}

	@Override
	public void searchTasks(AIHelper helper) {
		// If chest open, close it.
		if (helper.getMinecraft().currentScreen instanceof GuiContainer) {
			addTask(new CloseScreenTask());
		}
		super.searchTasks(helper);
	}

	@Override
	public String getDescription(AIHelper helper) {
		return "Craft items.";
	}

}
