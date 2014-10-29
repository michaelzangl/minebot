package net.famzangl.minecraft.minebot.ai.commands;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.animals.AnimalyType;
import net.famzangl.minecraft.minebot.ai.command.AICommand;
import net.famzangl.minecraft.minebot.ai.command.AICommandInvocation;
import net.famzangl.minecraft.minebot.ai.command.AICommandParameter;
import net.famzangl.minecraft.minebot.ai.command.ParameterType;
import net.famzangl.minecraft.minebot.ai.command.SafeStrategyRule;
import net.famzangl.minecraft.minebot.ai.strategy.AIStrategy;
import net.famzangl.minecraft.minebot.ai.strategy.KillAnimalsStrategy;

@AICommand(helpText = "Starts hitting animals.\nA filter can be given.\nAvoids animals of your team.", name = "minebot")
public class CommandKill {

	@AICommandInvocation(safeRule = SafeStrategyRule.DEFEND)
	public static AIStrategy run(
			AIHelper helper,
			@AICommandParameter(type = ParameterType.FIXED, fixedName = "kill", description = "") String nameArg,
			@AICommandParameter(type = ParameterType.ENUM, description = "Animal type", optional = true) AnimalyType type,
			@AICommandParameter(type = ParameterType.COLOR, description = "Color", optional = true) Integer color,
			@AICommandParameter(type = ParameterType.NUMBER, description = "How many", optional = true) Integer count) {
		return new KillAnimalsStrategy(count == null ? -1 : count,
				type == null ? AnimalyType.ANY : type, color == null ? -1 : color);
	}
}
