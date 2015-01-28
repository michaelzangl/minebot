package net.famzangl.minecraft.minebot.ai.task;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.minecraft.util.BlockPos;

public class MineBlockTask extends AITask {
	private BlockPos pos;

	public MineBlockTask(BlockPos pos) {
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