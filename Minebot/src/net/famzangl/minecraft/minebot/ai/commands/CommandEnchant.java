package net.famzangl.minecraft.minebot.ai.commands;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.command.AICommand;
import net.famzangl.minecraft.minebot.ai.command.AICommandInvocation;
import net.famzangl.minecraft.minebot.ai.command.AICommandParameter;
import net.famzangl.minecraft.minebot.ai.command.ParameterType;
import net.famzangl.minecraft.minebot.ai.command.SafeStrategyRule;
import net.famzangl.minecraft.minebot.ai.enchanting.EnchantStrategy;
import net.famzangl.minecraft.minebot.ai.strategy.AIStrategy;

@AICommand(helpText = "Kill mobs in range and enchant whatever you have in the inventory.", name = "minebot")
public class CommandEnchant {

	@AICommandInvocation(safeRule = SafeStrategyRule.DEFEND)
	public static AIStrategy run(
			AIHelper helper,
			@AICommandParameter(type = ParameterType.FIXED, fixedName = "enchant", description = "") String nameArg) {
		return new EnchantStrategy();
	}

	@AICommandInvocation(safeRule = SafeStrategyRule.DEFEND)
	public static AIStrategy run(
			AIHelper helper,
			@AICommandParameter(type = ParameterType.FIXED, fixedName = "enchant", description = "") String nameArg,
			@AICommandParameter(type = ParameterType.NUMBER, description = "") int level) {
		return new EnchantStrategy(level);
	}

}
