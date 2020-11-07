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
import net.famzangl.minecraft.minebot.ai.path.world.BlockSets;
import net.famzangl.minecraft.minebot.ai.path.world.WorldData;
import net.famzangl.minecraft.minebot.ai.scanner.ChestBlockHandler.ChestData;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.WallSignBlock;
import net.minecraft.entity.item.ItemFrameEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.state.properties.ChestType;
import net.minecraft.tileentity.SignTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ChestBlockHandler extends RangeBlockHandler<ChestData> {
	private static final Logger LOGGER = LogManager.getLogger(RangeBlockHandler.class);

	private static final BlockSet CHEST = BlockSet.builder().add(Blocks.CHEST,
			Blocks.TRAPPED_CHEST).build();

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

		public boolean couldPutItem(ItemStack stack, WorldData world) {
			return !isFullFor(stack) && canOpen(world) && isItemAllowed(stack);
		}

		public boolean couldTakeItem(ItemStack stack, WorldData world) {
			return !isEmptyFor(stack) && canOpen(world) && isItemAllowed(stack);
		}

		private boolean canOpen(WorldData world) {
			// Won't use world state, but we assume that bot will not add / remove blocks above chest
			return !ChestBlock.isBlocked(world.getBackingWorld(), pos)
					&& secondaryPos == null || !ChestBlock.isBlocked(world.getBackingWorld(), secondaryPos);
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
		public ChestData(BlockPos pos) {
			super(pos);
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
			list.removeIf(filter -> filter.matches(stack));
		}

		public void registerByItemFrame(ItemFrameEntity frame) {
			ItemStack displayed = frame.getDisplayedItem();
			if (!displayed.isEmpty()) {
				allowedItems.add(new SameItemFilter(displayed));
				LOGGER.debug("Allow item by item frame for {}: {}", pos, displayed.getItem());
				persistentStatus.update(this);
			}
		}

		public void registerBySign(TileEntity tileEntity) {
			if (!(tileEntity instanceof SignTileEntity)) {
				throw new IllegalArgumentException("Expected a sign tile entity to be passed for chest at " + pos + " but got " + tileEntity);
			}
			ITextComponent l1 = ((SignTileEntity) tileEntity).getText(1);
			ITextComponent l2 = ((SignTileEntity) tileEntity).getText(2);
			ITextComponent l3 = ((SignTileEntity) tileEntity).getText(3);
			ITextComponent l0 = ((SignTileEntity) tileEntity).getText(0);
			ITextComponent[] texts = {l0, l1,l2,l3};

			//SignTileEntity sign = (SignTileEntity) Minecraft.getInstance().world.getTileEntity(pos);
			//			AIChatController.addChatLine(sign.getText(1).getString());
			// 1 gave B from A B C D.
			//Tested using ^ to see it starts from X. (In setpos code)
			//Starts from 0.
			Set<String> lines = Stream.of(texts)
					.map(ITextComponent::getString)
					.map(str -> str.trim().toLowerCase(Locale.US))
					.collect(Collectors.toSet());
			Set<String> words = Stream.of(String.join(" ", lines)
					.replaceAll("\\W+", " ")
					.trim()
					.split("\\s+"))
					.collect(Collectors.toSet());

			LOGGER.debug("Registered text from sign for chest at {}. Words: {}, Lines: {}", pos, words, lines);

			// Text needs to contain item name
			allowedItems.add(itemToTest ->
					itemToTest.getItem() != Items.AIR && (
							// Display name as full line => supports i18n
							lines.contains(itemToTest.getDisplayName().getString().toLowerCase(Locale.US))
									// minecraft:xxx
									||
									itemToTest.getItem().getRegistryName() != null && (
											words.contains(itemToTest.getItem().getRegistryName().toString().toLowerCase(Locale.US))
													// only xxx
													|| words.contains(itemToTest.getItem().getRegistryName().getPath().toLowerCase(Locale.US)))
					));
			persistentStatus.update(this);
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
			ChestData chest = getChestAt(world, myPos);
			scanForItemFrames(world, myPos, chest);
		}
	}

	private void scanForItemFrames(WorldData world, BlockPos pos,
			ChestData chest) {
		AxisAlignedBB abb = new AxisAlignedBB(pos.add(-1, 0, -1), pos.add(2, 1, 2));
		List<ItemFrameEntity> frames = world.getBackingWorld()
				.getEntitiesWithinAABB(ItemFrameEntity.class, abb);
		for (ItemFrameEntity frame : frames) {
			BlockPos attachedTo = frame.getHangingPosition().offset(frame.getHorizontalFacing().getOpposite());
			if (attachedTo.equals(pos)) {
				// Frame attached to this chest
				chest.registerByItemFrame(frame);
			}
		}

		for (Direction dir : new Direction[] { Direction.EAST, Direction.SOUTH, Direction.NORTH, Direction.WEST }) {
			BlockPos signPos = pos.offset(dir);
			if (BlockSets.WALL_SIGN.isAt(world, signPos)) {
				// Wall sign
				BlockState state = world.getBlockState(signPos);
				BlockPos attachedTo = signPos.offset(state.get(WallSignBlock.FACING).getOpposite());
				if (attachedTo.equals(pos)) {
					chest.registerBySign(world.getBackingWorld().getTileEntity(signPos));
				}
			}
		}
	}

	private ChestData getChestAt(WorldData world, BlockPos pos) {
		ChestData chest = null;

		// Merged chests => handle them as one
		BlockState chestState = world.getBlockState(pos);
		if (chestState.get(ChestBlock.TYPE) != ChestType.SINGLE) {
			Direction directionAttached = ChestBlock.getDirectionToAttached(chestState);
			BlockPos otherChestPos = pos.offset(directionAttached);
			ChestData attempted = chests.get(otherChestPos);
			if (attempted != null) {
				chest = attempted;
				if (!chest.pos.equals(pos)) {
					LOGGER.debug("Merging chest at {} with {}", chest.pos, pos);
					chest.setSecondaryPos(pos);
				}
			}
		}

		if (chest == null) {
			chest = new ChestData(pos);
			chests.put(pos, chest);
			LOGGER.debug("Found new chest at {}", pos);
		}
		return chest;
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
