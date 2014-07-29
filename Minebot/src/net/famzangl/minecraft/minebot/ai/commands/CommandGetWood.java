package net.famzangl.minecraft.minebot.ai.commands;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.AIStrategy;
import net.famzangl.minecraft.minebot.ai.command.AICommand;
import net.famzangl.minecraft.minebot.ai.command.AICommandInvocation;
import net.famzangl.minecraft.minebot.ai.command.AICommandParameter;
import net.famzangl.minecraft.minebot.ai.command.ParameterType;
import net.famzangl.minecraft.minebot.ai.path.TreePathFinder;
import net.famzangl.minecraft.minebot.ai.strategy.PathFinderStrategy;
import net.famzangl.minecraft.minebot.build.WoodType;

@AICommand(helpText = "Gets wood", name = "minebot")
public class CommandGetWood {
	@AICommandInvocation()
	public static AIStrategy run(
			AIHelper helper,
			@AICommandParameter(type = ParameterType.FIXED, fixedName = "lumberjack", description = "") String nameArg) {
		return new PathFinderStrategy(new TreePathFinder(helper, null),
				"Getting some wood");
	}

	@AICommandInvocation()
	public static AIStrategy run(
			AIHelper helper,
			@AICommandParameter(type = ParameterType.FIXED, fixedName = "lumberjack", description = "") String nameArg,
			@AICommandParameter(type = ParameterType.ENUM, description = "wood type") WoodType type) {
		return new PathFinderStrategy(new TreePathFinder(helper, type),
				"Getting some wood");
	}

}
