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

import java.util.List;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.path.world.BlockSets;
import net.famzangl.minecraft.minebot.ai.path.world.WorldWithDelta;
import net.famzangl.minecraft.minebot.ai.task.TaskOperations;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;

/**
 * Digs one block up and one to the side. Then jumps there.
 * 
 * @author michael
 *
 */
public class JumpMoveTask extends HorizontalMoveTask {

	private final int oldX;
	private final int oldZ;

	public JumpMoveTask(BlockPos pos, int oldX, int oldZ) {
		super(pos);
		this.oldX = oldX;
		this.oldZ = oldZ;
	}

	@Override
	protected boolean doJump(AIHelper h) {
		BlockPos player = h.getPlayerPosition();
		return player.getX() != pos.getX() || player.getZ() != pos.getZ();
	}

	@Override
	public void runTick(AIHelper h, TaskOperations o) {
		if (!BlockSets.HEAD_CAN_WALK_TRHOUGH.isAt(h.getWorld(), oldX, pos.getY() + 1, oldZ)) {
			h.faceAndDestroy(toDestroyForJump());
		} else {
			super.runTick(h, o);
		}
	}

	@Override
	public String toString() {
		return "JumpMoveTask [oldX=" + oldX + ", oldZ=" + oldZ + ", pos=" + pos
				+ "]";
	}

	@Override
	public List<BlockPos> getPredestroyPositions(AIHelper helper) {
		final List<BlockPos> list = super.getPredestroyPositions(helper);
		list.add(0, toDestroyForJump());
		return list;
	}

	private BlockPos toDestroyForJump() {
		return new BlockPos(oldX, pos.getY() + 1, oldZ);
	}
	@Override
	public boolean applyToDelta(WorldWithDelta world) {
		world.setBlock(toDestroyForJump(), Blocks.air);
		return super.applyToDelta(world);
	}
}
