package net.famzangl.minecraft.minebot.build;

import net.famzangl.minecraft.minebot.Pos;
import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.AIStrategy;
import net.famzangl.minecraft.minebot.ai.command.AIChatController;
import net.famzangl.minecraft.minebot.ai.command.AICommand;
import net.famzangl.minecraft.minebot.build.blockbuild.BuildTask;
import net.minecraft.block.Block;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;

public class CommandScheduleBuild implements AICommand {

	@Override
	public String getName() {
		return "build:schedule";
	}

	@Override
	public String getArgsUsage() {
		return "x y z blocktype [...]";
	}

	@Override
	public String getHelpText() {
		return "Adds a block to the list of blocks to be placed.";
	}

	@Override
	public AIStrategy evaluateCommand(ICommandSender sender, String[] args,
			AIHelper h, AIChatController aiChatController) {
		if (args.length < 5) {
			aiChatController.usage(this);
		} else {
			Pos forPosition = AIChatController.parsePos(sender, args, 1);
			Block blockToPlace = CommandBase.getBlockByText(sender,
					args[4]);
			if (blockToPlace == null || forPosition == null) {
				aiChatController.usage(this);
			} else {
				BuildTask task = BuildTask.taskFor(forPosition, blockToPlace,
						args.length >= 6 ? args[5] : "",
						args.length >= 7 ? args[6] : "");
				h.buildManager.addTask(task);
			}
		}
		return null;
	}

}
