package net.famzangl.minecraft.minebot.ai.command;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.AIStrategy;
import net.minecraft.command.ICommandSender;

final class Usage implements AICommand {
	@Override
	public String getName() {
		return "usage";
	}

	@Override
	public String getArgsUsage() {
		return "[command]";
	}

	@Override
	public String getHelpText() {
		return "Print the usage of the given command.";
	}

	@Override
	public AIStrategy evaluateCommand(ICommandSender sender, String[] args,
			AIHelper h, AIChatController aiChatController) {
		if (args.length == 1) {
			aiChatController.usage();
		} else if (args.length == 2) {
			AICommand command = aiChatController.getCommand(args[1]);
			if (command == null) {
				AIChatController.addChatLine("Command could not be found: " + args[1]);
			} else {
				aiChatController.usage(command);
			}
		} else {
			aiChatController.usage(this);
		}
		return null;
	}
}