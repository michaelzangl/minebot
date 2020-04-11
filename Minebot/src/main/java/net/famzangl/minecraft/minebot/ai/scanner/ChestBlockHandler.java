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

import net.famzangl.minecraft.minebot.ai.ItemFilter;
import net.famzangl.minecraft.minebot.ai.path.world.BlockSet;
import net.famzangl.minecraft.minebot.ai.path.world.WorldData;
import net.famzangl.minecraft.minebot.ai.scanner.ChestBlockHandler.ChestData;
import net.famzangl.minecraft.minebot.ai.utils.PrivateFieldUtils;
import net.minecraft.block.Blocks;
import net.minecraft.entity.item.HangingEntity;
import net.minecraft.entity.item.ItemFrameEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

public class ChestBlockHandler extends RangeBlockHandler<ChestData> {
	private static final BlockSet CHEST = new BlockSet(Blocks.CHEST,
			Blocks.TRAPPED_CHEST);

	public static class AbstractChestData {
		protected final BlockPos pos;
		private BlockPos secondaryPos;
		protected final ArrayList<ItemFilter> allowedItems = new ArrayList<ItemFilter>();
		protected final ArrayList<ItemFilter> fullItems = new ArrayList<ItemFilter>();
		protected final ArrayList<ItemFilter> emptyItems = new ArrayList<ItemFilter>();

		public AbstractChestData(BlockPos pos) {
			this.pos = pos;
		}

		public BlockPos getPos() {
			return pos;
		}

		public BlockPos getSecondaryPos() {
			return secondaryPos;
		}

		public void setSecondaryPos(BlockPos secondaryPos) {
			if (this.secondaryPos != null
					&& !secondaryPos.equals(this.secondaryPos)) {
				throw new IllegalStateException("Cannot update secondary pos.");
			}
			this.secondaryPos = secondaryPos;
		}

		public boolean isItemAllowed(ItemStack stack) {
			for (ItemFilter filter : allowedItems) {
				if (filter.matches(stack)) {
					return true;
				}
			}
			return false;
		}

		public boolean couldPutItem(ItemStack stack) {
			return !isFullFor(stack) && isItemAllowed(stack);
		}

		public boolean couldTakeItem(ItemStack stack) {
			return !isEmptyFor(stack) && isItemAllowed(stack);
		}

		private boolean isEmptyFor(ItemStack stack) {
			for (ItemFilter filter : emptyItems) {
				if (filter.matches(stack)) {
					return true;
				}
			}
			return false;
		}

		public boolean isFullFor(ItemStack stack) {
			for (ItemFilter filter : fullItems) {
				if (filter.matches(stack)) {
					return true;
				}
			}
			return false;
		}
	}

	public static class ChestData extends AbstractChestData {
		private final int chestBlockId;

		public ChestData(BlockPos pos, int chestBlockId) {
			super(pos);
			this.chestBlockId = chestBlockId;
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
			persistentStatus.update(this);
		}

		public void markAsFullFor(ItemStack stack, boolean full) {
			removeFrom(fullItems, stack);
			if (full) {
				fullItems.add(new SameItemFilter(stack));
			}
			persistentStatus.update(this);
		}

		public void markAsEmptyFor(ItemStack stack, boolean empty) {
			removeFrom(emptyItems, stack);
			if (empty) {
				emptyItems.add(new SameItemFilter(stack));
			}
			persistentStatus.update(this);
		}

		@Override
		public void setSecondaryPos(BlockPos secondaryPos) {
			super.setSecondaryPos(secondaryPos);
			persistentStatus.update(this);
		}

		private void removeFrom(ArrayList<ItemFilter> list, ItemStack stack) {
			Iterator<ItemFilter> iterator = list.iterator();
			while (iterator.hasNext()) {
				ItemFilter filter = iterator.next();
				if (filter.matches(stack)) {
					iterator.remove();
				}
			}
		}

		public boolean isOfType(int id) {
			return id == chestBlockId;
		}

		public void registerByItemFrame(ItemFrameEntity frame) {
			ItemStack displayed = frame.getDisplayedItem();
			if (displayed != null && !displayed.isEmpty()) {
				allowItem(displayed);
			}
		}
	}

	public static class PersistentChestStatus {
		private final HashMap<BlockPos, AbstractChestData> chests = new HashMap<BlockPos, AbstractChestData>();

		public void update(AbstractChestData data) {
			chests.put(data.getPos(), data);
			BlockPos secondaryPos = data.getSecondaryPos();
			if (secondaryPos != null) {
				chests.put(secondaryPos, data);
			}
		}

		public void reset() {
			chests.clear();
		}

		public boolean isEmptyFor(BlockPos pos, ItemStack stack) {
			AbstractChestData data = chests.get(pos);
			return data != null && data.isEmptyFor(stack);
		}

		public boolean isFullFor(BlockPos pos, ItemStack stack) {
			AbstractChestData data = chests.get(pos);
			return data != null && data.isFullFor(stack);
		}
	}

	private static final PersistentChestStatus persistentStatus = new PersistentChestStatus();

	/**
	 * A table of chests.
	 */
	private final HashMap<BlockPos, ChestData> chests = new HashMap<BlockPos, ChestData>();

	@Override
	public BlockSet getIds() {
		return CHEST;
	}

	@Override
	protected void addPositionToCache(WorldData world, BlockPos pos, ChestData chestData) {
		super.addPositionToCache(world, pos, chestData);
		if (chestData.getSecondaryPos() != null) {
			super.addPositionToCache(world, chestData.getSecondaryPos(), chestData);
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
		AxisAlignedBB abb = new AxisAlignedBB(x - 1, y, z - 1, x + 2, y + 1,
				z + 2);
		List<ItemFrameEntity> frames = world.getBackingWorld()
				.getEntitiesWithinAABB(ItemFrameEntity.class, abb);
		for (ItemFrameEntity frame : frames) {
			Direction direction = getDirection(frame);
			if (direction == null) {
				continue;
			}
			BlockPos pos = PrivateFieldUtils.getFieldValue(frame,
					HangingEntity.class, BlockPos.class);
			Direction dir = PrivateFieldUtils.getFieldValue(frame,
					HangingEntity.class, Direction.class);
			if (pos.offset(dir, -1).equals(myPos)) {
				// Frame attached to this chest
				chest.registerByItemFrame(frame);
			}
		}
	}

	private ChestData getChestAt(BlockPos pos, int id) {
		ChestData chest = null;
		for (Direction d : new Direction[] { Direction.UP, Direction.NORTH,
				Direction.SOUTH, Direction.EAST, Direction.WEST }) {
			BlockPos blockPos = pos.add(d.getXOffset(), 0, d.getZOffset());
			ChestData attempted = chests.get(blockPos);
			if (attempted != null && attempted.isOfType(id)) {
				chest = attempted;
				if (!chest.pos.equals(pos)) {
					chest.setSecondaryPos(pos);
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
	 * @param itemFrame
	 * @return
	 */
	private Direction getDirection(ItemFrameEntity itemFrame) {
		return itemFrame.facingDirection;
	}

	public int getExpectedPutRating(BlockPos pos, ItemStack stack) {
		if (persistentStatus.isFullFor(pos, stack)) {
			return 10;
		} else {
			return 0;
		}
	}

	public int getExpectedTakeRating(BlockPos pos, ItemStack stack) {
		if (persistentStatus.isEmptyFor(pos, stack)) {
			return 10;
		} else {
			return 0;
		}
	}
}
