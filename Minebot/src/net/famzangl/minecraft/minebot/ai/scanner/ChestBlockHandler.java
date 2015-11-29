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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import net.famzangl.minecraft.minebot.ai.ItemFilter;
import net.famzangl.minecraft.minebot.ai.path.world.BlockSet;
import net.famzangl.minecraft.minebot.ai.path.world.WorldData;
import net.famzangl.minecraft.minebot.ai.scanner.ChestBlockHandler.ChestData;
import net.famzangl.minecraft.minebot.ai.utils.PrivateFieldUtils;
import net.minecraft.entity.EntityHanging;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

public class ChestBlockHandler extends RangeBlockHandler<ChestData> {

	private static final BlockSet CHEST = new BlockSet(Blocks.chest,
			Blocks.trapped_chest);

	public static class ChestData {
		private final BlockPos pos;
		private final ArrayList<ItemFilter> allowedItems = new ArrayList<ItemFilter>();
		private final ArrayList<ItemFilter> fullItems = new ArrayList<ItemFilter>();
		private final ArrayList<ItemFilter> emptyItems = new ArrayList<ItemFilter>();
		public BlockPos secondaryPos;
		private final int chestBlockId;

		public ChestData(BlockPos pos, int chestBlockId) {
			super();
			this.pos = pos;
			this.chestBlockId = chestBlockId;
		}

		public boolean isItemAllowed(ItemStack stack) {
			for (ItemFilter f : allowedItems) {
				if (f.matches(stack)) {
					return true;
				}
			}
			return false;
		}

		public boolean couldPutItem(ItemStack stack) {
			return !isFullFor(stack) && isItemAllowed(stack);
		}

		public boolean couldTakeItem(ItemStack stack) {
			for (ItemFilter f : emptyItems) {
				if (f.matches(stack)) {
					return false;
				}
			}
			return isItemAllowed(stack);
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((pos == null) ? 0 : pos.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			ChestData other = (ChestData) obj;
			if (pos == null) {
				if (other.pos != null)
					return false;
			} else if (!pos.equals(other.pos))
				return false;
			return true;
		}

		@Override
		public String toString() {
			return "ChestData [pos=" + pos + ", allowedItems=" + allowedItems
					+ "]";
		}

		public void allowItem(final ItemStack displayed) {
			allowedItems.add(new SameItemFilter(displayed));
		}

		public BlockPos getPos() {
			return pos;
		}

		public BlockPos getSecondaryPos() {
			return secondaryPos;
		}

		public void markAsFullFor(ItemStack s, boolean full) {
			removeFrom(fullItems, s);
			if (full) {
				fullItems.add(new SameItemFilter(s));
			}
		}

		public void markAsEmptyFor(ItemStack s, boolean empty) {
			removeFrom(emptyItems, s);
			if (empty) {
				emptyItems.add(new SameItemFilter(s));
			}
		}

		private void removeFrom(ArrayList<ItemFilter> list, ItemStack s) {
			Iterator<ItemFilter> iterator = list.iterator();
			while (iterator.hasNext()) {
				ItemFilter filter = iterator.next();
				if (filter.matches(s)) {
					iterator.remove();
				}
			}
		}

		public boolean isFullFor(ItemStack stack) {
			for (ItemFilter f : fullItems) {
				if (f.matches(stack)) {
					return true;
				}
			}
			return false;
		}

		public boolean isOfType(int id) {
			return id == chestBlockId;
		}

		public void registerByItemFrame(EntityItemFrame f) {
			ItemStack displayed = f.getDisplayedItem();
			if (displayed != null) {
				allowItem(displayed);
			}
		}
	}

	/**
	 * A table of chests.
	 */
	private final Hashtable<BlockPos, ChestData> chests = new Hashtable<BlockPos, ChestData>();

	@Override
	public BlockSet getIds() {
		return CHEST;
	}

	@Override
	protected void addPositionToCache(WorldData world, BlockPos pos, ChestData c) {
		super.addPositionToCache(world, pos, c);
		if (c.secondaryPos != null) {
			super.addPositionToCache(world, c.secondaryPos, c);
		}
	}

	@Override
	protected Collection<Entry<BlockPos, ChestData>> getTargetPositions() {
		return chests.entrySet();
	}

	@Override
	public void scanBlock(WorldData world, int id, int x, int y, int z) {
		if (CHEST.isAt(world, x, y, z)) {
			BlockPos myPos = new BlockPos(x, y, z);
			ChestData chest = getChestAt(myPos, id);
			scanForItemFrames(world, x, y, z, chest);
		}
	}

	private void scanForItemFrames(WorldData world, int x, int y, int z,
			ChestData chest) {
		BlockPos myPos = new BlockPos(x, y, z);
		AxisAlignedBB abb = new AxisAlignedBB(x - 1, y, z - 1, x + 2,
				y + 1, z + 2);
		List<EntityItemFrame> frames = world.getBackingWorld()
				.getEntitiesWithinAABB(EntityItemFrame.class, abb);
		for (EntityItemFrame f : frames) {
			EnumFacing direction = getDirection(f);
			if (direction == null) {
				continue;
			}
			BlockPos p = PrivateFieldUtils.getFieldValue(f,
					EntityHanging.class, BlockPos.class);
			EnumFacing dir = PrivateFieldUtils.getFieldValue(f,
					EntityHanging.class, EnumFacing.class);
			if (p.offset(dir, -1).equals(myPos)) {
				// Frame attached to this chest
				chest.registerByItemFrame(f);
			}
		}
	}

	private ChestData getChestAt(BlockPos pos, int id) {
		ChestData chest = null;
		for (EnumFacing d : new EnumFacing[] { EnumFacing.UP, EnumFacing.NORTH,
				EnumFacing.SOUTH, EnumFacing.EAST, EnumFacing.WEST }) {
			BlockPos p = pos.add(d.getFrontOffsetX(), 0, d.getFrontOffsetZ());
			ChestData attempted = chests.get(p);
			if (attempted != null && attempted.isOfType(id)) {
				chest = attempted;
				if (!chest.pos.equals(pos)) {
					chest.secondaryPos = pos;
				}
			}
		}
		if (chest == null) {
			chest = new ChestData(pos, id);
			chests.put(pos, chest);
		}
		return chest;
	}

	/**
	 * Gets the direction of whatever this frame is attached to
	 * 
	 * @param f
	 * @return
	 */
	private EnumFacing getDirection(EntityItemFrame f) {
		return f.field_174860_b;
	}
}
