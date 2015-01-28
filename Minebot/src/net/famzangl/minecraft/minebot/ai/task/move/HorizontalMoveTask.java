package net.famzangl.minecraft.minebot.ai.task.move;

import java.util.ArrayList;
import java.util.List;

import net.famzangl.minecraft.minebot.Pos;
import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.BlockWhitelist;
import net.famzangl.minecraft.minebot.ai.strategy.TaskOperations;
import net.famzangl.minecraft.minebot.ai.task.AITask;
import net.famzangl.minecraft.minebot.ai.task.CanPrefaceAndDestroy;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;

public class HorizontalMoveTask extends AITask implements CanPrefaceAndDestroy {
	private static final BlockWhitelist hardBlocks = new BlockWhitelist(
			Blocks.obsidian);
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
		Block upper = h.getBlock(pos.add(0,1,0));
		if (!h.canWalkThrough(upper)) {
			if (hardBlocks.contains(upper)) {
				hasObsidianUpper = true;
			}
			h.faceAndDestroyWithHangingBlock(pos.add(0,1,0));
		} else {
			Block lower = h.getBlock(pos);
			if (!h.canWalkOn(lower)) {
				if (hardBlocks.contains(lower)) {
					hasObsidianLower = true;
				}
				h.faceAndDestroyWithHangingBlock(pos);
			} else {
				final boolean nextIsFacing = o.faceAndDestroyForNextTask();
				h.walkTowards(pos.getX() + 0.5, pos.getZ() + 0.5, doJump(h), !nextIsFacing);
			}
		}
	}

	@Override
	public int getGameTickTimeout() {
		return super.getGameTickTimeout() + (hasObsidianLower ? OBSIDIAN_TIME : 0) + (hasObsidianUpper ? OBSIDIAN_TIME : 0) ;
	}

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
		if (!helper.canWalkThrough(helper.getBlock(pos.add(0,1,0)))) {
			arrayList.add(pos.add(0,1,0));
		}
		if (!helper.canWalkOn(helper.getBlock(pos))) {
			arrayList.add(pos);
		}
		return arrayList;
	}
}
