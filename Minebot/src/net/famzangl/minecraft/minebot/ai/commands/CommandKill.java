package net.famzangl.minecraft.minebot.ai.commands;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.AIStrategy;
import net.famzangl.minecraft.minebot.ai.animals.AnimalyType;
import net.famzangl.minecraft.minebot.ai.command.AICommand;
import net.famzangl.minecraft.minebot.ai.command.AICommandInvocation;
import net.famzangl.minecraft.minebot.ai.command.AICommandParameter;
import net.famzangl.minecraft.minebot.ai.command.ParameterType;
import net.famzangl.minecraft.minebot.ai.strategy.KillAnimalsStrategy;

@AICommand(helpText = "Starts hitting animals.\nA filter can be given.\nAvoids animals of your team.", name = "minebot")
public class CommandKill {

	@AICommandInvocation()
	public static AIStrategy run(
			AIHelper helper,
			@AICommandParameter(type = ParameterType.FIXED, fixedName = "kill", description = "") String nameArg) {
		return run(helper, nameArg, AnimalyType.ANY);
	}
	
	@AICommandInvocation()
	public static AIStrategy run(
			AIHelper helper,
			@AICommandParameter(type = ParameterType.FIXED, fixedName = "kill", description = "") String nameArg,
			@AICommandParameter(type = ParameterType.ENUM, description = "") AnimalyType type) {
		return new KillAnimalsStrategy(0, type);
	}
}
