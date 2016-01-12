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
import net.famzangl.minecraft.minebot.ai.command.BlockWithDataOrDontcare;
import net.famzangl.minecraft.minebot.ai.path.world.BlockSet;
import net.famzangl.minecraft.minebot.ai.task.AITask;
import net.famzangl.minecraft.minebot.ai.task.BlockHalf;
import net.famzangl.minecraft.minebot.ai.task.place.JumpingPlaceAtHalfTask;
import net.famzangl.minecraft.minebot.ai.task.place.SneakAndPlaceAtHalfTask;
import net.famzangl.minecraft.minebot.build.block.SlabFilter;
import net.famzangl.minecraft.minebot.build.block.SlabType;
import net.minecraft.util.BlockPos;

public class SlabBuildTask extends BlockBuildTask {

	public static final BlockSet BLOCKS = SlabType.BLOCKS;
	private final SlabType slabType;

	public SlabBuildTask(BlockPos forPosition, BlockWithDataOrDontcare block) {
		super(forPosition, block);
		this.slabType = SlabType.getForSlabBlock(block);
	}
	
	protected BlockHalf getHalf() {
		if (slabType.getBlock().containedIn(blockToPlace.toBlockSet())) {
			return BlockHalf.LOWER_HALF;
		} else {
			return BlockHalf.UPPER_HALF;
		}
	}

	@Override
	protected BlockItemFilter getItemToPlaceFilter() {
		return new SlabFilter(slabType);
	}

	@Override
	public AITask getPlaceBlockTask(BlockPos relativeFromPos) {
		if (!isStandablePlace(relativeFromPos)) {
			throw new IllegalArgumentException("Cannot build standing there: "
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
