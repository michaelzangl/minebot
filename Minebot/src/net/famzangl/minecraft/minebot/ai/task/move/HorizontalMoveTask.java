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

import java.util.ArrayList;
import java.util.List;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.path.world.BlockSet;
import net.famzangl.minecraft.minebot.ai.path.world.BlockSets;
import net.famzangl.minecraft.minebot.ai.path.world.WorldData;
import net.famzangl.minecraft.minebot.ai.path.world.WorldWithDelta;
import net.famzangl.minecraft.minebot.ai.task.AITask;
import net.famzangl.minecraft.minebot.ai.task.CanPrefaceAndDestroy;
import net.famzangl.minecraft.minebot.ai.task.TaskOperations;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;

/**
 * Digs/walks horizontaly one block wide.
 * 
 * @author michael
 *
 */
public class HorizontalMoveTask extends AITask implements CanPrefaceAndDestroy {
	private static final BlockSet hardBlocks = new BlockSet(Blocks.obsidian);
	static final int OBSIDIAN_TIME = 10 * 20;
	protected final BlockPos pos;
	private boolean hasObsidianLower;
	private boolean hasObsidianUpper;

	public HorizontalMoveTask(BlockPos pos) {
		this.pos = pos;
	}

	@Override
	public boolean isFinished(AIHelper h) {
		return h.isStandingOn(pos);
	}

	@Override
	public void runTick(AIHelper h, TaskOperations o) {
		if (needDestroyHead(h.getWorld())) {
			BlockPos upper = pos.add(0, 1, 0);
			if (hardBlocks.isAt(h.getWorld(), upper)) {
				hasObsidianUpper = true;
			}
			h.faceAndDestroyWithHangingBlock(pos.add(0, 1, 0));
		} else {
			if (needDestroyFoot(h.getWorld())) {
				if (hardBlocks.isAt(h.getWorld(), pos)) {
					hasObsidianLower = true;
				}
				h.faceAndDestroyWithHangingBlock(pos);
			} else {
				final boolean nextIsFacing = o.faceAndDestroyForNextTask();
				h.walkTowards(pos.getX() + 0.5, pos.getZ() + 0.5, doJump(h),
						!nextIsFacing);
			}
		}
	}

//	@Override
//	public int getGameTickTimeout(AIHelper helper) {
//		return super.getGameTickTimeout(helper)
//				+ (hasObsidianLower ? OBSIDIAN_TIME : 0)
//				+ (hasObsidianUpper ? OBSIDIAN_TIME : 0);
//	}

	protected boolean doJump(AIHelper h) {
		return false;
	}

	@Override
	public String toString() {
		return "HorizontalMoveTask [pos=" + pos + "]";
	}

	@Override
	public List<BlockPos> getPredestroyPositions(AIHelper helper) {
		final ArrayList<BlockPos> arrayList = new ArrayList<BlockPos>();
		WorldData world = helper.getWorld();
		if (needDestroyHead(world)) {
			arrayList.add(pos.add(0, 1, 0));
		}
		if (needDestroyFoot(world)) {
			arrayList.add(pos);
		}
		return arrayList;
	}

	private boolean needDestroyFoot(WorldData world) {
		return !BlockSets.FEET_CAN_WALK_THROUGH.isAt(world, pos);
	}

	private boolean needDestroyHead(WorldData world) {
		return !BlockSets.HEAD_CAN_WALK_TRHOUGH.isAt(world, pos.add(0, 1, 0));
	}

	@Override
	public boolean applyToDelta(WorldWithDelta world) {
		if (needDestroyHead(world)) {
			world.setBlock(pos.add(0, 1, 0), Blocks.air);
		}
		if (needDestroyFoot(world)) {
			world.setBlock(pos, Blocks.air);
		}
		world.setPlayerPosition(pos);
		return true;
	}
}
