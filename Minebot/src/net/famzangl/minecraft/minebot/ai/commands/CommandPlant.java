package net.famzangl.minecraft.minebot.ai.commands;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.AIStrategy;
import net.famzangl.minecraft.minebot.ai.command.AICommand;
import net.famzangl.minecraft.minebot.ai.command.AICommandInvocation;
import net.famzangl.minecraft.minebot.ai.command.AICommandParameter;
import net.famzangl.minecraft.minebot.ai.command.ParameterType;
import net.famzangl.minecraft.minebot.ai.path.PlantPathFinder;
import net.famzangl.minecraft.minebot.ai.path.PlantPathFinder.PlantType;
import net.famzangl.minecraft.minebot.ai.strategy.PathFinderStrategy;

@AICommand(helpText = "Plants plants\n"
		+ "Uses a hoe if needed.", name = "minebot")
public class CommandPlant {
	@AICommandInvocation()
	public static AIStrategy run(
			AIHelper helper,
			@AICommandParameter(type = ParameterType.FIXED, fixedName = "plant", description = "") String nameArg) {
		return new PathFinderStrategy(new PlantPathFinder(helper, PlantType.ANY), "Planting");
	}
	
	@AICommandInvocation()
	public static AIStrategy run(
			AIHelper helper,
			@AICommandParameter(type = ParameterType.FIXED, fixedName = "plant", description = "") String nameArg,
			@AICommandParameter(type = ParameterType.ENUM, description = "plant type") PlantType type) {
		return new PathFinderStrategy(new PlantPathFinder(helper, type), "Planting");
	}
}
