package net.famzangl.minecraft.minebot.ai.strategy;

import java.util.ArrayList;
import java.util.List;

import net.famzangl.minecraft.minebot.Pos;
import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.enchanting.CloseScreenTask;
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
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class UnstoreStrategy extends PathFinderStrategy {

	public static class Wishlist {
		InventoryDefinition wantedInventory;
		// int[] alreadyTaken = new int[36];
		boolean[] noMoreWork = new boolean[36];

		public Wishlist(InventoryDefinition wantedInventory) {
			this.wantedInventory = wantedInventory;
		}

		public boolean couldUseOneOf(ChestData c) {
			for (int i = 0; i < 36; i++) {
				InventorySlot slot = wantedInventory.getSlot(i);
				if (slot == null || noMoreWork[i]) {
					continue;
				}

				if (c.couldTakeItem(slot.getFakeMcStack())) {
					return true;
				}
			}
			return false;
		}

		public ArrayList<AITask> getTakeTasks(ItemStack[] mainInventory,
				ChestData c) {
			ArrayList<AITask> tasks = new ArrayList<AITask>();
			for (int inventorySlot = 0; inventorySlot < 36; inventorySlot++) {
				InventorySlot slot = wantedInventory.getSlot(inventorySlot);
				if (slot == null || !c.couldTakeItem(slot.getFakeMcStack())) {
					continue;
				}
				if (mainInventory[inventorySlot] != null) {
					if (!new SameItemFilter(mainInventory[inventorySlot])
							.matches(slot.getFakeMcStack())) {
						System.out
								.println("This slot already contains an other item.");
						noMoreWork[inventorySlot] = true;
						continue;
					} else if (mainInventory[inventorySlot].stackSize >= Math
							.min(mainInventory[inventorySlot].getMaxStackSize(),
									slot.amount)) {
						System.out
						.println("This slot already contains enough items.");
						noMoreWork[inventorySlot] = true;
						continue;
					}
				}

				tasks.add(getTask(inventorySlot, slot, c));
			}
			return tasks;
		}

		private MoveInInventoryTask getTask(final int inventorySlot,
				final InventorySlot slot, final ChestData c) {
			return new MoveInInventoryTask() {
				private int fromStack = -2;

				@Override
				public boolean isFinished(AIHelper h) {
					return noMoreWork[inventorySlot] || super.isFinished(h);
				}

				@Override
				protected int getToStack(AIHelper h) {
					GuiChest screen = (GuiChest) h.getMinecraft().currentScreen;
					int slots = screen.inventorySlots.inventorySlots.size();
					int iSlot;
					if (inventorySlot < 9) {
						iSlot = inventorySlot + 3 * 9;
					} else {
						iSlot = inventorySlot - 9;
					}
					return iSlot + (slots - 9 * 4);
				}

				@Override
				protected int getFromStack(AIHelper h) {
					if (fromStack == -2) {
						fromStack = -1;
						GuiChest screen = (GuiChest) h.getMinecraft().currentScreen;
						SameItemFilter filter = new SameItemFilter(
								slot.getFakeMcStack());
						List<Slot> inventorySlots = screen.inventorySlots.inventorySlots;
						for (int i = 0; i < inventorySlots.size() - 36; i++) {
							Slot s = inventorySlots.get(i);
							if (filter.matches(s.getStack())) {
								fromStack = i;
								break;
							}
						}
						if (fromStack < 0) {
							System.out.println("Empty stack.");
							c.markAsEmptyFor(slot.getFakeMcStack(), true);
						}
					}

					return fromStack;
				}

				@Override
				protected int getMissingAmount(AIHelper h, int currentCount) {
					return -currentCount + slot.amount;
				}
			};
		}
	}

	private static class UnstorePathFinder extends BlockRangeFinder {
		private final Wishlist list;

		private ChestBlockHandler chestBlockHandler;

		@Override
		protected BlockRangeScanner constructScanner(Pos playerPosition) {
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
			ArrayList<ChestData> chests = chestBlockHandler.getReachableForPos(new Pos(
					x, y, z));
			if (chests != null) {
				for (ChestData c : chests) {
					if (list.couldUseOneOf(c)) {
						return distance;
					}
				}
			}
			return -1;
		}

		@Override
		protected void addTasksForTarget(Pos currentPos) {
			ArrayList<ChestData> chests = chestBlockHandler
					.getReachableForPos(currentPos);
			for (final ChestData c : chests) {
				ItemStack[] inventory = helper.getMinecraft().thePlayer.inventory.mainInventory;
				ArrayList<AITask> tasks = list.getTakeTasks(inventory, c);

				if (!tasks.isEmpty()) {
					addTask(new OpenChestTask(c.getSecondaryPos(), c.getPos()));
					addTask(new WaitTask(5));
					for (AITask t : tasks) {
						addTask(t);
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
		if (helper.getMinecraft().currentScreen instanceof GuiChest) {
			addTask(new CloseScreenTask());
		}
		super.searchTasks(helper);
	}

	@Override
	public String getDescription(AIHelper helper) {
		return "Get items out of chest.";
	}

}
