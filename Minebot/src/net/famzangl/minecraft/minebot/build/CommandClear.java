package net.famzangl.minecraft.minebot.build;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.AIStrategy;
import net.famzangl.minecraft.minebot.ai.command.AIChatController;
import net.famzangl.minecraft.minebot.ai.command.AICommand;
import net.famzangl.minecraft.minebot.ai.task.AITask;
import net.minecraft.command.ICommandSender;

public class CommandClear implements AICommand {

	@Override
	public String getName() {
		return "build:clear";
	}

	@Override
	public String getArgsUsage() {
		return "";
	}

	@Override
	public String getHelpText() {
		return "Clear all scheduled builds";
	}

	@Override
	public AIStrategy evaluateCommand(ICommandSender sender, String[] args,
			AIHelper h, AIChatController aiChatController) {
		return new AIStrategy() {
			@Override
			public void searchTasks(AIHelper helper) {
				if (helper.buildManager.peekNextTask() != null) {
					helper.addTask(new NextTaskTask(10));
				}
			}
			
			@Override
			public AITask getOverrideTask(AIHelper helper) {
				return null;
			}
			
			@Override
			public String getDescription() {
				return "Clearing build list.";
			}
		};
	}

}
