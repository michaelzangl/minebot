package net.famzangl.minecraft.minebot.build;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.AIStrategy;
import net.famzangl.minecraft.minebot.ai.command.AIChatController;
import net.famzangl.minecraft.minebot.ai.command.AICommand;
import net.famzangl.minecraft.minebot.ai.task.AITask;
import net.minecraft.command.ICommandSender;

public class CommandStepNext implements AICommand {

	@Override
	public String getName() {
		return "build:next";
	}

	@Override
	public String getArgsUsage() {
		return "";
	}

	@Override
	public String getHelpText() {
		return "Use next command in queue";
	}

	@Override
	public AIStrategy evaluateCommand(ICommandSender sender, String[] args,
			AIHelper h, AIChatController aiChatController) {
		return new AIStrategy() {
			private boolean done;

			@Override
			public void searchTasks(AIHelper helper) {
				if (!done) {
					helper.addTask(new NextTaskTask());
					done = true;
				}
			}
			
			@Override
			public AITask getOverrideTask(AIHelper helper) {
				return null;
			}
			
			@Override
			public String getDescription() {
				return null;
			}
		};
	}
}
