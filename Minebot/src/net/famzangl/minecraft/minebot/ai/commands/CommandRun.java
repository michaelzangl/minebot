package net.famzangl.minecraft.minebot.ai.commands;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Scanner;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.command.AIChatController;
import net.famzangl.minecraft.minebot.ai.command.AICommand;
import net.famzangl.minecraft.minebot.ai.command.AICommandInvocation;
import net.famzangl.minecraft.minebot.ai.command.AICommandParameter;
import net.famzangl.minecraft.minebot.ai.command.IAIControllable;
import net.famzangl.minecraft.minebot.ai.command.ParameterType;
import net.famzangl.minecraft.minebot.ai.strategy.AIStrategy;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiScreen;

@AICommand(helpText = "Run commands from a file.", name = "minebot")
public class CommandRun {
	private static final class StrategyReceiver implements IAIControllable {
		private final IAIControllable controlled;
		private AIStrategy receivedStrategy;

		private StrategyReceiver(IAIControllable controlled) {
			this.controlled = controlled;
		}

		@Override
		public void requestUseStrategy(AIStrategy strategy) {
			receivedStrategy = strategy;
		}

		@Override
		public Minecraft getMinecraft() {
			return controlled.getMinecraft();
		}

		@Override
		public AIHelper getAiHelper() {
			return controlled.getAiHelper();
		}

		public AIStrategy getReceivedStrategy() {
			return receivedStrategy;
		}
	}

	private static final class RunFileStrategy extends AIStrategy {

		private final String fileName;
		private LinkedList<String> commands = null;
		private IOException exception = null;
		private boolean fileRead;
		private boolean exceptionReported;
		private AIStrategy activeStrategy;
		private boolean repeatMode = false;

		public RunFileStrategy(String fileName) {
			super();
			this.fileName = fileName;
			new Thread() {
				@Override
				public void run() {
					try {
						commands = readFile(new File(
								RunFileStrategy.this.fileName));
					} catch (final FileNotFoundException e) {
						e.printStackTrace();
						exception = e;
					} finally {
						fileRead = true;
					}
				};

				private LinkedList<String> readFile(final File file)
						throws FileNotFoundException {
					final LinkedList<String> commands = new LinkedList<String>();
					final Scanner reader = new Scanner(file);
					String line;
					while (reader.hasNextLine()) {
						line = reader.nextLine();
						if (!line.startsWith("#") && !line.isEmpty()) {
							commands.add(line);
						}
					}
					reader.close();
					return commands;
				}
			}.start();
		}

		@Override
		public boolean checkShouldTakeOver(AIHelper helper) {
			return !fileRead
					|| (exception != null ? !exceptionReported : !commands
							.isEmpty());
		}

		@Override
		protected TickResult onGameTick(AIHelper helper) {
			if (!fileRead) {
				return TickResult.TICK_HANDLED;
			} else if (exception != null) {
				if (!exceptionReported) {
					AIChatController.addChatLine("Error while reading file: "
							+ exception.getMessage());
					exceptionReported = true;
				}
				return TickResult.NO_MORE_WORK;
			} else if (activeStrategy != null) {
				final TickResult result = activeStrategy.gameTick(helper);
				if (result == TickResult.NO_MORE_WORK) {
					activeStrategy.setActive(false, helper);
					activeStrategy = null;
					return TickResult.TICK_AGAIN;
				} else {
					return result;
				}
			} else if (commands.isEmpty()) {

				AIChatController.addChatLine("Done");
				return TickResult.NO_MORE_WORK;
			} else {
				final IAIControllable controlled = AIChatController
						.getRegistry().getControlled();
				final StrategyReceiver tempController = new StrategyReceiver(
						controlled);
				try {
					AIChatController.getRegistry()
							.setControlled(tempController);
					final String command = commands.removeFirst();
					runCommand(helper, command);
					if (repeatMode) {
						commands.add(command);
					}
				} finally {
					AIChatController.getRegistry().setControlled(controlled);
				}
				if (tempController.receivedStrategy != null) {
					activeStrategy = tempController.getReceivedStrategy();
					activeStrategy.setActive(true, helper);
				}
				return TickResult.TICK_AGAIN;
			}
		}

		private void runCommand(AIHelper helper, String command) {
			if (command.equals("repeat:")) {
				repeatMode = true;
			} else {
				CommandRun.runCommand(helper, command);
			}
		}

		@Override
		public String getDescription() {
			return "Running from file.";
		}

	}

	public static void runCommand(AIHelper helper, String command) {
		if (helper.getMinecraft().ingameGUI.getChatGUI() != null) {
			final GuiChat chat = new GuiChat();
			helper.getMinecraft().displayGuiScreen(chat);
			chat.func_146403_a(command);
			helper.getMinecraft().displayGuiScreen((GuiScreen) null);
		}
	}

	@AICommandInvocation()
	public static AIStrategy run(
			AIHelper helper,
			@AICommandParameter(type = ParameterType.FIXED, fixedName = "run", description = "") String nameArg,
			@AICommandParameter(type = ParameterType.FILE, description = "") String file) {
		return new RunFileStrategy(file);
	}
}
