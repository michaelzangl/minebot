package net.famzangl.minecraft.minebot.ai.task;

import net.famzangl.minecraft.minebot.ai.AIHelper;

public abstract class AITask {

	public abstract boolean isFinished(AIHelper h);

	public abstract void runTick(AIHelper h);
	
	public int getGameTickTimeout() {
		return 20 * 5;
	}

}
