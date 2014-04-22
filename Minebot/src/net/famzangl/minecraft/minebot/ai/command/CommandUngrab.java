package net.famzangl.minecraft.minebot.ai.command;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.AIStrategy;
import net.minecraft.command.ICommandSender;

public class CommandUngrab implements AICommand {

	@Override
	public String getName() {
		return "ungrab";
	}

	@Override
	public String getArgsUsage() {
		return "";
	}

	@Override
	public String getHelpText() {
		return "Ungrabs your mouse\n" +
				"Like pressing ESC but the game is still running.";
	}

	@Override
	public AIStrategy evaluateCommand(ICommandSender sender, String[] args,
			AIHelper h, AIChatController aiChatController) {
		h.ungrab();
		return null;
	}

}
