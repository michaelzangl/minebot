package net.famzangl.minecraft.minebot.ai.command;

import net.famzangl.minecraft.minebot.Pos;
import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.AIStrategy;
import net.minecraft.command.ICommandSender;

public class CommandSetPos implements AICommand {

	private boolean isPos2;

	public CommandSetPos(boolean pos2) {
		this.isPos2 = pos2;
	}

	@Override
	public String getName() {
		return isPos2 ? "pos2" : "pos1";
	}

	@Override
	public String getArgsUsage() {
		return "[x y z]";
	}

	@Override
	public String getHelpText() {
		return "Set " + getName() + " marker to the given or current Position.";
	}

	@Override
	public AIStrategy evaluateCommand(ICommandSender sender, String[] args,
			AIHelper h, AIChatController aiChatController) {
		Pos pos;
		if (args.length == 1) {
			pos = h.getPlayerPosition();
		} else if (args.length == 4) {
			pos = AIChatController.parsePos(sender, args, 1);
		} else {
			aiChatController.usage(this);
			return null;
		}
		h.setPosition(pos, isPos2);
		return null;
	}

}
