package net.famzangl.minecraft.minebot.ai.commands;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.command.AICommand;
import net.famzangl.minecraft.minebot.ai.command.AICommandInvocation;
import net.famzangl.minecraft.minebot.ai.command.AICommandParameter;
import net.famzangl.minecraft.minebot.ai.command.ParameterType;
import net.famzangl.minecraft.minebot.ai.command.SafeStrategyRule;
import net.famzangl.minecraft.minebot.ai.strategy.AIStrategy;
import net.famzangl.minecraft.minebot.ai.strategy.AirbridgeStrategy;
import net.minecraft.util.EnumFacing;

@AICommand(helpText = "Build a tunnel with the given profile", name = "minebot")
public class CommandAirbridge {
	@AICommandInvocation(safeRule = SafeStrategyRule.DEFEND)
	public static AIStrategy run(
			AIHelper helper,
			@AICommandParameter(type = ParameterType.FIXED, fixedName = "airbridge", description = "") String nameArg,
			@AICommandParameter(type = ParameterType.ENUM, description = "direction", optional = true) EnumFacing inDirection,
			@AICommandParameter(type = ParameterType.NUMBER, description = "max length", optional = true) Integer length) {
		return new AirbridgeStrategy(helper.getPlayerPosition(),
				inDirection == null ? helper.getLookDirection() : inDirection,
				length == null ? -1 : length);
	}

}
