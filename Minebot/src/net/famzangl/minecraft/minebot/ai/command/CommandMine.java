package net.famzangl.minecraft.minebot.ai.command;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.AIStrategy;
import net.famzangl.minecraft.minebot.ai.strategy.MineStrategy;
import net.minecraft.command.ICommandSender;

public class CommandMine implements AICommand {

	@Override
	public String getName() {
		return "mine";
	}

	@Override
	public String getArgsUsage() {
		return "[blockName]";
	}

	@Override
	public String getHelpText() {
		return "Mines for ores.\n"
				+ "Uses the minebot.properties file to find ores."
				+ "If blockName is given, only the block that is given is searched for.";
	}

	@Override
	public AIStrategy evaluateCommand(ICommandSender sender, String[] args,
			AIHelper h, AIChatController aiChatController) {
		if (args.length == 1) {
			return new MineStrategy().produceStrategy(h);
		} else if (args.length == 2) {
			return new MineStrategy().produceStrategy(h, args[1]);
		} else {
			aiChatController.usage(this);
			return null;
		}
	}

}
