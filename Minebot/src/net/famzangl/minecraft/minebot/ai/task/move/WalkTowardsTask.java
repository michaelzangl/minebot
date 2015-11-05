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
package net.famzangl.minecraft.minebot.ai.task.move;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.path.world.RecordingWorld;
import net.famzangl.minecraft.minebot.ai.task.AITask;
import net.famzangl.minecraft.minebot.ai.task.TaskOperations;
import net.famzangl.minecraft.minebot.ai.task.error.PositionTaskError;
import net.minecraft.util.BlockPos;

/**
 * Simply walks towards (x, z) assuming there is nothing in the way.
 * @author michael
 *
 */
public class WalkTowardsTask extends AITask {

	protected final int x;
	protected final int z;
	private BlockPos ensureOnPos;
	private BlockPos startPosition;

	public WalkTowardsTask(int x, int z, BlockPos fromPos) {
		this.x = x;
		this.z = z;
		this.ensureOnPos = this.startPosition = fromPos;
	}

	@Override
	public boolean isFinished(AIHelper h) {
		return h.arrivedAt(x + 0.5, z + 0.5);
	}

	@Override
	public void runTick(AIHelper h, TaskOperations o) {
		if (ensureOnPos != null) {
			if (!h.isStandingOn(ensureOnPos)) {
				o.desync(new PositionTaskError(ensureOnPos));
			}
			ensureOnPos = null;
		}
		if (startPosition == null) {
			startPosition = h.getPlayerPosition();
		}
		final boolean nextIsFacing = o.faceAndDestroyForNextTask();
		h.walkTowards(x + 0.5, z + 0.5, false, !nextIsFacing);
	}
	
	@Override
	public int getGameTickTimeout(AIHelper helper) {
		if (startPosition == null) {
			return super.getGameTickTimeout(helper);
		} else {
			return RecordingWorld.timeToWalk(startPosition, new BlockPos(x, startPosition.getY(), z));
		}
	}

	@Override
	public String toString() {
		return "WalkTowardsTask [x=" + x + ", z=" + z + ", fromPos=" + ensureOnPos
				+ "]";
	}

}
