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
import net.famzangl.minecraft.minebot.ai.path.world.WorldWithDelta;
import net.famzangl.minecraft.minebot.ai.task.AITask;
import net.famzangl.minecraft.minebot.ai.task.SkipWhenSearchingPrefetch;
import net.famzangl.minecraft.minebot.ai.task.TaskOperations;
import net.minecraft.util.BlockPos;

/**
 * Ensures that the player is standing on a block. If it is not, it walks to the
 * nearest block center.
 * 
 * @author michael
 * 
 */
@SkipWhenSearchingPrefetch
public class AlignToGridTask extends AITask {
	private final int x;
	private final int y;
	private final int z;

	public AlignToGridTask(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public AlignToGridTask(BlockPos p) {
		this(p.getX(), p.getY(), p.getZ());
	}

	@Override
	public boolean isFinished(AIHelper h) {
		return h.isStandingOn(x, y, z);
	}

	@Override
	public void runTick(AIHelper h, TaskOperations o) {
		h.walkTowards(x + 0.5, z + 0.5, false, !o.faceAndDestroyForNextTask());
	}

	@Override
	public String toString() {
		return "AlignToGridTask [x=" + x + ", y=" + y + ", z=" + z + "]";
	}
	
	@Override
	public boolean applyToDelta(WorldWithDelta world) {
		world.setPlayerPosition(new BlockPos(x, y, z));
		return true;
	}
}
