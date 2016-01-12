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
import java.util.Hashtable;
import java.util.List;
import java.util.Map.Entry;

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
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.util.BlockPos;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

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

		private static final int WIDTH = 0;
		private static final int HEIGHT = 1;

		private final ItemWithSubtype[][] slots = new ItemWithSubtype[3][3];

		public CraftingPossibility(IRecipe r) {
			LOGGER.trace(MARKER_RECIPE, "Parsing recipe: " + r);
			if (r instanceof ShapedRecipes) {
				ShapedRecipes shapedRecipes = (ShapedRecipes) r;
				LOGGER.trace(MARKER_RECIPE, "Interpreting ShapedRecipes: "
						+ shapedRecipes.getRecipeOutput().getItem()
								.getUnlocalizedName());
				int[] dim = getSizes(shapedRecipes);

				ItemStack[] items = PrivateFieldUtils.getFieldValue(
						shapedRecipes, ShapedRecipes.class, ItemStack[].class);
				LOGGER.trace(MARKER_RECIPE, "Found items of size " + dim[WIDTH]
						+ "x" + dim[HEIGHT] + ": " + Arrays.toString(items));
				for (int x = 0; x < dim[WIDTH]; x++) {
					for (int y = 0; y < dim[HEIGHT]; y++) {
						ItemStack itemStack = items[x + y * dim[WIDTH]];
						if (itemStack != null) {
							this.slots[x][y] = new ItemWithSubtype(itemStack);
						}
					}
				}
				LOGGER.trace(MARKER_RECIPE, "Slots " + Arrays.toString(slots));
			}
			// else if (r instanceof ShapedOreRecipe) { ShapedOreRecipe
			// shapedRecipes = (ShapedOreRecipe) r; try { Field widthFiled =
			// ShapedOreRecipe.class .getDeclaredField("width");
			// widthFiled.setAccessible(true); int width =
			// widthFiled.getInt(shapedRecipes); for (int x = 0; x < width; x++)
			// { int height = shapedRecipes.getRecipeSize() / width; for (int y
			// = 0; y < height; y++) { Object itemStack =
			// shapedRecipes.getInput()[x + y width]; if (itemStack instanceof
			// ItemStack) { this.slots[x][y] = new ItemWithSubtype( (ItemStack)
			// itemStack); } else if (itemStack instanceof ArrayList) {
			// ArrayList list = (ArrayList) itemStack; this.slots[x][y] = new
			// ItemWithSubtype( (ItemStack) list.get(0)); } } } } catch
			// (NoSuchFieldException e) { throw new
			// IllegalArgumentException("Cannot access " + r); } catch
			// (SecurityException e) { throw new
			// IllegalArgumentException("Cannot access " + r); } catch
			// (IllegalAccessException e) { throw new
			// IllegalArgumentException("Cannot access " + r); }
			// }
			else {
				LOGGER.error(MARKER_RECIPE,
						"An item recipe has been found but the item cannot be crafted. The class "
								+ r.getClass().getCanonicalName()
								+ " cannot be understood.");
				throw new IllegalArgumentException("Cannot (yet) craft " + r);
			}
		}

		private ItemStack[] getItems(ShapedRecipes shapedRecipes) {
			for (Field f : ShapedRecipes.class.getDeclaredFields()) {
				if (f.getType().isArray()) {
					Class<?> componentType = f.getType().getComponentType();
					if (componentType == ItemStack.class) {
						f.setAccessible(true);
						try {
							return (ItemStack[]) f.get(shapedRecipes);
						} catch (IllegalArgumentException e) {
							e.printStackTrace();
						} catch (IllegalAccessException e) {
							e.printStackTrace();
						}
					}
				}
			}
			return null;
		}

		private int[] getSizes(ShapedRecipes shapedRecipes) {
			int i = 0;
			int[] sizes = new int[2];
			for (Field f : ShapedRecipes.class.getDeclaredFields()) {
				if (f.getType() == Integer.TYPE) {
					f.setAccessible(true);
					try {
						sizes[i] = f.getInt(shapedRecipes);
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					}
					i++;
					if (i >= sizes.length) {
						break;
					}
				}
			}
			return sizes;
		}

		public ItemCountList getRequiredItems(int count) {
			ItemCountList list = new ItemCountList();
			for (ItemWithSubtype[] s : slots) {
				for (ItemWithSubtype ss : s) {
					list.add(ss, count);
				}
			}
			LOGGER.trace(MARKER_RECIPE, "Items required for " + this + ": " + list);
			return list;
		}

		public boolean goodForPosition(ItemWithSubtype item, int x, int y) {
			return slots[x][y] != null ? item != null
					&& (slots[x][y].equals(item) || slots[x][y].equals(item
							.withSubtype(SUBTYPE_IGNORED))) : item == null;
		}

		@Override
		public String toString() {
			return "CraftingPossibility [slots=" + Arrays.toString(slots) + "]";
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
			List<IRecipe> recipes = CraftingManager.getInstance()
					.getRecipeList();
			List<CraftingPossibility> possible = new ArrayList<CraftingPossibility>();

			for (IRecipe r : recipes) {
				ItemStack out = r.getRecipeOutput();
				if (out != null && new ItemWithSubtype(out).equals(item)) {
					try {
						possible.add(new CraftingPossibility(r));
					} catch (IllegalArgumentException e) {
						System.err.println("Cannot craft:" + e.getMessage());
					}
				}
			}
			return possible;
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
		private static final BlockSet IDS = new BlockSet(Blocks.crafting_table);

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
		CraftingTableHandler h = new CraftingTableHandler();
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
			scanner.addHandler(h);
			return scanner;
		}

		@Override
		protected float rateDestination(int distance, int x, int y, int z) {
			ArrayList<CraftingTableData> tables = h
					.getReachableForPos(new BlockPos(x, y, z));
			return !failed && tables != null && tables.size() > 0 ? distance
					: -1;
		}

		@Override
		protected void addTasksForTarget(BlockPos currentPos) {
			ArrayList<CraftingTableData> tables = h
					.getReachableForPos(currentPos);
			CraftingTableData table = tables.get(0);
			List<CraftingPossibility> possibilities = wish.getPossibility();
			System.out.println("Crafting one of: " + possibilities);

			ItemWithSubtype[][] grid = getCraftablePossibility(helper,
					possibilities);
			if (grid == null) {
				failed = true;
				System.err.println("Could not find any way to craft this.");
				// FIXME: Desync. Error.
				return;
			}

			addTask(new UseItemOnBlockAtTask(table.pos) {
				@Override
				protected boolean isBlockAllowed(AIHelper h, BlockPos pos) {
					return h.getBlock(pos) == Blocks.crafting_table;
				}

				@Override
				public boolean isFinished(AIHelper h) {
					return super.isFinished(h)
							&& h.getMinecraft().currentScreen instanceof GuiCrafting;
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
			for (ItemStack s : helper.getMinecraft().thePlayer.inventory.mainInventory) {
				if (itemWithSubtype.equals(ItemWithSubtype.fromStack(s))) {
					count += s.stackSize;
				}
			}
			return count;
		}

		/**
		 * Gets an array of items that specifies how they need to be placed on the crafting grid.
		 * @param h
		 * @param possibilities
		 * @return
		 */
		private ItemWithSubtype[][] getCraftablePossibility(AIHelper h,
				List<CraftingPossibility> possibilities) {
			for (CraftingPossibility p : possibilities) {
				ItemWithSubtype[][] assignedSlots = new ItemWithSubtype[3][3];
				// TODO: Order this in a better way. We need to have multiples of our item count first.
				for (ItemStack i : h.getMinecraft().thePlayer.inventory.mainInventory) {
					if (i == null) {
						continue;
					}
					ItemWithSubtype item = new ItemWithSubtype(i);
					int leftOver = i.stackSize;
					for (int x = 0; x < 3 && leftOver > 0; x++) {
						for (int y = 0; y < 3 && leftOver > 0; y++) {
							if (p.goodForPosition(item, x, y)
									&& assignedSlots[x][y] == null) {
								assignedSlots[x][y] = item;
								leftOver--;
								LOGGER.trace("Placing at "+ x + "," + y + ": " + item);
							}
						}
					}
				}
				boolean allGood = true;
				for (int x = 0; x < 3; x++) {
					for (int y = 0; y < 3; y++) {
						if (!p.goodForPosition(assignedSlots[x][y], x, y)) {
							allGood = false;
							LOGGER.warn(MARKER_RECIPE, "Placed wrong item at " + x + "," + y + ": " + assignedSlots[x][y]);
						}
					}
				}
				if (allGood) {
					return assignedSlots;
				}
			}
			LOGGER.warn("Could not find any way to craft any of " + possibilities);
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
		super(new CraftingTableFinder(new CraftingWish(amount,
				item)), "Crafting");
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
		return "Get items out of chest.";
	}

}
