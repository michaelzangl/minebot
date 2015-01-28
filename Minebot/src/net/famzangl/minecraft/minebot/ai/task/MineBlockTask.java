package net.famzangl.minecraft.minebot.ai.task;

import net.famzangl.minecraft.minebot.Pos;
import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.strategy.TaskOperations;

public class MineBlockTask extends AITask {
	private Pos pos;

	public MineBlockTask(Pos pos) {
		this.pos = pos;
	}

	@Override
	public boolean isFinished(AIHelper h) {
		return h.isAirBlock(pos);
	}

	@Override
	public void runTick(AIHelper h, TaskOperations o) {
		h.faceAndDestroy(pos);
	}

	@Override
	public String toString() {
		return "MineBlockTask [pos=" + pos + "]";
	}

}