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
import net.famzangl.minecraft.minebot.ai.strategy.TaskStrategy;
import net.famzangl.minecraft.minebot.ai.task.AITask;
import net.famzangl.minecraft.minebot.build.blockbuild.BuildTask;
import net.minecraft.util.BlockPos;

@AICommand(helpText = "Place the next block.\n Use the walk command before this command or walk there yourself.", name = "minebuild")
public class CommandStepPlace {

	@AICommandInvocation(safeRule = SafeStrategyRule.DEFEND)
	public static AIStrategy run(
			AIHelper helper,
			@AICommandParameter(type = ParameterType.FIXED, fixedName = "step", description = "") String nameArg2,
			@AICommandParameter(type = ParameterType.FIXED, fixedName = "place", description = "") String nameArg3) {
		return new TaskStrategy() {

			@Override
			protected void searchTasks(AIHelper helper) {
				final BuildTask task = helper.buildManager.peekNextTask();
				if (task == null) {
					AIChatController.addChatLine("No more build tasks.");
				} else {
					addStep(helper, task);
				}
			}

			private void addStep(AIHelper helper, final BuildTask task) {
				final BlockPos forPosition = task.getForPosition();
				final BlockPos fromPos = getFromPos(helper, task, forPosition);
				if (fromPos == null) {
					AIChatController.addChatLine("Not at starting position.");
				} else if (helper.isAirBlock(task.getForPosition().getX(),
						task.getForPosition().getY(), task.getForPosition().getZ())) {
					final AITask t = task.getPlaceBlockTask(fromPos);
					if (t != null) {
						addTask(t);
					}
				} else {
					AIChatController
							.addChatLine("Could not place the block: There is already something!");
				}
			}

			@Override
			public String getDescription(AIHelper helper) {
				return "Place the block.";
			}
		};
	}

	private static BlockPos getFromPos(AIHelper helper, final BuildTask task,
			BlockPos forPosition) {
		BlockPos fromPos = null;
		for (final BlockPos p : task.getStandablePlaces()) {
			if (helper.isStandingOn(p.getX() + forPosition.getX(), p.getY() + forPosition.getY(),
					p.getZ() + forPosition.getZ())) {
				fromPos = p;
			}
		}
		return fromPos;
	}
}
