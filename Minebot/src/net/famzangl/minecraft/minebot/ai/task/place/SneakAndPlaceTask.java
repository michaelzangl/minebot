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
import net.famzangl.minecraft.minebot.ai.path.world.BlockSets;
import net.famzangl.minecraft.minebot.ai.task.AITask;
import net.famzangl.minecraft.minebot.ai.task.TaskOperations;
import net.famzangl.minecraft.minebot.ai.task.error.SelectTaskError;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovementInput;

/**
 * Sneak towards (x, y, z) and then place the block below you.
 * 
 * @author michael
 * 
 */
public class SneakAndPlaceTask extends AITask {

	protected final BlockPos pos;
	protected final BlockItemFilter filter;
	protected final BlockPos relativeFrom;
	/**
	 * Direction we need to walk.
	 */
	private final EnumFacing inDirection;
	private final double minBuildHeight;
	private int faceTimer;

	/**
	 * 
	 * @param x
	 * @param y
	 * @param z
	 * @param filter
	 * @param relativeFrom
	 *            Vector: place position -> start standing pos.
	 * @param minBuildHeight
	 */
	public SneakAndPlaceTask(BlockPos pos, BlockItemFilter filter,
			BlockPos relativeFrom, double minBuildHeight) {
		this.pos = pos;
		this.filter = filter;
		this.relativeFrom = relativeFrom;
		this.minBuildHeight = minBuildHeight;
		final EnumFacing foundInDir = AIHelper.getDirectionForXZ(
				-relativeFrom.getX(), -relativeFrom.getZ());
		if (relativeFrom.getY() != 1 || foundInDir == null) {
			throw new IllegalArgumentException();
		}
		inDirection = foundInDir;
	}

	@Override
	public boolean isFinished(AIHelper h) {
		return !BlockSets.AIR.isAt(h.getWorld(), getPositionToPlaceAt()) && !h.isJumping();
	}

	protected BlockPos getPositionToPlaceAt() {
		return pos.add(0, -1, 0);
	}

	@Override
	public void runTick(AIHelper h, TaskOperations o) {
		if (faceTimer > 0) {
			faceTimer--;
		}
		if (h.sneakFrom(getFromPos(), inDirection, faceWhileSneaking())) {
			final boolean hasRequiredHeight = h.getMinecraft().thePlayer
					.getEntityBoundingBox().minY > minBuildHeight - 0.05;
			if (hasRequiredHeight) {
				if (faceTimer == 0) {
					faceBlock(h, o);
					faceTimer = 3;
				} else if (isFacingRightBlock(h)) {
					if (h.selectCurrentItem(filter)) {
						h.overrideUseItem();
					} else {
						o.desync(new SelectTaskError(filter));
					}
				}
			} else {
				final MovementInput i = new MovementInput();
				i.jump = true;
				h.overrideMovement(i);
			}
		}
	}

	protected boolean faceWhileSneaking() {
		return false;
	}

	protected boolean isFacingRightBlock(AIHelper h) {
		return h.isFacingBlock(getFromPos(), inDirection);
	}

	protected void faceBlock(AIHelper h, TaskOperations o) {
		h.faceSideOf(getFromPos(), inDirection);
	}

	protected BlockPos getFromPos() {
		return pos.add(relativeFrom.getX(), -1, relativeFrom.getZ());
	}

	@Override
	public String toString() {
		return "SneakAndPlaceTask [pos=" + pos + ", filter=" + filter
				+ ", relativeFrom=" + relativeFrom + ", inDirection="
				+ inDirection + ", minBuildHeight=" + minBuildHeight + "]";
	}

}
