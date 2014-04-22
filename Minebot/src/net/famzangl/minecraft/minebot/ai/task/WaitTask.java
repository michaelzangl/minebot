package net.famzangl.minecraft.minebot.ai.task;

import net.famzangl.minecraft.minebot.ai.AIHelper;

public class WaitTask implements AITask {

	public static WaitTask instance = new WaitTask();

	@Override
	public boolean isFinished(AIHelper h) {
		return true;
	}

	@Override
	public void runTick(AIHelper h) {
	}

}
