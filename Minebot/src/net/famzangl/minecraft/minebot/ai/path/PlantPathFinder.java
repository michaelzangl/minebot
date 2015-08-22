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

import net.famzangl.minecraft.minebot.ai.ClassItemFilter;
import net.famzangl.minecraft.minebot.ai.ItemFilter;
import net.famzangl.minecraft.minebot.ai.path.world.BlockSet;
import net.famzangl.minecraft.minebot.ai.path.world.BlockSets;
import net.famzangl.minecraft.minebot.ai.path.world.WorldData;
import net.famzangl.minecraft.minebot.ai.task.UseItemOnBlockAtTask;
import net.famzangl.minecraft.minebot.ai.task.place.DestroyBlockTask;
import net.famzangl.minecraft.minebot.ai.task.place.PlaceBlockAtFloorTask;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCrops;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;

public class PlantPathFinder extends MovePathFinder {
	public enum PlantType {
		ANY(Blocks.farmland, Items.wheat_seeds, Items.carrot, Items.potato), WHEAT(
				Blocks.farmland, Items.wheat_seeds), CARROT(Blocks.farmland,
				Items.carrot), POTATO(Blocks.farmland, Items.potato), NETHERWART(
				Blocks.soul_sand, Items.nether_wart);

		public final Block farmland;

		private final Item[] items;

		private PlantType(Block farmland, Item... items) {
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

	private final PlantType type;

	private static final BlockSet farmlandable = new BlockSet(
			Blocks.dirt, Blocks.grass);
	private static final BlockSet farmland = new BlockSet(
			Blocks.farmland);

	public PlantPathFinder(PlantType type) {
		this.type = type;
		allowedGroundForUpwardsBlocks = allowedGroundBlocks;
		footAllowedBlocks = BlockSets.FEET_CAN_WALK_THROUGH;
		headAllowedBlocks = BlockSets.HEAD_CAN_WALK_TRHOUGH;
		footAllowedBlocks = footAllowedBlocks.intersectWith(forbiddenBlocks
				.invert());
		headAllowedBlocks = headAllowedBlocks.intersectWith(forbiddenBlocks
				.invert());
	}

	@Override
	protected float rateDestination(int distance, int x, int y, int z) {
		if (isGrown(world, x, y, z)) {
			return distance + 1;
		} else if (BlockSets.AIR.isAt(world, x, y, z) && farmland.isAt(world, x, y - 1, z)
				&& helper.canSelectItem(new SeedFilter(type))) {
			return distance + 1;
		} else if (type.farmland == Blocks.farmland
				&& BlockSets.AIR.isAt(world, x, y, z)
				&& farmlandable.contains(helper.getBlock(x, y - 1, z))
				&& helper.canSelectItem(new SeedFilter(type))
				&& helper.canSelectItem(new ClassItemFilter(ItemHoe.class))) {
			return distance + 10;
		} else {
			return -1;
		}
	}

	private boolean isGrown(WorldData world, int x, int y, int z) {
		final int blockWithMeta = world.getBlockIdWithMeta(x, y, z);
		if (Block.getBlockById(blockWithMeta >> 4) instanceof BlockCrops) {
			final int metadata = blockWithMeta & 0xf;
			return metadata >= 7;
		}
		return false;
	}

	@Override
	protected void addTasksForTarget(BlockPos currentPos) {
		if (BlockSets.AIR.isAt(world, currentPos)) {
			BlockPos farmlandPos = currentPos.add(0, -1, 0);
			if (!farmland.isAt(world, farmlandPos)) {
				addTask(new UseItemOnBlockAtTask(new ClassItemFilter(
						ItemHoe.class), farmlandPos));
			}
			addTask(new PlaceBlockAtFloorTask(currentPos, new SeedFilter(type)));
		} else {
			addTask(new DestroyBlockTask(currentPos));
		}
	}
}
