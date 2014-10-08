package net.famzangl.minecraft.minebot.ai.task.move;

import java.util.ArrayList;
import java.util.List;

import net.famzangl.minecraft.minebot.Pos;
import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.strategy.TaskOperations;
import net.famzangl.minecraft.minebot.ai.task.AITask;
import net.famzangl.minecraft.minebot.ai.task.CanPrefaceAndDestroy;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;

public class HorizontalMoveTask extends AITask implements CanPrefaceAndDestroy {
	static final int OBSIDIAN_TIME = 10 * 20;
	protected final int x;
	protected final int y;
	protected final int z;
	private boolean hasObsidianLower;
	private boolean hasObsidianUpper;

	public HorizontalMoveTask(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	@Override
	public boolean isFinished(AIHelper h) {
		return h.isStandingOn(x, y, z);
	}

	@Override
	public void runTick(AIHelper h, TaskOperations o) {
		Block upper = h.getBlock(x, y + 1, z);
		if (!h.canWalkThrough(upper)) {
			if (AIHelper.blockIsOneOf(upper, Blocks.obsidian)) {
				hasObsidianUpper = true;
			}
			h.faceAndDestroyWithHangingBlock(x, y + 1, z);
		} else {
			Block lower = h.getBlock(x, y, z);
			if (!h.canWalkOn(lower)) {
				if (AIHelper.blockIsOneOf(lower, Blocks.obsidian)) {
					hasObsidianLower = true;
				}
				h.faceAndDestroyWithHangingBlock(x, y, z);
			} else {
				final boolean nextIsFacing = o.faceAndDestroyForNextTask();
				h.walkTowards(x + 0.5, z + 0.5, doJump(h), !nextIsFacing);
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
		return "HorizontalMoveTask [x=" + x + ", y=" + y + ", z=" + z + "]";
	}

	@Override
	public List<Pos> getPredestroyPositions(AIHelper helper) {
		final ArrayList<Pos> arrayList = new ArrayList<Pos>();
		if (!helper.canWalkThrough(helper.getBlock(x, y + 1, z))) {
			arrayList.add(new Pos(x, y + 1, z));
		}
		if (!helper.canWalkOn(helper.getBlock(x, y, z))) {
			arrayList.add(new Pos(x, y, z));
		}
		return arrayList;
	}
}
