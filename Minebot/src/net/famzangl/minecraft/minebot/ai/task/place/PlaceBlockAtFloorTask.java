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
import net.famzangl.minecraft.minebot.ai.ItemFilter;
import net.famzangl.minecraft.minebot.ai.path.world.BlockSets;
import net.famzangl.minecraft.minebot.ai.task.AITask;
import net.famzangl.minecraft.minebot.ai.task.BlockHalf;
import net.famzangl.minecraft.minebot.ai.task.TaskOperations;
import net.famzangl.minecraft.minebot.ai.task.error.SelectTaskError;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

public class PlaceBlockAtFloorTask extends AITask {
	private final ItemFilter filter;
	private int faceTimer;
	protected final BlockPos pos;

	/**
	 * 
	 * @param pos
	 *            The position we are standing on when placing the block. See {@link #getRelativePlaceAtY()}
	 * @param filter
	 *            What to place.
	 */
	public PlaceBlockAtFloorTask(BlockPos pos, ItemFilter filter) {
		this.pos = pos;
		this.filter = filter;
	}

	protected int getRelativePlaceAtY() {
		return 0;
	}

	/**
	 * Check if we face the adjacent block in that direction.
	 * 
	 * @param aiHelper
	 * @param dir
	 * @return
	 */
	protected boolean isFacing(AIHelper aiHelper, EnumFacing dir) {
		BlockPos facingBlock = getPlaceAtPos().offset(dir);
		return aiHelper.isFacingBlock(facingBlock, dir.getOpposite(), getSide(dir));
	}

	protected BlockHalf getSide(EnumFacing dir) {
		return BlockHalf.ANY;
	}

	protected final BlockPos getPlaceAtPos() {
		return pos.add(0, getRelativePlaceAtY(), 0);
	}

	@Override
	public boolean isFinished(AIHelper aiHelper) {
		return !BlockSets.AIR.isAt(aiHelper.getWorld(), getPlaceAtPos());
	}

	@Override
	public void runTick(AIHelper aiHelper, TaskOperations taskOperations) {
		if (faceTimer > 0) {
			faceTimer--;
		}
		if (BlockSets.AIR.isAt(aiHelper.getWorld(), getPlaceAtPos())) {
			if (!aiHelper.selectCurrentItem(filter)) {
				taskOperations.desync(new SelectTaskError(filter));
			} else {
				if (faceTimer == 0) {
					faceBlock(aiHelper, taskOperations);
					faceTimer = 2;
				} else {
					tryPlaceBlock(aiHelper);
				}
			}
		}
	}

	protected void faceBlock(AIHelper aiHelper, TaskOperations o) {
		aiHelper.faceSideOf(getPlaceAtPos().offset(EnumFacing.DOWN), EnumFacing.UP);
	}

	protected void tryPlaceBlock(AIHelper aiHelper) {
		if (isAtDesiredHeight(aiHelper) && isFacingRightBlock(aiHelper)) {
			aiHelper.overrideUseItem();
		}
	}

	protected boolean isAtDesiredHeight(AIHelper aiHelper) {
		return aiHelper.getMinecraft().player.getEntityBoundingBox().minY >= getPlaceAtPos()
				.getY();
	}

	protected boolean isFacingRightBlock(AIHelper aiHelper) {
		return isFacing(aiHelper, EnumFacing.DOWN);
	}

	@Override
	public String toString() {
		return "PlaceBlockAtFloorTask [filter=" + filter + ", pos=" + pos + "]";
	}

}