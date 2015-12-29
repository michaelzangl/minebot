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
package net.famzangl.minecraft.minebot.build.blockbuild;

import net.famzangl.minecraft.minebot.ai.BlockItemFilter;
import net.famzangl.minecraft.minebot.ai.path.world.BlockSet;
import net.famzangl.minecraft.minebot.ai.task.AITask;
import net.famzangl.minecraft.minebot.ai.task.BlockHalf;
import net.famzangl.minecraft.minebot.ai.task.place.JumpingPlaceBlockAtSideTask;
import net.famzangl.minecraft.minebot.ai.task.place.SneakAndPlaceAtSideTask;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

public class BuildNormalStairsTask extends AbstractBuildTask {

	public static final BlockSet BLOCKS = new BlockSet(Blocks.acacia_stairs,
			Blocks.birch_stairs, Blocks.brick_stairs, Blocks.dark_oak_stairs,
			Blocks.jungle_stairs, Blocks.nether_brick_stairs,
			Blocks.oak_stairs, Blocks.sandstone_stairs, Blocks.spruce_stairs,
			Blocks.stone_brick_stairs, Blocks.stone_stairs,
			Blocks.quartz_stairs);
	private final EnumFacing upwardsDirection;
	private final boolean inverted;
	private final Block stairs;

	public static enum Half {
		UPPER, LOWER
	}

	public BuildNormalStairsTask(BlockPos forPosition, Block stairs,
			EnumFacing upwardsDirection, Half half) {
		super(forPosition);
		this.stairs = stairs;
		this.upwardsDirection = upwardsDirection;
		this.inverted = half == Half.UPPER;
		if (upwardsDirection != EnumFacing.EAST
				&& upwardsDirection != EnumFacing.WEST
				&& upwardsDirection != EnumFacing.NORTH
				&& upwardsDirection != EnumFacing.SOUTH) {
			throw new IllegalArgumentException();
		}
	}

	@Override
	protected BlockItemFilter getItemToPlaceFilter() {
		return new BlockItemFilter(stairs);
	}

	@Override
	public AITask getPlaceBlockTask(BlockPos relativeFromPos) {
		final BlockHalf side = inverted ? BlockHalf.UPPER_HALF
				: BlockHalf.LOWER_HALF;
		if (!isStandablePlace(relativeFromPos)) {
			return null;
		} else if (relativeFromPos.equals(FROM_GROUND)) {
			return new JumpingPlaceBlockAtSideTask(forPosition.add(0, 1, 0),
					getItemToPlaceFilter(), upwardsDirection.getOpposite(), side);
		} else {
			return new SneakAndPlaceAtSideTask(forPosition.add(0, 1, 0),
					getItemToPlaceFilter(), relativeFromPos, getMinHeightToBuild(),
					upwardsDirection.getOpposite(), side);
		}
	}

	@Override
	public String toString() {
		return "BuildNormalStairsTask [upwardsDirection=" + upwardsDirection
				+ ", inverted=" + inverted + ", blockFilter=" + getItemToPlaceFilter()
				+ ", forPosition=" + forPosition + "]";
	}

	@Override
	public BuildTask withPositionAndRotation(BlockPos add, int rotateSteps,
			MirrorDirection mirror) {
		EnumFacing dir = upwardsDirection;
		for (int i = 0; i < rotateSteps; i++) {
			dir = dir.rotateY();
		}

		if (mirror == MirrorDirection.EAST_WEST && dir == EnumFacing.EAST) {
			dir = EnumFacing.WEST;
		} else if (mirror == MirrorDirection.EAST_WEST
				&& dir == EnumFacing.WEST) {
			dir = EnumFacing.EAST;
		} else if (mirror == MirrorDirection.NORTH_SOUTH
				&& dir == EnumFacing.NORTH) {
			dir = EnumFacing.SOUTH;
		} else if (mirror == MirrorDirection.NORTH_SOUTH
				&& dir == EnumFacing.SOUTH) {
			dir = EnumFacing.NORTH;
		}

		return new BuildNormalStairsTask(add, stairs, dir,
				inverted ? Half.UPPER : Half.LOWER);
	}

}
