package net.famzangl.minecraft.minebot.build.reverse;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.command.AIChatController;
import net.famzangl.minecraft.minebot.ai.strategy.AIStrategy;

public final class RunReverseBuildStrategy extends AIStrategy {
	final String outFile;
	private boolean done = false;

	public RunReverseBuildStrategy(String outFile) {
		this.outFile = outFile;
	}

	@Override
	public boolean checkShouldTakeOver(AIHelper helper) {
		return !done;
	}

	@Override
	protected TickResult onGameTick(AIHelper helper) {
		if (!done) {
			if (helper.getPos1() == null || helper.getPos2() == null) {
				AIChatController.addChatLine("Set positions first.");
			} else {
				new BuildReverser(helper, this.outFile).run();
			}
			done = true;
		}
		return TickResult.NO_MORE_WORK;
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