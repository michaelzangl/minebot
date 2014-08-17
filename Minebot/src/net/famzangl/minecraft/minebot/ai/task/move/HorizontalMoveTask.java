package net.famzangl.minecraft.minebot.ai.task.move;

import java.util.ArrayList;
import java.util.List;

import net.famzangl.minecraft.minebot.Pos;
import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.task.AITask;
import net.famzangl.minecraft.minebot.ai.task.CanPrefaceAndDestroy;

public class HorizontalMoveTask extends AITask implements CanPrefaceAndDestroy {
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
			boolean nextIsFacing = h.faceAndDestroyForNextTask();
			h.walkTowards(x + 0.5, z + 0.5, doJump(h), !nextIsFacing);
		}
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
		ArrayList<Pos> arrayList = new ArrayList<Pos>();
		arrayList.add(new Pos(x, y + 1, z));
		if (!helper.canWalkOn(helper.getBlock(x, y, z))) {
			arrayList.add(new Pos(x, y, z));
		}
		return arrayList;
	}
}
