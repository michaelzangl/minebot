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

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.enchanting.CloseScreenTask;
import net.famzangl.minecraft.minebot.ai.path.world.WorldData;
import net.famzangl.minecraft.minebot.ai.scanner.BlockRangeFinder;
import net.famzangl.minecraft.minebot.ai.scanner.BlockRangeScanner;
import net.famzangl.minecraft.minebot.ai.scanner.ChestBlockHandler;
import net.famzangl.minecraft.minebot.ai.scanner.ChestBlockHandler.ChestData;
import net.famzangl.minecraft.minebot.ai.scanner.SameItemFilter;
import net.famzangl.minecraft.minebot.ai.strategy.InventoryDefinition.InventorySlot;
import net.famzangl.minecraft.minebot.ai.task.AITask;
import net.famzangl.minecraft.minebot.ai.task.OpenChestTask;
import net.famzangl.minecraft.minebot.ai.task.WaitTask;
import net.famzangl.minecraft.minebot.ai.task.inventory.MoveInInventoryTask;
import net.minecraft.client.gui.screen.inventory.ChestScreen;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;

public class UnstoreStrategy extends PathFinderStrategy {

	public static class Wishlist {
		InventoryDefinition wantedInventory;
		// int[] alreadyTaken = new int[36];
		boolean[] noMoreWork = new boolean[36];

		public Wishlist(InventoryDefinition wantedInventory) {
			this.wantedInventory = wantedInventory;
		}

		public boolean couldUseOneOf(ChestData chestData, WorldData world) {
			for (int i = 0; i < 36; i++) {
				InventorySlot slot = wantedInventory.getSlot(i);
				if (slot.isEmpty() || noMoreWork[i]) {
					continue;
				}

				if (chestData.couldTakeItem(slot.getFakeMcStack(), world)) {
					return true;
				}
			}
			return false;
		}

		public ArrayList<AITask> getTakeTasks(List<ItemStack> inventory,
				ChestData chestData, WorldData world) {
			ArrayList<AITask> tasks = new ArrayList<AITask>();
			for (int inventorySlot = 0; inventorySlot < 36; inventorySlot++) {
				InventorySlot slot = wantedInventory.getSlot(inventorySlot);
				if (slot.isEmpty() || !chestData.couldTakeItem(slot.getFakeMcStack(), world)) {
					continue;
				}
				ItemStack itemInSlot = inventory.get(inventorySlot);
				if (itemInSlot != null && itemInSlot.getCount() > 0) {
					if (!new SameItemFilter(itemInSlot)
							.matches(slot.getFakeMcStack())) {
						System.out
								.println("This slot already contains an other item.");
						noMoreWork[inventorySlot] = true;
						continue;
					} else if (itemInSlot.getCount() >= Math.min(itemInSlot.getMaxStackSize(), slot.amount)) {
						System.out
								.println("This slot already contains enough items.");
						noMoreWork[inventorySlot] = true;
						continue;
					}
				}

				tasks.add(getTask(inventorySlot, slot, chestData));
			}
			return tasks;
		}

		private MoveInInventoryTask getTask(final int inventorySlot,
				final InventorySlot slot, final ChestData chestData) {
			return new MoveInInventoryTask() {
				private int fromStack = -2;

				@Override
				public boolean isFinished(AIHelper aiHelper) {
					return noMoreWork[inventorySlot] || super.isFinished(aiHelper);
				}

				@Override
				protected int getToStack(AIHelper aiHelper) {
					ChestScreen screen = (ChestScreen) aiHelper.getMinecraft().currentScreen;
					int slots = screen.getContainer().inventorySlots.size();
					int iSlot;
					if (inventorySlot < 9) {
						iSlot = inventorySlot + 3 * 9;
					} else {
						iSlot = inventorySlot - 9;
					}
					return iSlot + (slots - 9 * 4);
				}

				@Override
				protected int getFromStack(AIHelper aiHelper) {
					fromStack = -1;
					ChestScreen screen = (ChestScreen) aiHelper.getMinecraft().currentScreen;
					SameItemFilter filter = new SameItemFilter(
							slot.getFakeMcStack());
					List<Slot> inventorySlots = screen.getContainer().inventorySlots;
					int fromStackRating = -1;
					int missing = getMissingAmount(aiHelper,
							getSlotContentCount(screen.getContainer()
									.getSlot(getToStack(aiHelper))));
					for (int i = 0; i < inventorySlots.size() - 36; i++) {
						Slot inventorySlot = inventorySlots.get(i);
						if (filter.matches(inventorySlot.getStack())) {
							int rating = rateSize(aiHelper, getSlotContentCount(inventorySlot), missing);
							if (rating > fromStackRating) {
								fromStackRating = rating;
								fromStack = i;
							}
						}
					}
					if (fromStack < 0) {
						System.out.println("Empty stack.");
						chestData.markAsEmptyFor(slot.getFakeMcStack(), true);
					}

					return fromStack;
				}

				private int rateSize(AIHelper aiHelper, int stackSize, int missing) {
					if (stackSize == missing) {
						return 4;
					} else if (stackSize == missing * 2) {
						return 3;
					} else if (stackSize < missing) {
						return 2;
					} else {
						return 1;
					}
				}

				@Override
				protected int getMissingAmount(AIHelper aiHelper, int currentCount) {
					return -currentCount + slot.amount;
				}
			};
		}
	}

	private static class UnstorePathFinder extends BlockRangeFinder {
		private final Wishlist list;

		private ChestBlockHandler chestBlockHandler;

		@Override
		protected BlockRangeScanner constructScanner(BlockPos playerPosition) {
			BlockRangeScanner scanner = super.constructScanner(playerPosition);
			chestBlockHandler = new ChestBlockHandler();
			scanner.addHandler(chestBlockHandler);
			return scanner;
		}

		public UnstorePathFinder(Wishlist list) {
			this.list = list;
		}

		@Override
		protected float rateDestination(int distance, int x, int y, int z) {
			ArrayList<ChestData> chests = chestBlockHandler
					.getReachableForPos(new BlockPos(x, y, z));
			if (chests != null) {
				for (ChestData chestData : chests) {
					if (list.couldUseOneOf(chestData, world)) {
						return distance;
					}
				}
			}
			return -1;
		}

		@Override
		protected void addTasksForTarget(BlockPos currentPos) {
			ArrayList<ChestData> chests = chestBlockHandler
					.getReachableForPos(currentPos);
			for (final ChestData chestData : chests) {
				NonNullList<ItemStack> inventory = helper.getMinecraft().player.inventory.mainInventory;
				ArrayList<AITask> tasks = list.getTakeTasks(inventory, chestData, helper.getWorld());

				if (!tasks.isEmpty()) {
					addTask(new OpenChestTask(chestData.getSecondaryPos(), chestData.getPos()));
					addTask(new WaitTask(5));
					for (AITask aiTask : tasks) {
						addTask(aiTask);
						addTask(new WaitTask(5));
					}
					addTask(new CloseScreenTask());
					addTask(new WaitTask(5));
					break;
				}

			}
		}
	}

	public UnstoreStrategy(Wishlist list) {
		super(new UnstorePathFinder(list), null);
	}

	@Override
	public void searchTasks(AIHelper helper) {
		// If chest open, close it.
		if (helper.getMinecraft().currentScreen instanceof ChestScreen) {
			addTask(new CloseScreenTask());
		}
		super.searchTasks(helper);
	}

	@Override
	public String getDescription(AIHelper helper) {
		return "Get items out of chest.";
	}

}
