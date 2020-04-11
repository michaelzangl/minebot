package net.famzangl.minecraft.minebot.ai.strategy;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Scanner;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.command.AIChatController;
import net.famzangl.minecraft.minebot.ai.command.IAIControllable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;

public final class RunFileStrategy extends AIStrategy {

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

	private final File fileName;
	private LinkedList<String> commands = null;
	private IOException exception = null;
	private boolean fileRead;
	private boolean exceptionReported;
	private AIStrategy activeStrategy;
	private boolean repeatMode = false;
	private int stackMaxLeft = -1;
	private StrategyStack stack;

	public RunFileStrategy(File file) {
		super();
		this.fileName = file;
		new Thread() {
			@Override
			public void run() {
				try {
					commands = readFile(RunFileStrategy.this.fileName);
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
		} else if (stack != null) {
			if (stackMaxLeft == 0 || commands.isEmpty()) {
				AIChatController.addChatLine("Stack has no end.");
				return TickResult.NO_MORE_WORK;
			} else {
				String command = getNextCommand();
				if (command.equals("stack end")) {
					setActiveStrategy(helper, new StackStrategy(stack));
					stack = null;
					stackMaxLeft = -1;
					return TickResult.TICK_AGAIN;
				} else {
					final AIStrategy receivedStrategy = runAndGetStrategy(
							helper, command);
					if (receivedStrategy != null) {
						stack.addStrategy(receivedStrategy);
					} else {
						AIChatController
								.addChatLine("Command is not a strategy: "
										+ command);
						return TickResult.ABORT;
					}
					return TickResult.TICK_AGAIN;
				}
			}
		} else if (commands.isEmpty()) {
			AIChatController.addChatLine("Done");
			return TickResult.NO_MORE_WORK;
		} else {
			final String command = getNextCommand();
			if (command.equals("repeat:")) {
				repeatMode = true;
			} else if (command.equals("stack:")) {
				stackMaxLeft = commands.size();
				stack = new StrategyStack();
			} else {
				final AIStrategy receivedStrategy = runAndGetStrategy(helper,
						command);
				if (receivedStrategy != null) {
					setActiveStrategy(helper, receivedStrategy);
				}
			}
			return TickResult.TICK_AGAIN;
		}
	}

	private String getNextCommand() {
		String command = commands.removeFirst();
		if (repeatMode) {
			commands.add(command);
		}
		return command;
	}

	private void setActiveStrategy(AIHelper helper,
			final AIStrategy receivedStrategy) {
		activeStrategy = receivedStrategy;
		activeStrategy.setActive(true, helper);
	}

	@Override
	public String getDescription(AIHelper helper) {
		return "Running from file..."
				+ (activeStrategy != null ? "\n"
						+ activeStrategy.getDescription(helper) : "");
	}

	public static AIStrategy runAndGetStrategy(AIHelper helper,
			final String command) {
		final IAIControllable controlled = AIChatController.getRegistry()
				.getControlled();
		final StrategyReceiver tempController = new StrategyReceiver(controlled);
		try {
			AIChatController.getRegistry().setControlled(tempController);
			runCommand(helper, command);
		} finally {
			AIChatController.getRegistry().setControlled(controlled);
		}
		return tempController.getReceivedStrategy();
	}

	public static void runCommand(AIHelper helper, String command) {
		if (helper.getMinecraft().ingameGUI.getChatGUI() != null) {
			final GuiChat chat = new GuiChat();
			// helper.getMinecraft().displayGuiScreen(chat);
			chat.mc = helper.getMinecraft();
			chat.sendChatMessage(command);
			// helper.getMinecraft().displayGuiScreen((GuiScreen) null);
		}
	}

}