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
package net.famzangl.minecraft.minebot.ai.path;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.ClassItemFilter;
import net.famzangl.minecraft.minebot.ai.ItemFilter;
import net.famzangl.minecraft.minebot.ai.path.world.BlockBounds;
import net.famzangl.minecraft.minebot.ai.path.world.BlockMetaSet;
import net.famzangl.minecraft.minebot.ai.path.world.BlockSet;
import net.famzangl.minecraft.minebot.ai.path.world.BlockSets;
import net.famzangl.minecraft.minebot.ai.path.world.WorldData;
import net.famzangl.minecraft.minebot.ai.path.world.WorldWithDelta;
import net.famzangl.minecraft.minebot.ai.task.CanWorkWhileApproaching;
import net.famzangl.minecraft.minebot.ai.task.TaskOperations;
import net.famzangl.minecraft.minebot.ai.task.UseItemOnBlockAtTask;
import net.famzangl.minecraft.minebot.ai.task.error.TaskError;
import net.famzangl.minecraft.minebot.ai.task.place.DestroyBlockTask;
import net.famzangl.minecraft.minebot.ai.task.place.PlaceBlockAtFloorTask;
import net.famzangl.minecraft.minebot.settings.MinebotSettingsRoot;
import net.famzangl.minecraft.minebot.settings.PathfindingSetting;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemSeeds;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class PlantPathFinder extends MovePathFinder {
	private static final BlockMetaSet GROWN_CROPS = new BlockMetaSet(Blocks.NETHER_WART, 3)
			.unionWith(new BlockMetaSet(Blocks.WHEAT, 7))
			.unionWith(new BlockMetaSet(Blocks.CARROTS, 7))
			.unionWith(new BlockMetaSet(Blocks.POTATOES, 7))
			.unionWith(new BlockMetaSet(Blocks.BEETROOTS, 3));
	private static final BlockSet FARMLAND = new BlockSet(Blocks.FARMLAND);
	private static final BlockSet NETHERWART_FARMLAND = new BlockSet(
			Blocks.SOUL_SAND);

	private static final BlockSet FARMLANDABLE = new BlockSet(Blocks.DIRT,
			Blocks.GRASS);

	private final class PlaceSeedsTask extends PlaceBlockAtFloorTask implements CanWorkWhileApproaching {
		private final SeedFilter seedFilter;

		private PlaceSeedsTask(BlockPos pos, SeedFilter filter) {
			super(pos, filter);
			seedFilter = filter;
		}

		@Override
		protected boolean isAtDesiredHeight(AIHelper aiHelper) {
			return true;
		}

		@Override
		public boolean applyToDelta(WorldWithDelta world) {
			Item anyPlaceItem = seedFilter.type.items[0];
			IBlockState block = ((ItemSeeds)anyPlaceItem).getPlant(null, null);
			world.setBlock(pos, block);
			return true;
		}

		@Override
		public boolean doApproachWork(AIHelper helper) {
			if (isFinished(helper)) {
				return false;
			}
			runTick(helper, new TaskOperations() {
				@Override
				public boolean faceAndDestroyForNextTask() {
					return false;
				}
				
				@Override
				public void desync(TaskError taskError) {
				}
			});
			return true;
		}
	}

	public enum PlantType {
		NORMAL(FARMLAND, Items.WHEAT_SEEDS, Items.CARROT, Items.POTATO, Items.BEETROOT_SEEDS),
		WHEAT(FARMLAND, Items.WHEAT_SEEDS),
		CARROT(FARMLAND, Items.CARROT),
		POTATO(FARMLAND, Items.POTATO),
		BEETROOT(FARMLAND, Items.BEETROOT_SEEDS),
		NETHERWART(NETHERWART_FARMLAND, Items.NETHER_WART);

		public final BlockSet farmland;

		private final Item[] items;

		private PlantType(BlockSet farmland, Item... items) {
			this.farmland = farmland;
			this.items = items;
		}

		public boolean canPlantItem(Item item) {
			for (final Item i : items) {
				if (item == i) {
					return true;
				}
			}
			return false;
		}
	}

	private final class SeedFilter implements ItemFilter {
		private final PlantType type;

		public SeedFilter(PlantType type) {
			super();
			this.type = type;
		}

		@Override
		public boolean matches(ItemStack itemStack) {
			return itemStack != null && type.canPlantItem(itemStack.getItem());
		}
	}

	private static class UseHoeTask extends UseItemOnBlockAtTask implements CanWorkWhileApproaching {

		private Vec3d facingPosition;

		public UseHoeTask(BlockPos farmlandPos) {
			super(new ClassItemFilter(ItemHoe.class), farmlandPos);
		}

		@Override
		public boolean applyToDelta(WorldWithDelta world) {
			if (FARMLANDABLE.isAt(world, getPos())) {
				world.setBlock(getPos(), Blocks.FARMLAND);
			}
			return true;
		}

		@Override
		public boolean doApproachWork(AIHelper helper) {
			if (isFinished(helper)) {
				return false;
			}
			runTick(helper, new TaskOperations() {
				@Override
				public boolean faceAndDestroyForNextTask() {
					return false;
				}
				
				@Override
				public void desync(TaskError taskError) {
				}
			});
			return true;
		}

		protected void reFace(AIHelper aiHelper) {
			if (facingPosition != null) {
				aiHelper.face(facingPosition);
			}
		}

		@Override
		protected void notFacingBlock(AIHelper aiHelper) {
			BlockBounds bounds = aiHelper.getWorld().getBlockBounds(getPos());
			facingPosition = bounds.onlySide(EnumFacing.UP).random(getPos(), 0.7);
			reFace(aiHelper);
		}
	}

	private final PlantType type;

	public PlantPathFinder(PlantType type) {
		this.type = type;
	}

	@Override
	protected PathfindingSetting loadSettings(MinebotSettingsRoot settingsRoot) {
		return settingsRoot.getPathfinding().getPlanting();
	}

	@Override
	protected float rateDestination(int distance, int x, int y, int z) {
		if (isGrown(world, x, y, z)) {
			return distance + 1;
		} else if (BlockSets.AIR.isAt(world, x, y, z)
				&& type.farmland.isAt(world, x, y - 1, z)
				&& helper.canSelectItem(new SeedFilter(type))) {
			return distance + 1;
		} else if (type.farmland == FARMLAND
				&& BlockSets.AIR.isAt(world, x, y, z)
				&& FARMLANDABLE.contains(helper.getBlock(x, y - 1, z))
				&& helper.canSelectItem(new SeedFilter(type))
				&& helper.canSelectItem(new ClassItemFilter(ItemHoe.class))) {
			return distance + 10;
		} else {
			return -1;
		}
	}

	private boolean isGrown(WorldData world, int x, int y, int z) {
		return GROWN_CROPS.isAt(world, x, y, z);
	}

	@Override
	protected void addTasksForTarget(BlockPos currentPos) {
		if (BlockSets.AIR.isAt(world, currentPos)) {
			BlockPos farmlandPos = currentPos.add(0, -1, 0);
			if (!type.farmland.isAt(world, farmlandPos)) {
				addTask(new UseHoeTask(farmlandPos));
			}
			addTask(new PlaceSeedsTask(currentPos, new SeedFilter(type)));
		} else {
			addTask(new DestroyBlockTask(currentPos));
		}
	}
}
