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
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

public class PlaceBlockAtFloorTask extends AITask {
	private final ItemFilter filter;
	private int faceTimer;
	protected final BlockPos pos;

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
	 * @param h
	 * @param dir
	 * @return
	 */
	protected boolean isFacing(AIHelper h, EnumFacing dir) {
		BlockPos facingBlock = getPlaceAtPos().offset(dir);
		return h.isFacingBlock(facingBlock, dir.getOpposite(), getSide(dir));
	}

	protected BlockHalf getSide(EnumFacing dir) {
		return BlockHalf.ANY;
	}

	protected final BlockPos getPlaceAtPos() {
		return pos.add(0, getRelativePlaceAtY(), 0);
	}

	@Override
	public boolean isFinished(AIHelper h) {
		return !BlockSets.AIR.isAt(h.getWorld(), getPlaceAtPos());
	}

	@Override
	public void runTick(AIHelper h, TaskOperations o) {
		if (faceTimer > 0) {
			faceTimer--;
		}
		if (BlockSets.AIR.isAt(h.getWorld(), getPlaceAtPos())) {
			if (!h.selectCurrentItem(filter)) {
				o.desync(new SelectTaskError(filter));
			} else {
				if (faceTimer == 0) {
					faceBlock(h, o);
					faceTimer = 2;
				} else {
					tryPlaceBlock(h);
				}
			}
		}
	}

	protected void faceBlock(AIHelper h, TaskOperations o) {
		h.faceSideOf(getPlaceAtPos().offset(EnumFacing.DOWN), EnumFacing.UP);
	}

	protected void tryPlaceBlock(AIHelper h) {
		if (isAtDesiredHeight(h)
				&& isFacingRightBlock(h)) {
			h.overrideUseItem();
		}
	}

	protected boolean isAtDesiredHeight(AIHelper h) {
		return h.getMinecraft().thePlayer
				.getEntityBoundingBox().minY >= getPlaceAtPos().getY();
	}

	protected boolean isFacingRightBlock(AIHelper h) {
		return isFacing(h, EnumFacing.DOWN);
	}

	@Override
	public String toString() {
		return "PlaceBlockAtFloorTask [filter=" + filter + ", pos=" + pos + "]";
	}

}