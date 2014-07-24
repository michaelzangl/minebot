package net.famzangl.minecraft.minebot.ai.commands;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.AIStrategy;
import net.famzangl.minecraft.minebot.ai.command.AICommand;
import net.famzangl.minecraft.minebot.ai.command.AICommandInvocation;
import net.famzangl.minecraft.minebot.ai.command.AICommandParameter;
import net.famzangl.minecraft.minebot.ai.command.ParameterType;

@AICommand(helpText = "Ungrabs the mouse cursor\n" +
		"The bot can then still run while you do other things on your computer.\n" +
		"Just click in the window to re-grab the mouse.", name = "minebot")
public class CommandUngrab {

	@AICommandInvocation()
	public static AIStrategy run(
			AIHelper helper,
			@AICommandParameter(type = ParameterType.FIXED, fixedName = "ungrab", description = "") String nameArg) {
		helper.ungrab();
		return null;
	}

}
