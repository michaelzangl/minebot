package net.famzangl.minecraft.minebot.build;

import net.famzangl.minecraft.minebot.Pos;
import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.AIStrategy;
import net.famzangl.minecraft.minebot.ai.command.AIChatController;
import net.famzangl.minecraft.minebot.ai.command.AICommand;
import net.famzangl.minecraft.minebot.ai.strategy.PathFinderStrategy;
import net.famzangl.minecraft.minebot.build.blockbuild.BuildTask;
import net.minecraft.command.ICommandSender;

/**
 * 
 * @author michael
 * 
 */
public class CommandStepWalk implements AICommand {

	@Override
	public String getName() {
		return "build:walk";
	}

	@Override
	public String getArgsUsage() {
		return "";
	}

	@Override
	public String getHelpText() {
		return "Walk for the next build task.";
	}

	@Override
	public AIStrategy evaluateCommand(ICommandSender sender, String[] args,
			AIHelper h, AIChatController aiChatController) {
		if (args.length != 1) {
			aiChatController.usage(this);
			return null;
		} else {
			final BuildTask task = h.buildManager.peekNextTask();
			if (task == null) {
				AIChatController.addChatLine("No more build tasks.");
				return null;
			} else {
				ForBuildPathFinder pf = new ForBuildPathFinder(h, task);
				return new PathFinderStrategy(pf, "Going to building site.") {
					@Override
					public void searchTasks(AIHelper helper) {
						Pos atTarget = CommandBuild.isAroundSite(helper, task);
						if (atTarget == null) {
							super.searchTasks(helper);
						}
					}
				};
			}
		}
	}

}
