package net.famzangl.minecraft.minebot.build.reverse;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.AIStrategy;
import net.famzangl.minecraft.minebot.ai.task.AITask;

final class RunReverseBuildStrategy implements AIStrategy {
	final String outFile;
	private boolean done = false;

	RunReverseBuildStrategy(String outFile) {
		this.outFile = outFile;
	}

	@Override
	public void searchTasks(AIHelper helper) {
		if (!done) {
			helper.addTask(new RunReverseBuildTask(outFile));
			done = true;
		}
	}

	@Override
	public AITask getOverrideTask(AIHelper helper) {
		return null;
	}

	@Override
	public String getDescription() {
		return "Generating build tasks.";
	}

	@Override
	public String toString() {
		return "RunReverseBuildStrategy [outFile=" + outFile + "]";
	}

}