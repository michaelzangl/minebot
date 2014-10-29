package net.famzangl.minecraft.minebot.ai.strategy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Map.Entry;

import net.famzangl.minecraft.minebot.Pos;
import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.enchanting.CloseScreenTask;
import net.famzangl.minecraft.minebot.ai.enchanting.PutItemInTableTask;
import net.famzangl.minecraft.minebot.ai.enchanting.SelectEnchantmentTask;
import net.famzangl.minecraft.minebot.ai.enchanting.TakeEnchantedItemTask;
import net.famzangl.minecraft.minebot.ai.scanner.BlockRangeFinder;
import net.famzangl.minecraft.minebot.ai.scanner.BlockRangeScanner;
import net.famzangl.minecraft.minebot.ai.scanner.RangeBlockHandler;
import net.famzangl.minecraft.minebot.ai.task.UseItemOnBlockAtTask;
import net.famzangl.minecraft.minebot.ai.task.inventory.ItemWithSubtype;
import net.minecraft.block.Block;
import net.minecraft.client.gui.GuiEnchantment;
import net.minecraft.init.Blocks;

public class EnchantStrategy extends PathFinderStrategy {

	public static class EnchantingTableData {

		public final Pos pos;

		public EnchantingTableData(Pos pos) {
			this.pos = pos;
			// TODO: Get level?
		}
	}

	public final static class EnchantingTableHandler extends
			RangeBlockHandler<EnchantingTableData> {
		private static final int[] IDS = new int[] { Block
				.getIdFromBlock(Blocks.enchanting_table) };

		private final Hashtable<Pos, EnchantingTableData> found = new Hashtable<Pos, EnchantingTableData>();

		@Override
		public int[] getIds() {
			return IDS;
		}

		@Override
		public void scanBlock(AIHelper helper, int id, int x, int y, int z) {
			Pos pos = new Pos(x, y, z);
			found.put(pos, new EnchantingTableData(pos));
		}

		@Override
		protected Collection<Entry<Pos, EnchantingTableData>> getTargetPositions() {
			return found.entrySet();
		}
	}

	public static class EnchantmentTableFinder extends BlockRangeFinder {
		EnchantingTableHandler h = new EnchantingTableHandler();
		private SelectEnchantmentTask enchantTask;

		@Override
		protected BlockRangeScanner constructScanner(Pos playerPosition) {
			BlockRangeScanner scanner = super.constructScanner(playerPosition);
			scanner.addHandler(h);
			return scanner;
		}

		@Override
		protected boolean runSearch(Pos playerPosition) {
			if (helper.getMinecraft().thePlayer.experienceLevel <= 0
					|| (enchantTask != null && enchantTask.hasFailed())) {
				return true;
			}
			return super.runSearch(playerPosition);
		}

		@Override
		protected float rateDestination(int distance, int x, int y, int z) {
			ArrayList<EnchantingTableData> tables = h
					.getReachableForPos(new Pos(x, y, z));
			return tables != null && tables.size() > 0 ? distance : -1;
		}

		@Override
		protected void addTasksForTarget(Pos currentPos) {
			ArrayList<EnchantingTableData> tables = h
					.getReachableForPos(currentPos);
			EnchantingTableData table = tables.get(0);

			addTask(new UseItemOnBlockAtTask(table.pos.x, table.pos.y,
					table.pos.z) {
				@Override
				public boolean isFinished(AIHelper h) {
					return super.isFinished(h)
							&& h.getMinecraft().currentScreen instanceof GuiEnchantment;
				};
			});
			addTask(new PutItemInTableTask());
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
