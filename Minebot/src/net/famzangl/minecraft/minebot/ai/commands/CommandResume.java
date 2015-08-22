package net.famzangl.minecraft.minebot.ai.commands;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.command.AICommand;
import net.famzangl.minecraft.minebot.ai.command.AICommandInvocation;
import net.famzangl.minecraft.minebot.ai.command.AICommandParameter;
import net.famzangl.minecraft.minebot.ai.command.ParameterType;
import net.famzangl.minecraft.minebot.ai.command.SafeStrategyRule;
import net.famzangl.minecraft.minebot.ai.strategy.AIStrategy;
import net.famzangl.minecraft.minebot.ai.strategy.RespawnStrategy;

@AICommand(helpText = "Resume the last thing that was aborted.", name = "minebot")
public class CommandResume {

	@AICommandInvocation(safeRule = SafeStrategyRule.NONE)
	public static AIStrategy run(
			AIHelper helper,
			@AICommandParameter(type = ParameterType.FIXED, fixedName = "resume", description = "") String nameArg) {
		AIStrategy toResume = helper.getResumeStrategy();
		System.out.println("Resuming: " + toResume);
		return toResume;
	}
}
