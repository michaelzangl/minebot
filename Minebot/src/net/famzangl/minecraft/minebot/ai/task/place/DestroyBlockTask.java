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
import net.famzangl.minecraft.minebot.ai.path.world.BlockSets;
import net.famzangl.minecraft.minebot.ai.path.world.WorldWithDelta;
import net.famzangl.minecraft.minebot.ai.task.AITask;
import net.famzangl.minecraft.minebot.ai.task.TaskOperations;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;

/**
 * Simply destroy a block at a given position, assuming it is in reach.
 * 
 * @author michael
 *
 */
public class DestroyBlockTask extends AITask {
	private BlockPos pos;

	public DestroyBlockTask(BlockPos pos) {
		this.pos = pos;
	}

	@Override
	public boolean isFinished(AIHelper h) {
		return BlockSets.AIR.isAt(h.getWorld(), pos);
	}

	@Override
	public void runTick(AIHelper h, TaskOperations o) {
		h.faceAndDestroy(pos);
	}

	@Override
	public boolean applyToDelta(WorldWithDelta world) {
		world.setBlock(pos, Blocks.air);
		return true;
	}
}
