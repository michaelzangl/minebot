package net.famzangl.minecraft.minebot.ai.animals;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.AIStrategy;
import net.famzangl.minecraft.minebot.ai.command.AIChatController;
import net.famzangl.minecraft.minebot.ai.command.AICommand;
import net.minecraft.command.ICommandSender;

public class CommandFeed implements AICommand {

	@Override
	public String getName() {
		return "feed";
	}

	@Override
	public String getArgsUsage() {
		return "";
	}

	@Override
	public String getHelpText() {
		return "Feed the current item to all animals in range.\n" +
				"Only uses the item you are holding in your hand.\n" +
				"Collects XP-Orbs automatically";
	}

	@Override
	public AIStrategy evaluateCommand(ICommandSender sender, String[] args,
			AIHelper h, AIChatController aiChatController) {
		if (args.length == 1)
			return new FeedAnimalsStrategy();
		else {
			aiChatController.usage(this);
			return null;
		}
	}

}
