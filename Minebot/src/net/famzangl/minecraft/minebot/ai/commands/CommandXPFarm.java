package net.famzangl.minecraft.minebot.ai.commands;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.command.AICommand;
import net.famzangl.minecraft.minebot.ai.command.AICommandInvocation;
import net.famzangl.minecraft.minebot.ai.command.AICommandParameter;
import net.famzangl.minecraft.minebot.ai.command.ParameterType;
import net.famzangl.minecraft.minebot.ai.command.SafeStrategyRule;
import net.famzangl.minecraft.minebot.ai.enchanting.XPFarmStrategy;
import net.famzangl.minecraft.minebot.ai.strategy.AIStrategy;

@AICommand(helpText = "Kill mobs in range and (optionally) enchant whatever you have in the inventory.", name = "minebot")
public class CommandXPFarm {

	@AICommandInvocation(safeRule = SafeStrategyRule.DEFEND)
	public static AIStrategy run(
			AIHelper helper,
			@AICommandParameter(type = ParameterType.FIXED, fixedName = "xpfarm", description = "") String nameArg,
			@AICommandParameter(type = ParameterType.NUMBER, description = "Maximum level") Integer level) {
		return new XPFarmStrategy(false, level == null ? 40 : level);
	}

	@AICommandInvocation(safeRule = SafeStrategyRule.DEFEND)
	public static AIStrategy run(
			AIHelper helper,
			@AICommandParameter(type = ParameterType.FIXED, fixedName = "xpfarm", description = "") String nameArg,
			@AICommandParameter(type = ParameterType.FIXED, fixedName = "autoenchant", description = "") String nameArg2,
			@AICommandParameter(type = ParameterType.NUMBER, description = "Maximum level") Integer level) {
		return new XPFarmStrategy(true, level == null ? 30 : level);
	}

}
