/*******************************************************************************
 * This file is part of Minebot.
 *
 * Minebot is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Minebot is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Minebot.  If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
package net.famzangl.minecraft.minebot.ai.command;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedHashSet;
import java.util.List;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.net.MinebotNetHandler;
import net.famzangl.minecraft.minebot.ai.strategy.AIStrategy;
import net.famzangl.minecraft.minebot.ai.strategy.AbortOnDeathStrategy;
import net.famzangl.minecraft.minebot.ai.strategy.CreeperComesActionStrategy;
import net.famzangl.minecraft.minebot.ai.strategy.DamageTakenStrategy;
import net.famzangl.minecraft.minebot.ai.strategy.DoNotSuffocateStrategy;
import net.famzangl.minecraft.minebot.ai.strategy.EatStrategy;
import net.famzangl.minecraft.minebot.ai.strategy.PlaceTorchStrategy;
import net.famzangl.minecraft.minebot.ai.strategy.PlayerComesActionStrategy;
import net.famzangl.minecraft.minebot.ai.strategy.StackStrategy;
import net.famzangl.minecraft.minebot.ai.strategy.StrategyStack;
import net.minecraft.command.ServerCommandManager;
import net.minecraft.network.ThreadQuickExitException;
import net.minecraft.network.play.server.S3APacketTabComplete;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;

public class CommandRegistry {

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

	public void execute(String name, String[] args) {
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

	public List<String> addTabCompletionOptions(String name, String[] args,
			BlockPos pos) {
		// FIXME
		if (controlled == null) {
			return Collections.emptyList();
		}
		return tabCompletion(controlled.getAiHelper(), name, args);
	}

	private void addCommandHandler(String name) {
		ServerCommandManager cm = ((ServerCommandManager) (MinecraftServer
				.getServer().getCommandManager()));
		// FIXME!
		// cm.registerCommand(new CommandHandler(name));
		System.out.println("Command " + name + " registered.");
	}

	private void checkCommandClass(Class<?> commandClass) {
		if (!commandClass.isAnnotationPresent(AICommand.class)) {
			throw new IllegalArgumentException(
					"AICommand is not set for the class " + commandClass.getName() + ".");
		}
	}

	public AIStrategy evaluateCommandWithSaferule(AIHelper helper,
			String commandID, String[] arguments)
			throws UnknownCommandException {
		CommandDefinition evaluateableCommand = getEvaluatebale(commandID,
				arguments);
		AIStrategy strategy = evaluateableCommand.evaluate(helper, arguments);
		SafeStrategyRule safeRule = evaluateableCommand.getSafeStrategyRule();
		if (safeRule != SafeStrategyRule.NONE && strategy != null) {
			strategy = makeSafe(strategy, safeRule);
		}
		return strategy;
	}

	public AIStrategy evaluateCommand(AIHelper helper, String commandID,
			String[] arguments) throws UnknownCommandException {
		CommandDefinition evaluateableCommand = getEvaluatebale(commandID,
				arguments);
		return evaluateableCommand.evaluate(helper, arguments);
	}

	private CommandDefinition getEvaluatebale(String commandID,
			String[] arguments) throws UnknownCommandException {
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
		stack.addStrategy(new EatStrategy());
		if (safeRule == SafeStrategyRule.DEFEND_MINING) {
			stack.addStrategy(new PlaceTorchStrategy());
		}
		stack.addStrategy(strategy);
		return new StackStrategy(stack);
	}

	public List<String> tabCompletion(AIHelper helper, String commandID,
			String[] currentArgs) {
		final List<CommandDefinition> commands = getCommands(commandID);
		final HashSet<String> suggestions = new HashSet<String>();
		final String[] fixedArgs;
		if (currentArgs.length > 0) {
			fixedArgs = Arrays.copyOf(currentArgs, currentArgs.length - 1);
		} else {
			fixedArgs = currentArgs;
			currentArgs = new String[] { "" };
		}
		for (final CommandDefinition c : commands) {
			final ArrayList<ArgumentDefinition> args = c.getArguments();
			if (c.couldEvaluateStartingWith(fixedArgs)
					&& args.size() > fixedArgs.length) {
				final ArgumentDefinition lastArg = c.getArguments().get(
						fixedArgs.length);
				lastArg.getTabCompleteOptions(currentArgs[fixedArgs.length],
						suggestions);
			}
		}
		final ArrayList<String> asList = new ArrayList<String>(suggestions);
		Collections.sort(asList);
		return asList;
	}

	public String[] fillTabComplete(MinebotNetHandler minebotNetHandler, String[] serverResponse,
			String lastSendTabComplete) {
		String command = getCommandId(lastSendTabComplete);
		LinkedHashSet<String> res = new LinkedHashSet<String>();
		for (String c : commandTable.keySet()) {
			if (c.startsWith(command)) {
				res.add("/" + c);
			}
		}
		if (res.isEmpty())
			return serverResponse;
		res.addAll(Arrays.asList(serverResponse));
		
		return res.toArray(new String[0]);
	}

	public boolean interceptCommand(String m) {
		if (!m.startsWith("/")) {
			return false;
		}
		String commandId = getCommandId(m);
		if (commandTable.containsKey(commandId)) {
			System.out.println("Minebot handling command: " + m);
			String[] args = getCommandArgs(m);
			execute(commandId, args);
			return true;
		}

		return false;
	}

	private String[] getCommandArgs(String m) {
		String[] args = m.split("\\s+", -1);
		args = Arrays.copyOfRange(args, 1, args.length);
		return args;
	}

	private String getCommandId(String m) {
		int end = m.indexOf(' ');
		if (end < 0) {
			end = m.length();
		}
		String commandId = m.substring(1, end);
		return commandId;
	}

	public boolean interceptTab(String m, final MinebotNetHandler respondTo) {
		if (!m.startsWith("/")) {
			return false;
		}
		String commandId = getCommandId(m);
		if (commandTable.containsKey(commandId)) {
			String[] args = getCommandArgs(m);
			System.out.println("Minebot handling tab: " + commandId + ", "
					+ combine(args));
			List<String> options = addTabCompletionOptions(commandId, args,
					null);
			final S3APacketTabComplete packet = new S3APacketTabComplete(
					options.toArray(new String[options.size()]));
			new Thread("Tab response") {
				public void run() {
					try {
						respondTo.handleTabComplete(packet);
					} catch (ThreadQuickExitException t) {
					}
				};
			}.start();
			return true;
		}

		return false;
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
