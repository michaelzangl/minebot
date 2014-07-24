package net.famzangl.minecraft.minebot.ai.command;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.AIStrategy;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraftforge.client.ClientCommandHandler;

public class CommandRegistry {
	private final class CommandHandler extends CommandBase {
		private final String name;

		public CommandHandler(String name) {
			super();
			this.name = name;
		}

		@Override
		public void processCommand(ICommandSender sender, String[] args) {
			if (controlled == null) {
				AIChatController.addChatLine("ERROR: No controller started.");
			}
			try {
				final AIStrategy strategy = evaluateCommand(
						controlled.getAiHelper(), name, args);
				if (strategy != null) {
					controlled.requestUseStrategy(strategy);
				}
			} catch (UnknownCommandException e) {
				if (e.getEvaluateable().size() > 0) {
					AIChatController
							.addChatLine("ERROR: More than 1 command matches your command line.");
				} else {
					AIChatController.addChatLine("ERROR: No such command.");
				}
			}
		}

		@Override
		public String getCommandUsage(ICommandSender var1) {
			return "/" + name + " ...";
		}

		@Override
		public String getCommandName() {
			return name;
		}

		@Override
		public List<String> addTabCompletionOptions(
				ICommandSender par1iCommandSender, String[] par2ArrayOfStr) {
			if (controlled == null) {
				return Collections.emptyList();
			}
			return tabCompletion(controlled.getAiHelper(), name,
					par2ArrayOfStr);
		}

		@Override
		public int getRequiredPermissionLevel() {
			return 0;
		}

	}

	private final Hashtable<String, List<CommandDefinition>> commandTable = new Hashtable<String, List<CommandDefinition>>();
	private IAIControllable controlled;

	public void register(Class<?> commandClass) {
		checkCommandCLass(commandClass);
		String name = commandClass.getAnnotation(AICommand.class).name();
		List<CommandDefinition> list = commandTable.get(name);
		if (list == null) {
			list = new ArrayList<CommandDefinition>();
			commandTable.put(name, list);
			addCommandHandler(name);
		}
		getCommandsForClass(commandClass, list);
	}

	private void addCommandHandler(String name) {
		ClientCommandHandler.instance.registerCommand(new CommandHandler(name));
		System.out.println("Command " + name + " registered.");
	}

	private void checkCommandCLass(Class<?> commandClass) {
		if (!commandClass.isAnnotationPresent(AICommand.class)) {
			throw new IllegalArgumentException(
					"AICommand is not set for this class.");
		}
	}

	public AIStrategy evaluateCommand(AIHelper helper, String commandID,
			String[] arguments) throws UnknownCommandException {
		List<CommandDefinition> commands = getCommands(commandID);
		ArrayList<CommandDefinition> evaluateable = new ArrayList<CommandDefinition>();
		for (CommandDefinition c : commands) {
			if (c.couldEvaluateAgainst(arguments)) {
				evaluateable.add(c);
			}
		}
		if (evaluateable.size() != 1) {
			throw new UnknownCommandException(evaluateable);
		} else {
			return evaluateable.get(0).evaluate(helper, arguments);
		}
	}

	public List<String> tabCompletion(AIHelper helper, String commandID,
			String[] currentArgs) {
		List<CommandDefinition> commands = getCommands(commandID);
		HashSet<String> suggestions = new HashSet<String>();
		String[] fixedArgs = Arrays.copyOf(currentArgs, currentArgs.length - 1);
		for (CommandDefinition c : commands) {
			ArrayList<ArgumentDefinition> args = c.getArguments();
			if (c.couldEvaluateStartingWith(fixedArgs)
					&& args.size() > fixedArgs.length) {
				ArgumentDefinition lastArg = c.getArguments().get(
						currentArgs.length - 1);
				lastArg.getTabCompleteOptions(
						currentArgs[currentArgs.length - 1], suggestions);
			}
		}
		ArrayList<String> asList = new ArrayList<String>(suggestions);
		Collections.sort(asList);
		return asList;
	}

	private List<CommandDefinition> getCommands(String commandID) {
		List<CommandDefinition> commands = commandTable.get(commandID);
		if (commands == null) {
			commands = Collections.emptyList();
		}
		return commands;
	}

	private void getCommandsForClass(Class<?> commandClass,
			List<CommandDefinition> commands) {
		for (Method m : commandClass.getMethods()) {
			if (Modifier.isStatic(m.getModifiers())
					&& m.isAnnotationPresent(AICommandInvocation.class)) {
				commands.add(getCommandForMethod(m));
			}
		}
	}

	private CommandDefinition getCommandForMethod(Method m) {
		return new CommandDefinition(m);
	}

	public List<CommandDefinition> getAllCommands() {
		ArrayList<CommandDefinition> defs = new ArrayList<CommandDefinition>();
		for (List<CommandDefinition> list : commandTable.values()) {
			defs.addAll(list);
		}
		return defs;
	}

	public void setControlled(IAIControllable controlled) {
		this.controlled = controlled;
	}
}
