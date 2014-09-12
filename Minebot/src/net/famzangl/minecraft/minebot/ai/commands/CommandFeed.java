package net.famzangl.minecraft.minebot.ai.commands;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.command.AICommand;
import net.famzangl.minecraft.minebot.ai.command.AICommandInvocation;
import net.famzangl.minecraft.minebot.ai.command.AICommandParameter;
import net.famzangl.minecraft.minebot.ai.command.ParameterType;
import net.famzangl.minecraft.minebot.ai.strategy.AIStrategy;
import net.famzangl.minecraft.minebot.ai.strategy.FeedAnimalsStrategy;
import net.famzangl.minecraft.minebot.ai.strategy.ValueActionStrategy;

@AICommand(helpText = "Feeds animals in range\n"
		+ "The animals can be restricted by color.", name = "minebot")
public class CommandFeed {

	@AICommandInvocation()
	public static AIStrategy run(
			AIHelper helper,
			@AICommandParameter(type = ParameterType.FIXED, fixedName = "feed", description = "") String nameArg) {
		return ValueActionStrategy.makeSafe(new FeedAnimalsStrategy(), false);
	}

	@AICommandInvocation()
	public static AIStrategy run(
			AIHelper helper,
			@AICommandParameter(type = ParameterType.FIXED, fixedName = "feed", description = "") String nameArg,
			@AICommandParameter(type = ParameterType.COLOR, description = "The color of wolfes to feed.") int color) {
		return ValueActionStrategy.makeSafe(new FeedAnimalsStrategy(color), false);
	}
}
