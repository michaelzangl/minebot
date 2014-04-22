package net.famzangl.minecraft.minebot.ai.command;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.AIStrategy;
import net.minecraft.command.ICommandSender;

final class Help implements AICommand {
	@Override
	public String getName() {
		return "help";
	}

	@Override
	public String getArgsUsage() {
		return "command";
	}

	@Override
	public String getHelpText() {
		return "Gives you help to a given command";
	}

	@Override
	public AIStrategy evaluateCommand(ICommandSender sender, String[] args,
			AIHelper h, AIChatController aiChatController) {
		if (args.length == 2) {
			AICommand command = aiChatController.getCommand(args[1]);
			if (command == null) {
				AIChatController.addChatLine("Command could not be found: "
						+ args[1]);
			} else {
				aiChatController.usage(command);
				String[] help = command.getHelpText().split("\n");
				for (String text : help) {
					AIChatController.addChatLine(text);
				}
			}
		} else {
			aiChatController.usage(this);
		}
		return null;
	}
}