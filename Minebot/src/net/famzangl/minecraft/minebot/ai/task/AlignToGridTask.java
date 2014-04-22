package net.famzangl.minecraft.minebot.ai.task;

import net.famzangl.minecraft.minebot.Pos;
import net.famzangl.minecraft.minebot.ai.AIHelper;


public class AlignToGridTask implements AITask {
	private int x;
	private int y;
	private int z;

	public AlignToGridTask(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public AlignToGridTask(Pos p) {
		this(p.x, p.y, p.z);
	}

	@Override
	public boolean isFinished(AIHelper h) {
		return h.isStandingOn(x, y, z);
	}

	@Override
	public void runTick(AIHelper h) {
		h.walkTowards(x + 0.5, z + 0.5, false);
	}

	@Override
	public String toString() {
		return "AlignToGridTask [x=" + x + ", y=" + y + ", z=" + z + "]";
	}

}
