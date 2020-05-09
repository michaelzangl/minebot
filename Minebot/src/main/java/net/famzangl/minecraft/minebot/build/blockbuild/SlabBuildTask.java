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

import net.famzangl.minecraft.minebot.ai.path.world.BlockSet;
import net.famzangl.minecraft.minebot.ai.path.world.BlockSets;
import net.famzangl.minecraft.minebot.ai.task.AITask;
import net.famzangl.minecraft.minebot.ai.task.BlockHalf;
import net.famzangl.minecraft.minebot.ai.task.place.JumpingPlaceAtHalfTask;
import net.famzangl.minecraft.minebot.ai.task.place.SneakAndPlaceAtHalfTask;
import net.minecraft.block.BlockState;
import net.minecraft.block.SlabBlock;
import net.minecraft.state.properties.SlabType;
import net.minecraft.util.math.BlockPos;

public class SlabBuildTask extends BlockBuildTask {

			// Only the lower and the upper ones, but not the double slabs
	public static final BlockSet BLOCKS =
			BlockSet.builder().add(BlockSets.LOWER_SLABS)
			.add(BlockSets.UPPER_SLABS).build();

	public SlabBuildTask(BlockPos forPosition, BlockState block) {
		super(forPosition, block);
		SlabType half = blockToPlace.get(SlabBlock.TYPE);
		if (half != SlabType.BOTTOM && half != SlabType.TOP) {
			throw new IllegalArgumentException("Not a real slab: " + half);
		}
	}
	
	protected BlockHalf getHalf() {
		SlabType half = blockToPlace.get(SlabBlock.TYPE);

		return half == SlabType.TOP ? BlockHalf.UPPER_HALF : BlockHalf.LOWER_HALF;
	}

	@Override
	public AITask getPlaceBlockTask(BlockPos relativeFromPos) {
		if (!isStandablePlace(relativeFromPos)) {
			throw new IllegalArgumentException("Cannot build a slab at " + forPosition + " standing at relative position: "
					+ relativeFromPos);
		} else if (relativeFromPos.equals(FROM_GROUND)) {
			return new JumpingPlaceAtHalfTask(forPosition.add(0, 1, 0),
					getItemToPlaceFilter(), getHalf());
		} else {
			return new SneakAndPlaceAtHalfTask(forPosition.add(0, 1, 0),
					getItemToPlaceFilter(), forPosition.add(relativeFromPos),
					getMinHeightToBuild(), getHalf());
		}
	}

	@Override
	protected double getBlockHeight() {
		return getHalf() == BlockHalf.LOWER_HALF ? .5 : 1;
	}

	@Override
	public String toString() {
		return "BuildHalfslabTask [side=" + getHalf() + ", blockFilter="
				+ getItemToPlaceFilter() + ", forPosition=" + forPosition + "]";
	}

	@Override
	public BuildTask withPositionAndRotation(BlockPos add, int rotateSteps,
			MirrorDirection mirror) {
		return new SlabBuildTask(add, blockToPlace);
	}
}
