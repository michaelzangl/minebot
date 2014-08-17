package net.famzangl.minecraft.minebot.ai.task;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.strategy.TaskOperations;

@SkipWhenSearchingPrefetch
public class WaitTask extends AITask {

	private int ticks;

	public WaitTask() {
		this(1);
	}

	public WaitTask(int ticks) {
		this.ticks = ticks;
	}

	@Override
	public boolean isFinished(AIHelper h) {
		return ticks <= 0;
	}

	@Override
	public void runTick(AIHelper h, TaskOperations o) {
		ticks--;
	}

	@Override
	public String toString() {
		return "WaitTask [ticks=" + ticks + "]";
	}

}
