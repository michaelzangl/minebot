package net.famzangl.minecraft.minebot.build.commands;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.command.AICommand;
import net.famzangl.minecraft.minebot.ai.command.AICommandInvocation;
import net.famzangl.minecraft.minebot.ai.command.AICommandParameter;
import net.famzangl.minecraft.minebot.ai.command.ParameterType;
import net.famzangl.minecraft.minebot.ai.strategy.AIStrategy;

@AICommand(helpText = "Reset the internal build queue.", name = "minebuild")
public class CommandReset {

	private static final class ResetStrategy extends AIStrategy {
		@Override
		public boolean checkShouldTakeOver(AIHelper helper) {
			return helper.buildManager.peekNextTask() != null;
		}

		@Override
		protected TickResult onGameTick(AIHelper helper) {
			while (helper.buildManager.peekNextTask() != null) {
				helper.buildManager.popNextTask();
			}
			return TickResult.NO_MORE_WORK;
		}

		@Override
		public String getDescription(AIHelper helper) {
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
