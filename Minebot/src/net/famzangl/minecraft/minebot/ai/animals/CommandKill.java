package net.famzangl.minecraft.minebot.ai.animals;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.AIStrategy;
import net.famzangl.minecraft.minebot.ai.command.AIChatController;
import net.famzangl.minecraft.minebot.ai.command.AICommand;
import net.minecraft.command.ICommandSender;

public class CommandKill implements AICommand {

	@Override
	public String getName() {
		return "kill";
	}

	@Override
	public String getArgsUsage() {
		return "[type]";
	}

	@Override
	public String getHelpText() {
		return "Kill animals in range and collect loot.\n"
				+ "type can be any,cow,chicken, ...\n"
				+ "This ignores your own wolfes."
				+ "This ignores non-adult animals.";
	}

	@Override
	public AIStrategy evaluateCommand(ICommandSender sender, String[] args,
			AIHelper h, AIChatController aiChatController) {
		if (args.length == 1) {
			return new KillAnimalsStrategy();
		} else if (args.length == 2) {
			AnimalyType type = null;
			for (AnimalyType v : AnimalyType.values()) {
				if (v.toString().equalsIgnoreCase(args[1])) {
					type = v;
				}
			}
			if (type != null) {
				return new KillAnimalsStrategy(-1, type);
			}
		}
		aiChatController.usage(this);
		return null;
	}

}
