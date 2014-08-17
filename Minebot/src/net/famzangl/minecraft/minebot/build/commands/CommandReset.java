package net.famzangl.minecraft.minebot.build.commands;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.AIStrategy;
import net.famzangl.minecraft.minebot.ai.command.AICommand;
import net.famzangl.minecraft.minebot.ai.command.AICommandInvocation;
import net.famzangl.minecraft.minebot.ai.command.AICommandParameter;
import net.famzangl.minecraft.minebot.ai.command.ParameterType;
import net.famzangl.minecraft.minebot.ai.task.AITask;
import net.famzangl.minecraft.minebot.build.NextTaskTask;

@AICommand(helpText = "Reset the internal build queue.", name = "minebuild")
public class CommandReset {

	private static final class ResetStrategy implements AIStrategy {
		@Override
		public void searchTasks(AIHelper helper) {
			if (helper.buildManager.peekNextTask() != null) {
				helper.addTask(new NextTaskTask(10000));
			}
		}

		@Override
		public AITask getOverrideTask(AIHelper helper) {
			return null;
		}

		@Override
		public String getDescription() {
			return "Clearing build list.";
		}
	}

	@AICommandInvocation()
	public static AIStrategy run(
			AIHelper helper,
			@AICommandParameter(type = ParameterType.FIXED, fixedName = "reset", description = "") String nameArg) {
		return new ResetStrategy();
	}
}
