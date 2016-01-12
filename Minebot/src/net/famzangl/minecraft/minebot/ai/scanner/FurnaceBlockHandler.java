package net.famzangl.minecraft.minebot.ai.scanner;

import java.util.Collection;
import java.util.Hashtable;
import java.util.Map.Entry;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.path.world.BlockSet;
import net.famzangl.minecraft.minebot.ai.path.world.BlockSets;
import net.famzangl.minecraft.minebot.ai.path.world.WorldData;
import net.famzangl.minecraft.minebot.ai.scanner.FurnaceBlockHandler.FurnaceData;
import net.famzangl.minecraft.minebot.ai.task.inventory.ItemWithSubtype;
import net.famzangl.minecraft.minebot.ai.utils.PrivateFieldUtils;
import net.minecraft.client.gui.inventory.GuiFurnace;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.SlotFurnaceFuel;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.BlockPos;

public class FurnaceBlockHandler extends RangeBlockHandler<FurnaceData> {

	public static class FurnaceData {

		private ItemWithSubtype setFuelItem;
		private ItemWithSubtype setBurnItem;
		private ItemWithSubtype setResultItem;
		private boolean isFullBurn;
		private boolean isFullFuel;
		private boolean resultStackKnown;
		private BlockPos pos;

		public FurnaceData(BlockPos pos) {
			this.pos = pos;
		}

		public boolean couldPutFuel(ItemWithSubtype item) {
			return isFuel(item)
					&& (setFuelItem == null || setFuelItem.equals(item)
							&& !isFullFuel);
		}

		private static boolean isFuel(ItemWithSubtype item) {
			ItemStack stack = item.getFakeMCStack(1);
			return TileEntityFurnace.isItemFuel(stack)
					|| SlotFurnaceFuel.isBucket(stack);
		}

		public boolean couldPut(ItemWithSubtype item) {
			ItemStack result = FurnaceRecipes.instance().getSmeltingResult(
					item.getFakeMCStack(1));
			if (result == null)
				return false;
			if (setResultItem != null && !setResultItem.equals(item))
				return false;
			return setBurnItem == null || setBurnItem.equals(item)
					&& !isFullBurn;
		}

		public boolean couldTake() {
			return setResultItem != null || !resultStackKnown;
		}

		public void update(GuiFurnace screen) {
			if (screen == null) {
				return;
			}
			IInventory inv = PrivateFieldUtils.getFieldValue(screen,
					GuiFurnace.class, IInventory.class);
			ItemStack burn = inv.getStackInSlot(0);
			ItemStack fuel = inv.getStackInSlot(1);
			ItemStack result = inv.getStackInSlot(2);

			setBurnItem = burn == null ? null : new ItemWithSubtype(burn);
			setFuelItem = fuel == null ? null : new ItemWithSubtype(fuel);
			isFullBurn = burn != null
					&& burn.getMaxStackSize() <= burn.stackSize;
			isFullFuel = fuel != null
					&& fuel.getMaxStackSize() <= fuel.stackSize;
			setResultItem = result == null ? null : new ItemWithSubtype(result);
			resultStackKnown = true;
		}

		public BlockPos getPos() {
			return pos;
		}

		public void update(AIHelper h) {
			update((GuiFurnace) h.getMinecraft().currentScreen);
		}

	}

	private final Hashtable<BlockPos, FurnaceData> found = new Hashtable<BlockPos, FurnaceData>();

	@Override
	public BlockSet getIds() {
		return BlockSets.FURNACE;
	}

	@Override
	public void scanBlock(WorldData world, int id, int x, int y, int z) {
		BlockPos pos = new BlockPos(x, y, z);
		found.put(pos, new FurnaceData(pos));
	}

	@Override
	protected Collection<Entry<BlockPos, FurnaceData>> getTargetPositions() {
		return found.entrySet();
	}
}
