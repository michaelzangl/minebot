package net.famzangl.minecraft.minebot.ai.command;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.AIStrategy;
import net.famzangl.minecraft.minebot.ai.animals.FishStrategy;
import net.famzangl.minecraft.minebot.ai.path.ClearAreaPathfinder;
import net.famzangl.minecraft.minebot.ai.strategy.PathFinderStrategy;
import net.minecraft.command.ICommandSender;

public class CommandClearArea implements AICommand {

	@Override
	public String getName() {
		return "clear";
	}

	@Override
	public String getArgsUsage() {
		return "";
	}

	@Override
	public String getHelpText() {
		return "Clear the whole area.";
	}

	@Override
	public AIStrategy evaluateCommand(ICommandSender sender, String[] args,
			AIHelper h, AIChatController aiChatController) {
		if (args.length != 1) {
			aiChatController.usage(this);
			return null;
		} else {
			return new PathFinderStrategy(new ClearAreaPathfinder(h), "Clear area");
		}
	}

}
