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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Map.Entry;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.enchanting.CloseScreenTask;
import net.famzangl.minecraft.minebot.ai.enchanting.PutItemInTableTask;
import net.famzangl.minecraft.minebot.ai.enchanting.PutLapisInTableTask;
import net.famzangl.minecraft.minebot.ai.enchanting.SelectEnchantmentTask;
import net.famzangl.minecraft.minebot.ai.enchanting.TakeEnchantedItemTask;
import net.famzangl.minecraft.minebot.ai.path.world.BlockSet;
import net.famzangl.minecraft.minebot.ai.path.world.Pos;
import net.famzangl.minecraft.minebot.ai.path.world.WorldData;
import net.famzangl.minecraft.minebot.ai.scanner.BlockRangeFinder;
import net.famzangl.minecraft.minebot.ai.scanner.BlockRangeScanner;
import net.famzangl.minecraft.minebot.ai.scanner.RangeBlockHandler;
import net.famzangl.minecraft.minebot.ai.task.UseItemOnBlockAtTask;
import net.famzangl.minecraft.minebot.ai.task.inventory.ItemWithSubtype;
import net.minecraft.client.gui.GuiEnchantment;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;

public class EnchantStrategy extends PathFinderStrategy {

	public static class EnchantingTableData {

		public final BlockPos pos;

		public EnchantingTableData(BlockPos pos) {
			this.pos = pos;
			// TODO: Get level?
		}
	}

	public final static class EnchantingTableHandler extends
			RangeBlockHandler<EnchantingTableData> {
		private static final BlockSet IDS = new BlockSet(Blocks.enchanting_table);

		private final Hashtable<BlockPos, EnchantingTableData> found = new Hashtable<BlockPos, EnchantingTableData>();

		@Override
		public BlockSet getIds() {
			return IDS;
		}

		@Override
		public void scanBlock(WorldData world, int id, int x, int y, int z) {
			BlockPos pos = new BlockPos(x, y, z);
			found.put(pos, new EnchantingTableData(pos));
		}

		@Override
		protected Collection<Entry<BlockPos, EnchantingTableData>> getTargetPositions() {
			return found.entrySet();
		}
	}

	public static class EnchantmentTableFinder extends BlockRangeFinder {
		EnchantingTableHandler h = new EnchantingTableHandler();
		private SelectEnchantmentTask enchantTask;

		@Override
		protected BlockRangeScanner constructScanner(BlockPos playerPosition) {
			BlockRangeScanner scanner = super.constructScanner(playerPosition);
			scanner.addHandler(h);
			return scanner;
		}

		@Override
		protected boolean runSearch(BlockPos playerPosition) {
			if (helper.getMinecraft().thePlayer.experienceLevel <= 0
					|| (enchantTask != null && enchantTask.hasFailed())) {
				return true;
			}
			return super.runSearch(playerPosition);
		}

		@Override
		protected float rateDestination(int distance, int x, int y, int z) {
			ArrayList<EnchantingTableData> tables = h
					.getReachableForPos(new BlockPos(x, y, z));
			return tables != null && tables.size() > 0 ? distance : -1;
		}

		@Override
		protected void addTasksForTarget(BlockPos currentPos) {
			ArrayList<EnchantingTableData> tables = h
					.getReachableForPos(currentPos);
			EnchantingTableData table = tables.get(0);

			addTask(new UseItemOnBlockAtTask(table.pos) {
				@Override
				public boolean isFinished(AIHelper h) {
					return super.isFinished(h)
							&& h.getMinecraft().currentScreen instanceof GuiEnchantment;
				};
			});
			addTask(new PutItemInTableTask());
			addTask(new PutLapisInTableTask());
			enchantTask = new SelectEnchantmentTask();
			addTask(enchantTask);
			addTask(new TakeEnchantedItemTask());
			addTask(new CloseScreenTask());
		}
	}

	public EnchantStrategy(ItemWithSubtype item) {
		// TODO: respect item id.
		super(new EnchantmentTableFinder(), "Enchanting");
	}

}
