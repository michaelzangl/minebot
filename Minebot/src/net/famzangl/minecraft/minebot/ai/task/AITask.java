package net.famzangl.minecraft.minebot.ai.task;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.strategy.TaskOperations;

public abstract class AITask {

	public abstract boolean isFinished(AIHelper h);

	public abstract void runTick(AIHelper h, TaskOperations o);

	public int getGameTickTimeout() {
		return 20 * 5;
	}

}
