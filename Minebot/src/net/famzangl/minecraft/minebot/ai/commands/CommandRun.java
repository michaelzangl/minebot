package net.famzangl.minecraft.minebot.ai.commands;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.AIStrategy;
import net.famzangl.minecraft.minebot.ai.command.AIChatController;
import net.famzangl.minecraft.minebot.ai.command.AICommand;
import net.famzangl.minecraft.minebot.ai.command.AICommandInvocation;
import net.famzangl.minecraft.minebot.ai.command.AICommandParameter;
import net.famzangl.minecraft.minebot.ai.command.ParameterType;
import net.famzangl.minecraft.minebot.ai.task.AITask;
import net.famzangl.minecraft.minebot.ai.task.SendCommandTask;

@AICommand(helpText = "Run commands from a file.", name = "minebot")
public class CommandRun {

	private static final class RundFileStrategy implements AIStrategy {
		private boolean added;
		private final String fileName;

		@Override
		public void searchTasks(AIHelper helper) {
			if (!added) {
				added = true;
				final File file = new File(fileName);
				try {
					final ArrayList<String> commands = new ArrayList<String>();
					final Scanner reader = new Scanner(file);
					String line;
					while (reader.hasNextLine()) {
						line = reader.nextLine();
						if (!line.startsWith("#") && !line.isEmpty()) {
							commands.add(line);
						}
					}
					reader.close();
					helper.addTask(new SendCommandTask(commands));
				} catch (final IOException e) {
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

		public RundFileStrategy(String fileName) {
			super();
			this.fileName = fileName;
		}
	}

	@AICommandInvocation()
	public static AIStrategy run(
			AIHelper helper,
			@AICommandParameter(type = ParameterType.FIXED, fixedName = "run", description = "") String nameArg,
			@AICommandParameter(type = ParameterType.FILE, description = "") String file) {
		return new RundFileStrategy(file);
	}
}
