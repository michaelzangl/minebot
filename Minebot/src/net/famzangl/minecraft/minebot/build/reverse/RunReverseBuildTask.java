package net.famzangl.minecraft.minebot.build.reverse;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.command.AIChatController;
import net.famzangl.minecraft.minebot.ai.task.AITask;

final class RunReverseBuildTask extends AITask {
	final String outFile;

	RunReverseBuildTask(String outFile) {
		this.outFile = outFile;
	}

	private boolean done = false;

	@Override
	public void runTick(AIHelper h) {
		if (h.getPos1() == null || h.getPos2() == null) {
			AIChatController.addChatLine("Set positions first.");
		} else {
			new BuildReverser(h, this.outFile).run();
		}
		done = true;
	}

	@Override
	public boolean isFinished(AIHelper h) {
		return done;
	}
}