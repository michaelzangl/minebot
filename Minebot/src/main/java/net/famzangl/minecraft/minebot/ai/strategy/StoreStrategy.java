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
import net.famzangl.minecraft.minebot.ai.scanner.BlockRangeFinder;
import net.famzangl.minecraft.minebot.ai.scanner.BlockRangeScanner;
import net.famzangl.minecraft.minebot.ai.scanner.ChestBlockHandler;
import net.famzangl.minecraft.minebot.ai.scanner.ChestBlockHandler.ChestData;
import net.famzangl.minecraft.minebot.ai.task.OpenChestTask;
import net.famzangl.minecraft.minebot.ai.task.WaitTask;
import net.famzangl.minecraft.minebot.ai.task.inventory.PutInChestTask;
import net.minecraft.client.gui.screen.inventory.ChestScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;

/**
 * Store whatever you are holding in a chest.
 * 
 * @author michael
 * 
 */
public class StoreStrategy extends PathFinderStrategy {
	private static class WaitIfNotFullTask extends WaitTask {

		private final ItemStack stack;
		private final ChestData c;

		public WaitIfNotFullTask(int time, ChestData c, ItemStack stack) {
			super(time);
			this.c = c;
			this.stack = stack;
		}
		
		@Override
		public boolean isFinished(AIHelper aiHelper) {
			return super.isFinished(aiHelper) || c.isFullFor(stack);
		}
	}
	
	private static class StorePathFinder extends BlockRangeFinder {

		private ChestBlockHandler chestBlockHandler;

		@Override
		protected BlockRangeScanner constructScanner(BlockPos playerPosition) {
			BlockRangeScanner scanner = super.constructScanner(playerPosition);
			chestBlockHandler = new ChestBlockHandler();
			scanner.addHandler(chestBlockHandler);
			return scanner;
		}
		
		@Override
		protected float rateDestination(int distance, int x, int y, int z) {
			ArrayList<ChestData> chests = chestBlockHandler.getReachableForPos(new BlockPos(
					x, y, z));
			if (chests != null) {
				for (ChestData c : chests) {
					for (ItemStack stack : helper.getMinecraft().player.inventory.mainInventory) {
						if (c.couldPutItem(stack)) {
							return distance + chestBlockHandler.getExpectedPutRating(c.getPos(), stack);
						}
					}
				}
			}
			return -1;
		}

		@Override
		protected void addTasksForTarget(BlockPos currentPos) {
			ArrayList<ChestData> chests = chestBlockHandler
					.getReachableForPos(currentPos);
			for (final ChestData c : chests) {
				boolean chestOpen = false;
				NonNullList<ItemStack> inventory = helper.getMinecraft().player.inventory.mainInventory;
				for (int i = 0; i < inventory.size(); i++) {
					final ItemStack stack = inventory.get(i);
					if (c.couldPutItem(stack)) {
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
								c.markAsFullFor(stack, true);
							}
						});
						addTask(new WaitIfNotFullTask(5, c, stack));
					}
				}
				if (chestOpen) {
					addTask(new CloseScreenTask());
					addTask(new WaitTask(5));
					chestOpen = false;
					break;
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
		if (helper.getMinecraft().currentScreen instanceof ChestScreen) {
			addTask(new CloseScreenTask());
		}
		super.searchTasks(helper);
	}

	@Override
	public String getDescription(AIHelper helper) {
		return "Put inventory in chest.";
	}

}
