package net.famzangl.minecraft.minebot.ai.commands;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.command.AICommand;
import net.famzangl.minecraft.minebot.ai.command.AICommandInvocation;
import net.famzangl.minecraft.minebot.ai.command.AICommandParameter;
import net.famzangl.minecraft.minebot.ai.command.ParameterType;
import net.famzangl.minecraft.minebot.ai.enchanting.EnchantStrategy;
import net.famzangl.minecraft.minebot.ai.strategy.AIStrategy;
import net.famzangl.minecraft.minebot.ai.strategy.ValueActionStrategy;

@AICommand(helpText = "Kill mobs in range and enchant whatever you have in the inventory.", name = "minebot")
public class CommandEnchant {

	@AICommandInvocation()
	public static AIStrategy run(
			AIHelper helper,
			@AICommandParameter(type = ParameterType.FIXED, fixedName = "enchant", description = "") String nameArg) {
		return ValueActionStrategy.makeSafe(new EnchantStrategy());
	}

	@AICommandInvocation()
	public static AIStrategy run(
			AIHelper helper,
			@AICommandParameter(type = ParameterType.FIXED, fixedName = "enchant", description = "") String nameArg,
			@AICommandParameter(type = ParameterType.NUMBER, description = "") int level) {
		return ValueActionStrategy.makeSafe(new EnchantStrategy(level));
	}

}
