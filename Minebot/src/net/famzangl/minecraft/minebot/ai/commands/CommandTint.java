package net.famzangl.minecraft.minebot.ai.commands;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.command.AICommand;
import net.famzangl.minecraft.minebot.ai.command.AICommandInvocation;
import net.famzangl.minecraft.minebot.ai.command.AICommandParameter;
import net.famzangl.minecraft.minebot.ai.command.ParameterType;
import net.famzangl.minecraft.minebot.ai.command.SafeStrategyRule;
import net.famzangl.minecraft.minebot.ai.strategy.AIStrategy;
import net.famzangl.minecraft.minebot.ai.strategy.TintStrategy;
import net.famzangl.minecraft.minebot.ai.strategy.TintStrategy.TintType;
import net.minecraft.item.EnumDyeColor;

@AICommand(helpText = "Tints wolves and sheep with the given color", name = "minebot")
public class CommandTint {

	@AICommandInvocation(safeRule = SafeStrategyRule.DEFEND)
	public static AIStrategy run(
			AIHelper helper,
			@AICommandParameter(type = ParameterType.FIXED, fixedName = "tint", description = "") String nameArg) {
		return run(helper, nameArg, null, TintType.ANY);
	}

	@AICommandInvocation(safeRule = SafeStrategyRule.DEFEND)
	public static AIStrategy run(
			AIHelper helper,
			@AICommandParameter(type = ParameterType.FIXED, fixedName = "tint", description = "") String nameArg,
			@AICommandParameter(type = ParameterType.COLOR, description = "The color to use") EnumDyeColor color,
			@AICommandParameter(type = ParameterType.ENUM, description = "Animals to apply the tint to", optional = true) TintType type) {
		return run(helper, nameArg, color, type, null);
	}

	@AICommandInvocation(safeRule = SafeStrategyRule.DEFEND)
	public static AIStrategy run(
			AIHelper helper,
			@AICommandParameter(type = ParameterType.FIXED, fixedName = "tint", description = "") String nameArg,
			@AICommandParameter(type = ParameterType.COLOR, description = "The color to use") EnumDyeColor color,
			@AICommandParameter(type = ParameterType.ENUM, description = "Animals to apply the tint to") TintType type,
			@AICommandParameter(type = ParameterType.COLOR, description = "The color the wolf should currently have") EnumDyeColor current) {
		return new TintStrategy(color, type, current);
	}
}
