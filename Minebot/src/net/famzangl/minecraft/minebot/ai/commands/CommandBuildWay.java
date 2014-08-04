package net.famzangl.minecraft.minebot.ai.commands;

import net.famzangl.minecraft.minebot.Pos;
import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.AIStrategy;
import net.famzangl.minecraft.minebot.ai.command.AICommand;
import net.famzangl.minecraft.minebot.ai.command.AICommandInvocation;
import net.famzangl.minecraft.minebot.ai.command.AICommandParameter;
import net.famzangl.minecraft.minebot.ai.command.ParameterType;
import net.famzangl.minecraft.minebot.ai.strategy.BuildWayStrategy;
import net.minecraftforge.common.util.ForgeDirection;

@AICommand(helpText = "Build a nice way", name = "minebot")
public class CommandBuildWay {

	@AICommandInvocation()
	public static AIStrategy run(
			AIHelper helper,
			@AICommandParameter(type = ParameterType.FIXED, fixedName = "way", description = "") String nameArg) {
		ForgeDirection dir = helper.getLookDirection();
		Pos pos = helper.getPlayerPosition();
		
		return new BuildWayStrategy(dir, pos);
	}
}
