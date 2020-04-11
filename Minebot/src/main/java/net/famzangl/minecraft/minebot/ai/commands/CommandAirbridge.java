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

@AICommand(helpText = "Builds an airbridge using the half-slabs in your inventory.", name = "minebot")
public class CommandAirbridge {
	public enum AirbridgeWidth {
		SMALL(0,0),
		WIDE(1,1),
		WIDER(2,2),
		MAXIMUM(3,3);
		
		private final int toLeft, toRight;

		private AirbridgeWidth(int toLeft, int toRight) {
			this.toLeft = toLeft;
			this.toRight = toRight;
		}
	}
	
	@AICommandInvocation(safeRule = SafeStrategyRule.DEFEND)
	public static AIStrategy run(
			AIHelper helper,
			@AICommandParameter(type = ParameterType.FIXED, fixedName = "airbridge", description = "") String nameArg,
			@AICommandParameter(type = ParameterType.ENUM, description = "direction: North, South, East, West", optional = true) EnumFacing inDirection,
			@AICommandParameter(type = ParameterType.NUMBER, description = "max distance to travel", optional = true) Integer length,
			@AICommandParameter(type = ParameterType.ENUM, description = "small, wide, wider, maximum", optional = true) AirbridgeWidth width) {
		if (width == null) {
			width = AirbridgeWidth.SMALL;
		}
		return new AirbridgeStrategy(helper.getPlayerPosition(),
				inDirection == null ? helper.getLookDirection() : inDirection,
				length == null ? -1 : length, width.toLeft, width.toRight);
	}

}
