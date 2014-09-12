package net.famzangl.minecraft.minebot.ai.commands;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.command.AICommand;
import net.famzangl.minecraft.minebot.ai.command.AICommandInvocation;
import net.famzangl.minecraft.minebot.ai.command.AICommandParameter;
import net.famzangl.minecraft.minebot.ai.command.ParameterType;
import net.famzangl.minecraft.minebot.ai.strategy.AIStrategy;
import net.famzangl.minecraft.minebot.ai.strategy.TintStrategy;
import net.famzangl.minecraft.minebot.ai.strategy.TintStrategy.TintType;
import net.famzangl.minecraft.minebot.ai.strategy.ValueActionStrategy;

@AICommand(helpText = "Tints wolves and sheep with the given color", name = "minebot")
public class CommandTint {

	@AICommandInvocation()
	public static AIStrategy run(
			AIHelper helper,
			@AICommandParameter(type = ParameterType.FIXED, fixedName = "tint", description = "") String nameArg) {
		return run(helper, nameArg, -1, TintType.ANY);
	}

	@AICommandInvocation()
	public static AIStrategy run(
			AIHelper helper,
			@AICommandParameter(type = ParameterType.FIXED, fixedName = "tint", description = "") String nameArg,
			@AICommandParameter(type = ParameterType.COLOR, description = "The color to use") int color) {
		return run(helper, nameArg, color, TintType.ANY);
	}

	@AICommandInvocation()
	public static AIStrategy run(
			AIHelper helper,
			@AICommandParameter(type = ParameterType.FIXED, fixedName = "tint", description = "") String nameArg,
			@AICommandParameter(type = ParameterType.COLOR, description = "The color to use") int color,
			@AICommandParameter(type = ParameterType.ENUM, description = "Animals to apply the tint to") TintType type) {
		return run(helper, nameArg, color, type, -1);
	}

	@AICommandInvocation()
	public static AIStrategy run(
			AIHelper helper,
			@AICommandParameter(type = ParameterType.FIXED, fixedName = "tint", description = "") String nameArg,
			@AICommandParameter(type = ParameterType.COLOR, description = "The color to use") int color,
			@AICommandParameter(type = ParameterType.ENUM, description = "Animals to apply the tint to") TintType type,
			@AICommandParameter(type = ParameterType.COLOR, description = "The color the wolf should currently have") int current) {
		return ValueActionStrategy.makeSafe(new TintStrategy(color, type,
				current), false);
	}
}
