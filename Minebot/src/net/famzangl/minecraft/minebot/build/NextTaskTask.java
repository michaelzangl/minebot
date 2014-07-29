package net.famzangl.minecraft.minebot.build;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.task.AITask;

public final class NextTaskTask extends AITask {
	private int tasksToSkip;

	public NextTaskTask() {
		this(1);
	}

	public NextTaskTask(int tasksToSkip) {
		this.tasksToSkip = tasksToSkip;
	}

	@Override
	public void runTick(AIHelper h) {
		while (tasksToSkip > 0) {
			if (h.buildManager.peekNextTask() != null) {
				h.buildManager.popNextTask();
			}
			tasksToSkip--;
		}
	}

	@Override
	public boolean isFinished(AIHelper h) {
		return tasksToSkip <= 0;
	}

	@Override
	public String toString() {
		return "NextTaskTask [tasksToSkip=" + tasksToSkip + "]";
	}

}