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
import net.famzangl.minecraft.minebot.ai.strategy.AIStrategy;
import net.famzangl.minecraft.minebot.ai.strategy.AbortOnDeathStrategy;
import net.famzangl.minecraft.minebot.ai.strategy.CreeperComesActionStrategy;
import net.famzangl.minecraft.minebot.ai.strategy.DamageTakenStrategy;
import net.famzangl.minecraft.minebot.ai.strategy.DoNotSuffocateStrategy;
import net.famzangl.minecraft.minebot.ai.strategy.PlayerComesActionStrategy;
import net.famzangl.minecraft.minebot.ai.strategy.StackStrategy;
import net.famzangl.minecraft.minebot.ai.strategy.StrategyStack;
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
				final AIStrategy strategy = evaluateCommandWithSaferule(
						controlled.getAiHelper(), name, args);
				if (strategy != null) {
					controlled.requestUseStrategy(strategy);
				}
			} catch (final UnknownCommandException e) {
				if (e.getEvaluateable().size() > 0) {
					AIChatController
							.addChatLine("ERROR: More than 1 command matches your command line.");
				} else {

					AIChatController.addChatLine("ERROR: No command:"
							+ combine(args) + ".");
				}
			} catch (final CommandEvaluationException e) {
				AIChatController.addChatLine("ERROR while evaluating: "
						+ e.getMessage());
			} catch (final Throwable e) {
				e.printStackTrace();
				AIChatController
						.addChatLine("ERROR: Could not evaluate. Please report.");
			}
		}

		private String combine(String[] args) {
			final StringBuilder b = new StringBuilder();
			for (final String a : args) {
				b.append(" ");
				b.append(a);
			}
			return b.toString();
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
			return tabCompletion(controlled.getAiHelper(), name, par2ArrayOfStr);
		}

		@Override
		public int getRequiredPermissionLevel() {
			return 0;
		}

	}

	private final Hashtable<String, List<CommandDefinition>> commandTable = new Hashtable<String, List<CommandDefinition>>();
	private IAIControllable controlled;

	public void register(Class<?> commandClass) {
		checkCommandClass(commandClass);
		final String name = commandClass.getAnnotation(AICommand.class).name();
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

	private void checkCommandClass(Class<?> commandClass) {
		if (!commandClass.isAnnotationPresent(AICommand.class)) {
			throw new IllegalArgumentException(
					"AICommand is not set for this class.");
		}
	}

	public AIStrategy evaluateCommandWithSaferule(AIHelper helper,
			String commandID, String[] arguments)
			throws UnknownCommandException {
		CommandDefinition evaluateableCommand = getEvaluatebale(commandID, arguments);
		AIStrategy strategy = evaluateableCommand.evaluate(helper, arguments);
		SafeStrategyRule safeRule = evaluateableCommand.getSafeStrategyRule();
		if (safeRule != SafeStrategyRule.NONE && strategy != null) {
			strategy = makeSafe(strategy, safeRule);
		}
		return strategy;
	}

	public AIStrategy evaluateCommand(AIHelper helper, String commandID,
			String[] arguments) throws UnknownCommandException {
		CommandDefinition evaluateableCommand = getEvaluatebale(commandID, arguments);
		return evaluateableCommand.evaluate(helper, arguments);
	}

	private CommandDefinition getEvaluatebale(String commandID, String[] arguments)
			throws UnknownCommandException {
		final List<CommandDefinition> commands = getCommands(commandID);
		final ArrayList<CommandDefinition> evaluateable = new ArrayList<CommandDefinition>();
		for (final CommandDefinition c : commands) {
			if (c.couldEvaluateAgainst(arguments)) {
				evaluateable.add(c);
			}
		}
		if (evaluateable.size() != 1) {
			throw new UnknownCommandException(evaluateable);
		}
		CommandDefinition evaluateableCommand = evaluateable.get(0);
		return evaluateableCommand;
	}

	private AIStrategy makeSafe(AIStrategy strategy, SafeStrategyRule safeRule) {
		final StrategyStack stack = new StrategyStack();
		stack.addStrategy(new AbortOnDeathStrategy());
		if (safeRule == SafeStrategyRule.DEFEND_MINING) {
			stack.addStrategy(new DoNotSuffocateStrategy());
		}
		stack.addStrategy(new DamageTakenStrategy());
		stack.addStrategy(new PlayerComesActionStrategy());
		stack.addStrategy(new CreeperComesActionStrategy());
		stack.addStrategy(strategy);
		return new StackStrategy(stack);
	}

	public List<String> tabCompletion(AIHelper helper, String commandID,
			String[] currentArgs) {
		final List<CommandDefinition> commands = getCommands(commandID);
		final HashSet<String> suggestions = new HashSet<String>();
		final String[] fixedArgs = Arrays.copyOf(currentArgs,
				currentArgs.length - 1);
		for (final CommandDefinition c : commands) {
			final ArrayList<ArgumentDefinition> args = c.getArguments();
			if (c.couldEvaluateStartingWith(fixedArgs)
					&& args.size() > fixedArgs.length) {
				final ArgumentDefinition lastArg = c.getArguments().get(
						currentArgs.length - 1);
				lastArg.getTabCompleteOptions(
						currentArgs[currentArgs.length - 1], suggestions);
			}
		}
		final ArrayList<String> asList = new ArrayList<String>(suggestions);
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
		for (final Method m : commandClass.getMethods()) {
			if (Modifier.isStatic(m.getModifiers())
					&& m.isAnnotationPresent(AICommandInvocation.class)) {
				commands.addAll(getCommandsForMethod(m));
			}
		}
	}

	private ArrayList<CommandDefinition> getCommandsForMethod(Method m) {
		return CommandDefinition.getDefinitions(m);
	}

	public List<CommandDefinition> getAllCommands() {
		final ArrayList<CommandDefinition> defs = new ArrayList<CommandDefinition>();
		for (final List<CommandDefinition> list : commandTable.values()) {
			defs.addAll(list);
		}
		return defs;
	}

	public IAIControllable getControlled() {
		return controlled;
	}

	public void setControlled(IAIControllable controlled) {
		this.controlled = controlled;
	}
}
