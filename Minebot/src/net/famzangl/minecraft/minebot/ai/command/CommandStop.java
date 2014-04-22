package net.famzangl.minecraft.minebot.ai.command;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.AIStrategy;
import net.famzangl.minecraft.minebot.ai.task.AITask;
import net.minecraft.command.ICommandSender;

public class CommandStop implements AICommand {

	@Override
	public String getName() {
		return "stop";
	}

	@Override
	public String getArgsUsage() {
		return "";
	}

	@Override
	public String getHelpText() {
		return "Stop whatever you are doing.";
	}

	@Override
	public AIStrategy evaluateCommand(ICommandSender sender, String[] args,
			AIHelper h, AIChatController aiChatController) {
		return new AIStrategy() {
			@Override
			public void searchTasks(AIHelper helper) {
			}
			
			@Override
			public AITask getOverrideTask(AIHelper helper) {
				return null;
			}
			
			@Override
			public String getDescription() {
				return "Stopping";
			}
		};
	}

}
