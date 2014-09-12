package net.famzangl.minecraft.minebot.ai.strategy;

import java.util.ArrayList;

import net.famzangl.minecraft.minebot.Pos;
import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.enchanting.CloseScreenTask;
import net.famzangl.minecraft.minebot.ai.path.MovePathFinder;
import net.famzangl.minecraft.minebot.ai.scanner.BlockRangeScanner;
import net.famzangl.minecraft.minebot.ai.scanner.BlockRangeScanner.ChestData;
import net.famzangl.minecraft.minebot.ai.task.OpenChestTask;
import net.famzangl.minecraft.minebot.ai.task.PutInChestTask;
import net.famzangl.minecraft.minebot.ai.task.WaitTask;
import net.minecraft.block.Block;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

/**
 * Store whatever you are holding in a chest.
 * 
 * @author michael
 * 
 */
public class StoreStrategy extends PathFinderStrategy {
	
	public static class BlockRangeFinder extends MovePathFinder {
		protected BlockRangeScanner rangeScanner;

		@Override
		protected boolean runSearch(Pos playerPosition) {
			if (rangeScanner == null) {
				rangeScanner = new BlockRangeScanner(playerPosition);
				rangeScanner.startAsync(helper);
				return false;
			} else if (!rangeScanner.isScaningFinished()) {
				return false;
			} else {
				return super.runSearch(playerPosition);
			}
		}

		@Override
		protected boolean isForbiddenBlock(Block block) {
			return !AIHelper.blockIsOneOf(block, Blocks.torch, Blocks.air);
		}
	}
	
	private static class StorePathFinder extends BlockRangeFinder {

		@Override
		protected float rateDestination(int distance, int x, int y, int z) {
			ArrayList<ChestData> chests = rangeScanner.getChestsForPos(new Pos(
					x, y, z));
			if (chests != null) {
				for (ChestData c : chests) {
					for (ItemStack s : helper.getMinecraft().thePlayer.inventory.mainInventory) {
						if (c.isItemAllowed(s)) {
							return distance;
						}
					}
				}
			}
			return -1;
		}

		@Override
		protected void addTasksForTarget(Pos currentPos) {
			ArrayList<ChestData> chests = rangeScanner
					.getChestsForPos(currentPos);
			for (final ChestData c : chests) {
				boolean chestOpen = false;
				ItemStack[] inventory = helper.getMinecraft().thePlayer.inventory.mainInventory;
				for (int i = 0; i < inventory.length; i++) {
					final ItemStack s = inventory[i];
					if (c.couldTakeItem(s)) {
						if (!chestOpen) {
							addTask(new OpenChestTask(c.getSecondaryPos(),
									c.getPos()));
							addTask(new WaitTask(5));
							chestOpen = true;
						}
						addTask(new PutInChestTask(i) {
							@Override
							protected void containerIsFull() {
								super.containerIsFull();
								c.markAsFullFor(s, true);
							}
						});
						addTask(new WaitTask(5));
					}
				}
				if (chestOpen) {
					addTask(new CloseScreenTask());
					addTask(new WaitTask(5));
					chestOpen = false;
				}
			}
		}
	}

	public StoreStrategy() {
		super(new StorePathFinder(), null);
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
		return "Put inventory in chest.";
	}

}
