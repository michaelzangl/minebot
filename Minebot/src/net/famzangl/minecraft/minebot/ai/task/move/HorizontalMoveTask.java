package net.famzangl.minecraft.minebot.ai.task.move;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.task.AITask;

public class HorizontalMoveTask extends AITask {
	protected final int x;
	protected final int y;
	protected final int z;

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
	public void runTick(AIHelper h) {
		if (!h.isAirBlock(x, y + 1, z)) {
			h.faceAndDestroy(x, y + 1, z);
		} else if (!h.isAirBlock(x, y, z) && !h.canWalkOn(h.getBlock(x, y, z))) {
			h.faceAndDestroy(x, y, z);
		} else {
			h.walkTowards(x + 0.5, z + 0.5, doJump(h));
		}
	}

	protected boolean doJump(AIHelper h) {
		return false;
	}

	@Override
	public String toString() {
		return "HorizontalMoveTask [x=" + x + ", y=" + y + ", z=" + z + "]";
	}
}
