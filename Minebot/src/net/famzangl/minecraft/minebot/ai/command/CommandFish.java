package net.famzangl.minecraft.minebot.ai.command;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.AIStrategy;
import net.famzangl.minecraft.minebot.ai.animals.FishStrategy;
import net.minecraft.command.ICommandSender;

public class CommandFish implements AICommand {

	@Override
	public String getName() {
		return "fish";
	}

	@Override
	public String getArgsUsage() {
		return "";
	}

	@Override
	public String getHelpText() {
		return "Catch some fish.";
	}

	@Override
	public AIStrategy evaluateCommand(ICommandSender sender, String[] args,
			AIHelper h, AIChatController aiChatController) {
		if (args.length != 1) {
			aiChatController.usage(this);
			return null;
		} else {
			return new FishStrategy();
		}
	}

}
