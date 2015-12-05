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
package net.famzangl.minecraft.minebot.ai.task.place;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.BlockItemFilter;
import net.famzangl.minecraft.minebot.ai.task.BlockHalf;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

public class SneakAndPlaceAtSideTask extends SneakAndPlaceAtHalfTask {

	public SneakAndPlaceAtSideTask(BlockPos pos, BlockItemFilter filter,
			BlockPos relativeFrom, double minBuildHeight,
			EnumFacing lookingDirection, BlockHalf side) {
		super(pos, filter, relativeFrom, minBuildHeight, side);
		this.lookingDirection = lookingDirection;
	}

	@Override
	protected EnumFacing[] createBuildDirsUnordered() {
		return new EnumFacing[] { lookingDirection, lookingDirection.rotateY(),
				lookingDirection.rotateYCCW() };
	}

	@Override
	protected boolean isFacingRightBlock(AIHelper h) {
		if (h.getLookDirection() != lookingDirection) {
			System.out.println("Not the right dir!");
			return false;
		} else {
			return super.isFacingRightBlock(h);
		}
	}

	@Override
	public String toString() {
		return "SneakAndPlaceAtSideTask [blockHalf=" + blockHalf
				+ ", lookingDirection=" + lookingDirection
				+ ", destinationStandPosition=" + destinationStandPosition
				+ ", startStandPosition=" + startStandPosition + ", filter="
				+ filter + "]";
	}

}
