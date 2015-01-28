package net.famzangl.minecraft.minebot.ai.commands;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.command.AICommand;
import net.famzangl.minecraft.minebot.ai.command.AICommandInvocation;
import net.famzangl.minecraft.minebot.ai.command.AICommandParameter;
import net.famzangl.minecraft.minebot.ai.command.ParameterType;
import net.famzangl.minecraft.minebot.ai.command.SafeStrategyRule;
import net.famzangl.minecraft.minebot.ai.path.TreePathFinder;
import net.famzangl.minecraft.minebot.ai.strategy.AIStrategy;
import net.famzangl.minecraft.minebot.ai.strategy.PathFinderStrategy;
import net.famzangl.minecraft.minebot.build.block.WoodType;

@AICommand(helpText = "Gets wood", name = "minebot")
public class CommandGetWood {
	@AICommandInvocation(safeRule = SafeStrategyRule.DEFEND)
	public static AIStrategy run(
			AIHelper helper,
			@AICommandParameter(type = ParameterType.FIXED, fixedName = "lumberjack", description = "") String nameArg,
			@AICommandParameter(type = ParameterType.ENUM, description = "wood type", optional = true) WoodType type,
			@AICommandParameter(type = ParameterType.FIXED, fixedName = "replant", description = "", optional = true) String replant) {
		return new PathFinderStrategy(
				new TreePathFinder(type, replant != null), "Getting some " + (type == null ? "wood" : type.toString().toLowerCase()));
	}

}
