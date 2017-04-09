package net.famzangl.minecraft.minebot.ai.task;

import net.famzangl.minecraft.minebot.ai.AIHelper;

public abstract class RunOnceTask extends AITask {

	private boolean run;

	@Override
	public boolean isFinished(AIHelper aiHelper) {
		return run;
	}

	@Override
	public void runTick(AIHelper aiHelper, TaskOperations taskOperations) {
		runOnce(aiHelper, taskOperations);
		run = true;
	}

	protected abstract void runOnce(AIHelper aiHelper, TaskOperations taskOperations);

}
