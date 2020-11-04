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
import net.minecraft.block.BeetrootBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CarrotBlock;
import net.minecraft.block.CropsBlock;
import net.minecraft.block.NetherWartBlock;
import net.minecraft.block.PotatoBlock;
import net.minecraft.item.HoeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.common.IPlantable;

public class PlantPathFinder extends MovePathFinder {

	private static final BlockSet GROWN_CROPS =
			BlockSet.builder().add(
					Blocks.NETHER_WART.getDefaultState().with(NetherWartBlock.AGE, 3),
					Blocks.WHEAT.getDefaultState().with(CropsBlock.AGE, 7),
					Blocks.CARROTS.getDefaultState().with(CarrotBlock.AGE, 7),
					Blocks.POTATOES.getDefaultState().with(PotatoBlock.AGE, 7),
					Blocks.BEETROOTS.getDefaultState().with(BeetrootBlock.BEETROOT_AGE, 3)
					).build();
	private static final BlockSet FARMLAND = BlockSet.builder().add(Blocks.FARMLAND).build();
	private static final BlockSet NETHERWART_FARMLAND = BlockSet.builder().add(
			Blocks.SOUL_SAND).build();

	private static final BlockSet FARMLANDABLE = BlockSet.builder().add(Blocks.DIRT,
			Blocks.GRASS_BLOCK).build();

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
			BlockState block = ((IPlantable) anyPlaceItem)
					.getPlant(null, null);
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

		private Vector3d facingPosition;

		public UseHoeTask(BlockPos farmlandPos) {
			super(new ClassItemFilter(HoeItem.class), farmlandPos);
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
			BlockBounds bounds = aiHelper.getWorld().getCollisionBounds(getPos());
			facingPosition = bounds.onlySide(Direction.UP).random(getPos(), 0.7);
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
				&& FARMLANDABLE.isAt(world, x, y - 1, z)
				&& helper.canSelectItem(new SeedFilter(type))
				&& helper.canSelectItem(new ClassItemFilter(HoeItem.class))) {
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
