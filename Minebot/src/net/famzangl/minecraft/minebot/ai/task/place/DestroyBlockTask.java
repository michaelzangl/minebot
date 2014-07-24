package net.famzangl.minecraft.minebot.ai.task.place;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.task.AITask;

public class DestroyBlockTask implements AITask {
	private final int x;
	private final int y;
	private final int z;

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
