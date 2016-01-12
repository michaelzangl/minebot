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
import net.famzangl.minecraft.minebot.ai.path.world.BlockSet;
import net.famzangl.minecraft.minebot.ai.path.world.BlockSets;
import net.famzangl.minecraft.minebot.ai.path.world.WorldData;
import net.famzangl.minecraft.minebot.ai.path.world.WorldWithDelta;
import net.famzangl.minecraft.minebot.ai.task.AITask;
import net.famzangl.minecraft.minebot.ai.task.TaskOperations;
import net.famzangl.minecraft.minebot.ai.task.error.PositionTaskError;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;

/**
 * Digs one block down.
 * 
 * @author michael
 *
 */
public class DownwardsMoveTask extends AITask {

	private static final BlockSet hardBlocks = new BlockSet(Blocks.obsidian);

	private boolean obsidianMining;
	private BlockPos pos;

	public DownwardsMoveTask(BlockPos pos) {
		this.pos = pos;
	}

	@Override
	public boolean isFinished(AIHelper h) {
		return h.isStandingOn(pos);
	}

	@Override
	public void runTick(AIHelper h, TaskOperations o) {
		WorldData world = h.getWorld();
		if (needsToClearFootBlock(world)) {
			// tallgrass, ...
			h.faceAndDestroy(pos.add(0, 1, 0));
		} else if (!BlockSets.AIR.isAt(world, pos)) {
			if (!h.isStandingOn(pos.add(0, 1, 0))) {
				o.desync(new PositionTaskError(pos.add(0, 1, 0)));
			}
			if (hardBlocks.isAt(world, pos)) {
				obsidianMining = true;
			}

			h.faceAndDestroy(pos);
		}
	}

	private boolean needsToClearFootBlock(WorldData world) {
		return !BlockSets.AIR.isAt(world, pos.add(0, 1, 0))
				&& !world.isSideTorch(pos.add(0, 1, 0));
	}

	@Override
	public int getGameTickTimeout(AIHelper helper) {
		return super.getGameTickTimeout(helper)
				+ (obsidianMining ? HorizontalMoveTask.OBSIDIAN_TIME : 0);
	}

	@Override
	public String toString() {
		return "DownwardsMoveTask [obsidianMining=" + obsidianMining + ", pos="
				+ pos + "]";
	}

	@Override
	public boolean applyToDelta(WorldWithDelta world) {
		if (needsToClearFootBlock(world)) {
			world.setBlock(pos.add(0, 1, 0), Blocks.air);
		}
		world.setBlock(pos, Blocks.air);
		world.setPlayerPosition(pos);
		return true;
	}
}