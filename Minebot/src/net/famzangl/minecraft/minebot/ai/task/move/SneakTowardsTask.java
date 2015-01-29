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
import net.famzangl.minecraft.minebot.ai.task.AITask;
import net.famzangl.minecraft.minebot.ai.task.TaskOperations;
import net.minecraft.util.EnumFacing;

/**
 * Sneak standing on (x, y - 1, z) towards the direction, so that we are
 * standing on the edge of the block.
 * 
 * @author michael
 * 
 */
public class SneakTowardsTask extends AITask {
	private final int x;
	private final int y;
	private final int z;
	private final EnumFacing dir;

	public SneakTowardsTask(int x, int y, int z, EnumFacing dir) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.dir = dir;
	}

	@Override
	public boolean isFinished(AIHelper h) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void runTick(AIHelper h, TaskOperations o) {
		// TODO Auto-generated method stub

	}

}
