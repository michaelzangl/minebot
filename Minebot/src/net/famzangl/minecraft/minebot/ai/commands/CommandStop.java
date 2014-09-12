package net.famzangl.minecraft.minebot.ai.commands;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.command.AICommand;
import net.famzangl.minecraft.minebot.ai.command.AICommandInvocation;
import net.famzangl.minecraft.minebot.ai.command.AICommandParameter;
import net.famzangl.minecraft.minebot.ai.command.ParameterType;
import net.famzangl.minecraft.minebot.ai.strategy.AIStrategy;
import net.famzangl.minecraft.minebot.ai.strategy.StopInStrategy;
import net.famzangl.minecraft.minebot.ai.strategy.StopStrategy;

@AICommand(name = "minebot", helpText = "Stop whatever you are doing.")
public class CommandStop {
	@AICommandInvocation()
	public static AIStrategy run(
			AIHelper helper,
			@AICommandParameter(type = ParameterType.FIXED, fixedName = "stop", description = "") String nameArg) {
		return new StopStrategy();
	}

	@AICommandInvocation()
	public static AIStrategy run(
			AIHelper helper,
			@AICommandParameter(type = ParameterType.FIXED, fixedName = "stop", description = "") String nameArg,
			@AICommandParameter(type = ParameterType.FIXED, fixedName = "after", description = "") String nameArg2,
			@AICommandParameter(type = ParameterType.NUMBER, fixedName = "", description = "Seconds") int seconds) {
		return new StopInStrategy(seconds, false);
	}

	@AICommandInvocation()
	public static AIStrategy run(
			AIHelper helper,
			@AICommandParameter(type = ParameterType.FIXED, fixedName = "stop", description = "") String nameArg,
			@AICommandParameter(type = ParameterType.FIXED, fixedName = "after", description = "") String nameArg2,
			@AICommandParameter(type = ParameterType.NUMBER, fixedName = "", description = "Seconds") int seconds,
			@AICommandParameter(type = ParameterType.FIXED, fixedName = "force", description = "") String nameArg3) {
		return new StopInStrategy(seconds, true);
	}
}
