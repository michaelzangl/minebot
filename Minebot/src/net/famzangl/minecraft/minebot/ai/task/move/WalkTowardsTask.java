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

import net.famzangl.minecraft.minebot.Pos;
import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.task.AITask;
import net.famzangl.minecraft.minebot.ai.task.TaskOperations;
import net.famzangl.minecraft.minebot.ai.task.error.PositionTaskError;

/**
 * Simply walks towards (x, z) assuming there is nothing in the way.
 * @author michael
 *
 */
public class WalkTowardsTask extends AITask {

	private final int x;
	private final int z;
	private Pos fromPos;

	public WalkTowardsTask(int x, int z, Pos fromPos) {
		this.x = x;
		this.z = z;
		this.fromPos = fromPos;
	}

	@Override
	public boolean isFinished(AIHelper h) {
		return h.arrivedAt(x + 0.5, z + 0.5);
	}

	@Override
	public void runTick(AIHelper h, TaskOperations o) {
		if (fromPos != null) {
			if (!h.isStandingOn(fromPos.getX(), fromPos.getY(), fromPos.getZ())) {
				o.desync(new PositionTaskError(fromPos.getX(), fromPos.getY(), fromPos.getZ()));
			}
			fromPos = null;
		}
		final boolean nextIsFacing = o.faceAndDestroyForNextTask();
		h.walkTowards(x + 0.5, z + 0.5, false, !nextIsFacing);
	}

	@Override
	public String toString() {
		return "WalkTowardsTask [x=" + x + ", z=" + z + ", fromPos=" + fromPos
				+ "]";
	}

}
