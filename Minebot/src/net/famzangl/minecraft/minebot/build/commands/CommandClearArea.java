package net.famzangl.minecraft.minebot.build.commands;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.AIStrategy;
import net.famzangl.minecraft.minebot.ai.command.AICommand;
import net.famzangl.minecraft.minebot.ai.command.AICommandInvocation;
import net.famzangl.minecraft.minebot.ai.command.AICommandParameter;
import net.famzangl.minecraft.minebot.ai.command.ParameterType;
import net.famzangl.minecraft.minebot.ai.path.ClearAreaPathfinder;
import net.famzangl.minecraft.minebot.ai.strategy.PathFinderStrategy;

@AICommand(helpText = "Clears the selected area.", name = "minebuild")
public class CommandClearArea {

	@AICommandInvocation()
	public static AIStrategy run(
			AIHelper helper,
			@AICommandParameter(type = ParameterType.FIXED, fixedName = "clear", description = "") String nameArg) {
		return new PathFinderStrategy(new ClearAreaPathfinder(helper),
				"Clear area");
	}
}
