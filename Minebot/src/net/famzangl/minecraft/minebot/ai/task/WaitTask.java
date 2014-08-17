package net.famzangl.minecraft.minebot.ai.task;

import net.famzangl.minecraft.minebot.ai.AIHelper;

@SkipWhenSearchingPrefetch
public class WaitTask extends AITask {

	public static WaitTask instance = new WaitTask();

	@Override
	public boolean isFinished(AIHelper h) {
		return true;
	}

	@Override
	public void runTick(AIHelper h) {
	}

}
