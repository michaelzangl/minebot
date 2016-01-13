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
package net.famzangl.minecraft.minebot.ai.scanner;

import java.util.Collection;
import java.util.HashMap;
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
			if (!isFuel(item)) {
				return false;
			} else if (setFuelItem != null && !setFuelItem.equals(item)) {
				return false;
			} else {
				return setFuelItem == null || !isFullFuel;
			}
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
			return setBurnItem == null || !isFullBurn;
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

	private final HashMap<BlockPos, FurnaceData> found = new HashMap<BlockPos, FurnaceData>();

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
