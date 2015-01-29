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

import java.util.Arrays;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.BlockItemFilter;
import net.famzangl.minecraft.minebot.ai.task.BlockSide;
import net.famzangl.minecraft.minebot.ai.task.TaskOperations;
import net.famzangl.minecraft.minebot.ai.task.error.StringTaskError;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

public class SneakAndPlaceAtHalfTask extends SneakAndPlaceTask {

	protected final BlockSide side;

	protected EnumFacing lookingDirection = null;

	protected final EnumFacing[] DIRS = new EnumFacing[] {
			EnumFacing.EAST, EnumFacing.NORTH, EnumFacing.WEST,
			EnumFacing.SOUTH };

	private int attempts;

	protected EnumFacing[] getBuildDirs() {
		return DIRS;
	}

	public SneakAndPlaceAtHalfTask(BlockPos pos, BlockItemFilter filter,
			BlockPos relativeFrom, double minBuildHeight, BlockSide side) {
		super(pos, filter, relativeFrom, minBuildHeight);
		this.side = side;
	}

	@Override
	protected void faceBlock(AIHelper h, TaskOperations o) {
		final EnumFacing[] dirs = getBuildDirs();
		attempts++;
		for (int i = 0; i < dirs.length; i++) {
			final EnumFacing useSide = dirs[attempts / 10 % dirs.length];
			if (!h.isAirBlock(getFromPos())) {
				faceSideBlock(h, useSide);
				return;
			} else {
				attempts += 10;
			}
		}
		o.desync(new StringTaskError("Could not face anywhere to place."));
	}

	private void faceSideBlock(AIHelper h, EnumFacing useSide) {
		h.faceSideOf(getFromPos(),
				useSide.getOpposite(), side == BlockSide.UPPER_HALF ? 0.5 : 0,
				side == BlockSide.LOWER_HALF ? 0.5 : 1,
				h.getMinecraft().thePlayer.posX - pos.getX(),
				h.getMinecraft().thePlayer.posZ - pos.getZ(), lookingDirection);
	}

	private boolean isFacing(AIHelper h, EnumFacing dir) {
		return h.isFacingBlock(getFromPos(),
				dir.getOpposite(), side);
	}

	@Override
	protected boolean isFacingRightBlock(AIHelper h) {
		for (final EnumFacing d : getBuildDirs()) {
			if (isFacing(h, d)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public String toString() {
		return "SneakAndPlaceAtHalfTask [side=" + side + ", lookingDirection="
				+ lookingDirection + ", DIRS=" + Arrays.toString(DIRS)
				+ ", attempts=" + attempts + ", pos=" + pos + ", filter="
				+ filter + ", relativeFrom=" + relativeFrom + "]";
	}

}
