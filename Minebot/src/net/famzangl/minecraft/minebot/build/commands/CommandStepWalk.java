package net.famzangl.minecraft.minebot.build.commands;

import net.famzangl.minecraft.minebot.Pos;
import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.command.AIChatController;
import net.famzangl.minecraft.minebot.ai.command.AICommand;
import net.famzangl.minecraft.minebot.ai.command.AICommandInvocation;
import net.famzangl.minecraft.minebot.ai.command.AICommandParameter;
import net.famzangl.minecraft.minebot.ai.command.ParameterType;
import net.famzangl.minecraft.minebot.ai.command.SafeStrategyRule;
import net.famzangl.minecraft.minebot.ai.strategy.AIStrategy;
import net.famzangl.minecraft.minebot.ai.strategy.PathFinderStrategy;
import net.famzangl.minecraft.minebot.build.ForBuildPathFinder;
import net.famzangl.minecraft.minebot.build.blockbuild.BuildTask;
import net.minecraft.util.BlockPos;

@AICommand(helpText = "Go to next building site.", name = "minebuild")
public class CommandStepWalk {

	@AICommandInvocation(safeRule = SafeStrategyRule.DEFEND)
	public static AIStrategy run(
			AIHelper helper,
			@AICommandParameter(type = ParameterType.FIXED, fixedName = "step", description = "") String nameArg2,
			@AICommandParameter(type = ParameterType.FIXED, fixedName = "walk", description = "") String nameArg3) {

		final BuildTask task = helper.buildManager.peekNextTask();
		if (task == null) {
			AIChatController.addChatLine("No more build tasks.");
			return null;
		} else {
			final ForBuildPathFinder pf = new ForBuildPathFinder(task);
			return new PathFinderStrategy(pf, "Going to building site.") {
				@Override
				public void searchTasks(AIHelper helper) {
					final BlockPos atTarget = CommandBuild
							.isAroundSite(helper, task);
					if (atTarget == null) {
						super.searchTasks(helper);
					}
				}
			};
		}
	}
}
