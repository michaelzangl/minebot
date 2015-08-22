package net.famzangl.minecraft.minebot.ai.commands;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.command.AICommand;
import net.famzangl.minecraft.minebot.ai.command.AICommandInvocation;
import net.famzangl.minecraft.minebot.ai.command.AICommandParameter;
import net.famzangl.minecraft.minebot.ai.command.ParameterType;
import net.famzangl.minecraft.minebot.ai.command.SafeStrategyRule;
import net.famzangl.minecraft.minebot.ai.strategy.AIStrategy;
import net.famzangl.minecraft.minebot.ai.strategy.WalkTowardsStrategy;

@AICommand(helpText = "Walk to a given x/z point", name = "minebot")
public class CommandWalk {
	@AICommandInvocation(safeRule = SafeStrategyRule.DEFEND)
	public static AIStrategy run(
			AIHelper helper,
			@AICommandParameter(type = ParameterType.FIXED, fixedName = "walk", description = "") String nameArg,
			@AICommandParameter(type = ParameterType.DOUBLE, description = "x") Double x,
			@AICommandParameter(type = ParameterType.DOUBLE, description = "z") Double z) {
		return new WalkTowardsStrategy(x, z);
	}

}
