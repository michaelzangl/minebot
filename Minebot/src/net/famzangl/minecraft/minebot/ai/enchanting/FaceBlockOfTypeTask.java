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
package net.famzangl.minecraft.minebot.ai.enchanting;

import java.util.List;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.path.world.BlockSet;
import net.famzangl.minecraft.minebot.ai.task.AITask;
import net.famzangl.minecraft.minebot.ai.task.TaskOperations;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;

public class FaceBlockOfTypeTask extends AITask {

	@Override
	public boolean isFinished(AIHelper h) {
		final BlockPos pos = getPos(h);
		return pos != null && h.isFacingBlock(pos.getX(), pos.getY(), pos.getZ());
	}

	private BlockPos getPos(AIHelper h) {
		List<BlockPos> positions = new BlockSet(Blocks.enchanting_table).findBlocks(h.getWorld(), h.getPlayerPosition(), 2);
		final BlockPos pos = positions.isEmpty() ? null : positions.get(1);
		return pos;
	}

	@Override
	public void runTick(AIHelper h, TaskOperations o) {
		final BlockPos pos = getPos(h);
		if (pos == null) {
			System.out.println("Could not find block around player.");
		}

		h.face(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
	}

}
