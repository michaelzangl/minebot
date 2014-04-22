package net.famzangl.minecraft.minebot.build.reverse;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.AIStrategy;
import net.famzangl.minecraft.minebot.ai.command.AIChatController;
import net.famzangl.minecraft.minebot.ai.command.AICommand;
import net.famzangl.minecraft.minebot.ai.task.AITask;
import net.minecraft.command.ICommandSender;

public class CommandReverse implements AICommand {

	@Override
	public String getName() {
		return "build:reverse";
	}

	@Override
	public String getArgsUsage() {
		return "";
	}

	@Override
	public String getHelpText() {
		return "Get a build script for the selected area.";
	}

	@Override
	public AIStrategy evaluateCommand(ICommandSender sender, String[] args,
			AIHelper h, AIChatController aiChatController) {
		return new AIStrategy() {
			private boolean done = false;

			@Override
			public void searchTasks(AIHelper helper) {
				if (!done) {
					helper.addTask(new AITask() {
						private boolean done = false;

						@Override
						public void runTick(AIHelper h) {
							if (h.getPos1() == null || h.getPos2() == null) {
								AIChatController.addChatLine("Set positions first.");
							} else {
								new BuildReverser(h).run();
							}
							done = true;
						}

						@Override
						public boolean isFinished(AIHelper h) {
							return done;
						}
					});
					done = true;
				}
			}

			@Override
			public AITask getOverrideTask(AIHelper helper) {
				return null;
			}

			@Override
			public String getDescription() {
				return "Generating build tasks.";
			}
		};
	}

}
