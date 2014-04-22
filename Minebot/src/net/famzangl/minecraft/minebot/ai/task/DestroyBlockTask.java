package net.famzangl.minecraft.minebot.ai.task;

import net.famzangl.minecraft.minebot.ai.AIHelper;

public class DestroyBlockTask implements AITask {
	private int x;
	private int y;
	private int z;

	public DestroyBlockTask(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	@Override
	public boolean isFinished(AIHelper h) {
		return h.isAirBlock(x, y, z);
	}

	@Override
	public void runTick(AIHelper h) {
		h.faceAndDestroy(x, y, z);
	}

}
