package net.famzangl.minecraft.minebot.ai.command;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.AIStrategy;
import net.famzangl.minecraft.minebot.ai.task.AITask;
import net.famzangl.minecraft.minebot.ai.task.SendCommandTask;
import net.minecraft.command.ICommandSender;

public class CommandRun implements AICommand {

	@Override
	public String getName() {
		return "run";
	}

	@Override
	public String getArgsUsage() {
		return "file";
	}

	@Override
	public String getHelpText() {
		return "Run all minecraft commands in the file as if they were typed in the chat.";
	}

	@Override
	public AIStrategy evaluateCommand(ICommandSender sender,
			final String[] args, AIHelper h, AIChatController aiChatController) {
		if (args.length != 2) {
			aiChatController.usage(this);
			return null;
		} else {
			return new AIStrategy() {

				private boolean added;

				@Override
				public void searchTasks(AIHelper helper) {
					if (!added) {
						added = true;
						File file = new File(args[1]);
						try {
							ArrayList<String> commands = new ArrayList<String>();
							Scanner reader = new Scanner(file);
							String line;
							while (reader.hasNextLine()) {
								line = reader.nextLine();
								if (!line.startsWith("#") && !line.isEmpty()) {
									commands.add(line);
								}
							}
							reader.close();
							helper.addTask(new SendCommandTask(commands));
						} catch (IOException e) {
							AIChatController
									.addChatLine("Error while reading file: "
											+ e.getMessage());
							e.printStackTrace();
						}
					}
				}

				@Override
				public AITask getOverrideTask(AIHelper helper) {
					return null;
				}

				@Override
				public String getDescription() {
					return "Loading";
				}
			};
		}
	}

}
