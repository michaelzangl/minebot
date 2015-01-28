package net.famzangl.minecraft.minebot.build.commands;

import net.famzangl.minecraft.minebot.Pos;
import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.command.AICommand;
import net.famzangl.minecraft.minebot.ai.command.AICommandInvocation;
import net.famzangl.minecraft.minebot.ai.command.AICommandParameter;
import net.famzangl.minecraft.minebot.ai.command.ParameterType;
import net.famzangl.minecraft.minebot.ai.strategy.AIStrategy;
import net.minecraft.util.BlockPos;

@AICommand(helpText = "Set bounding position manually.", name = "minebuild")
public class CommandSetPos {

	@AICommandInvocation()
	public static AIStrategy run(
			AIHelper helper,
			@AICommandParameter(type = ParameterType.FIXED, fixedName = "pos1", description = "") String nameArg) {
		helper.setPosition(helper.getPlayerPosition(), false);
		return null;
	}

	@AICommandInvocation()
	public static AIStrategy run(
			@AICommandParameter(type = ParameterType.FIXED, fixedName = "pos2", description = "") String nameArg,
			AIHelper helper) {
		helper.setPosition(helper.getPlayerPosition(), true);
		return null;
	}

	@AICommandInvocation()
	public static AIStrategy run(
			AIHelper helper,
			@AICommandParameter(type = ParameterType.FIXED, fixedName = "pos1", description = "") String nameArg,
			@AICommandParameter(type = ParameterType.POSITION, description = "The position") BlockPos pos) {
		helper.setPosition(pos, false);
		return null;
	}

	@AICommandInvocation()
	public static AIStrategy run(
			@AICommandParameter(type = ParameterType.FIXED, fixedName = "pos2", description = "") String nameArg,
			AIHelper helper,
			@AICommandParameter(type = ParameterType.POSITION, description = "The position") Pos pos) {
		helper.setPosition(pos, true);
		return null;
	}
}
