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
import net.famzangl.minecraft.minebot.ai.path.world.BlockSets;
import net.famzangl.minecraft.minebot.ai.task.AITask;
import net.famzangl.minecraft.minebot.ai.task.BlockHalf;
import net.famzangl.minecraft.minebot.ai.task.place.JumpingPlaceBlockAtSideTask;
import net.famzangl.minecraft.minebot.ai.task.place.SneakAndPlaceAtSideTask;
import net.minecraft.block.BlockState;
import net.minecraft.block.StairsBlock;
import net.minecraft.state.properties.Half;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;

public class BuildNormalStairsTask extends AbstractBuildTask {

	public static final BlockSet BLOCKS = BlockSets.STAIRS;
	private final BlockState stairs;
	private final Direction upwardsDirection;

	public BuildNormalStairsTask(BlockPos forPosition, BlockState stairs) {
		super(forPosition);
		if (!(stairs.getBlock() instanceof StairsBlock)) {
			throw new IllegalArgumentException("Not a staris block: " + stairs);
		}
		this.stairs = stairs;
		this.upwardsDirection = stairs.get(StairsBlock.HALF) == Half.BOTTOM
				? Direction.UP : Direction.DOWN;
	}

	@Override
	protected BlockItemFilter getItemToPlaceFilter() {
		return new BlockItemFilter(stairs);
	}

	@Override
	public AITask getPlaceBlockTask(BlockPos relativeFromPos) {
		final BlockHalf side = stairs.get(StairsBlock.HALF) == Half.TOP ? BlockHalf.UPPER_HALF
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
				+ ", blockFilter=" + getItemToPlaceFilter()
				+ ", forPosition=" + forPosition + "]";
	}

	@Override
	public BuildTask withPositionAndRotation(BlockPos add, int rotateSteps,
			MirrorDirection mirror) {
		Direction dir = upwardsDirection;
		for (int i = 0; i < rotateSteps; i++) {
			dir = dir.rotateY();
		}

		if (mirror == MirrorDirection.EAST_WEST && dir == Direction.EAST) {
			dir = Direction.WEST;
		} else if (mirror == MirrorDirection.EAST_WEST
				&& dir == Direction.WEST) {
			dir = Direction.EAST;
		} else if (mirror == MirrorDirection.NORTH_SOUTH
				&& dir == Direction.NORTH) {
			dir = Direction.SOUTH;
		} else if (mirror == MirrorDirection.NORTH_SOUTH
				&& dir == Direction.SOUTH) {
			dir = Direction.NORTH;
		}

		return new BuildNormalStairsTask(add, stairs.with(StairsBlock.FACING, dir));
	}
	
	@Override
	public Object[] getCommandArguments() {
		// TODO: Add state
		return new Object[] { stairs.getBlock().getRegistryName() };
	}

}
