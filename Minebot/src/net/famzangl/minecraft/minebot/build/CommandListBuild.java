package net.famzangl.minecraft.minebot.build;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.AIStrategy;
import net.famzangl.minecraft.minebot.ai.command.AIChatController;
import net.famzangl.minecraft.minebot.ai.command.AICommand;
import net.famzangl.minecraft.minebot.build.blockbuild.BuildTask;
import net.minecraft.command.ICommandSender;

/**
 * List all scheduled
 * @author michael
 *
 */
public class CommandListBuild implements AICommand{

	@Override
	public String getName() {
		return "build:list";
	}

	@Override
	public String getArgsUsage() {
		return "";
	}

	@Override
	public String getHelpText() {
		return "List scheduled builds";
	}

	@Override
	public AIStrategy evaluateCommand(ICommandSender sender, String[] args,
			AIHelper h, AIChatController aiChatController) {
		Iterable<BuildTask> list = h.buildManager.getScheduled();
		int i = 0;
		for (BuildTask task : list) {
			AIChatController.addChatLine("Slot " + (++i) + ": " + task);
		}
		AIChatController.addChatLine("Total: " + (i) + " slots.");
		return null;
	}

}
