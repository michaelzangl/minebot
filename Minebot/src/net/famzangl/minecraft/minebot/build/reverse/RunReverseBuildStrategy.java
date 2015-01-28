package net.famzangl.minecraft.minebot.build.reverse;

import java.io.File;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.command.AIChatController;
import net.famzangl.minecraft.minebot.ai.strategy.AIStrategy;

/**
 * Does a reverse build of the selected area and stores the result to a file.
 * @author michael
 *
 */
public final class RunReverseBuildStrategy extends AIStrategy {
	final File outFile;
	private boolean done = false;

	public RunReverseBuildStrategy(File file) {
		this.outFile = file;
	}

	@Override
	public boolean checkShouldTakeOver(AIHelper helper) {
		return !done;
	}

	@Override
	protected TickResult onGameTick(AIHelper helper) {
		if (!done) {
			if (helper.getPos1() == null || helper.getPos2() == null) {
				AIChatController.addChatLine("Set positions first. You can use /minebuild pos1 or /minebuild pos2.");
			} else {
				new BuildReverser(helper, this.outFile).run();
			}
			done = true;
		}
		return TickResult.NO_MORE_WORK;
	}

	@Override
	public String getDescription(AIHelper helper) {
		return "Generating build tasks.";
	}

	@Override
	public String toString() {
		return "RunReverseBuildStrategy [outFile=" + outFile + "]";
	}

}