package net.famzangl.minecraft.minebot.ai.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.AIStrategy;
import net.famzangl.minecraft.minebot.ai.command.AIChatController;
import net.famzangl.minecraft.minebot.ai.command.AICommand;
import net.famzangl.minecraft.minebot.ai.command.AICommandInvocation;
import net.famzangl.minecraft.minebot.ai.command.AICommandParameter;
import net.famzangl.minecraft.minebot.ai.command.ArgumentDefinition;
import net.famzangl.minecraft.minebot.ai.command.CommandDefinition;
import net.famzangl.minecraft.minebot.ai.command.FixedNameBuilder.FixedArgumentDefinition;
import net.famzangl.minecraft.minebot.ai.command.ParameterType;

import com.google.common.base.Function;

@AICommand(helpText = "Gives help about the minebot commands.\n"
		+ "It can display an index of commands or can give help to a specific command.", name = "minebot")
final public class CommandHelp {

	private static final class CommandToTextConverter implements
			Function<CommandDefinition, String> {
		@Override
		public String apply(CommandDefinition command) {
			StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append("/");
			stringBuilder.append(command.getCommandName());
			for (ArgumentDefinition arg : command.getArguments()) {
				stringBuilder.append(" ");
				stringBuilder.append(arg.getDescriptionType());
			}
			return stringBuilder.toString();
		}
	}

	private static final class CommandComperator implements
			Comparator<CommandDefinition> {
		@Override
		public int compare(CommandDefinition o1, CommandDefinition o2) {
			int res = o1.getCommandName().compareTo(o2.getCommandName());
			if (res != 0) {
				return res;
			} else {
				ArgumentDefinition a1 = o1.getArguments().get(0);
				ArgumentDefinition a2 = o2.getArguments().get(0);
				if (a1 instanceof FixedArgumentDefinition
						&& a2 instanceof FixedArgumentDefinition) {
					String n1 = ((FixedArgumentDefinition) a1).getFixedName();
					String n2 = ((FixedArgumentDefinition) a2).getFixedName();
					return n1.compareTo(n2);
				} else {
					return 0;
				}
			}
		}
	}

	@AICommandInvocation()
	public static AIStrategy run(
			AIHelper helper,
			@AICommandParameter(type = ParameterType.FIXED, fixedName = "help", description = "") String nameArg) {
		return run(helper, nameArg, 1);
	}

	@AICommandInvocation()
	public static AIStrategy run(
			AIHelper helper,
			@AICommandParameter(type = ParameterType.FIXED, fixedName = "help", description = "") String nameArg,
			@AICommandParameter(type = ParameterType.NUMBER, description = "help page") int page) {
		List<CommandDefinition> commands = new ArrayList<>(AIChatController
				.getRegistry().getAllCommands());
		Collections.sort(commands, new CommandComperator());
		AIChatController.addToChatPaged("Help", page, commands,
				new CommandToTextConverter());
		return null;

	}

	@AICommandInvocation()
	public static AIStrategy run(
			AIHelper helper,
			@AICommandParameter(type = ParameterType.FIXED, fixedName = "help", description = "") String nameArg,
			@AICommandParameter(type = ParameterType.COMMAND, description = "command help") String commandName) {
		boolean found = false;
		for (CommandDefinition command : AIChatController.getRegistry()
				.getAllCommands()) {
			ArrayList<ArgumentDefinition> args = command.getArguments();
			if (args.get(0).couldEvaluateAgainst(commandName)) {
				printHelp(command, args);
			}
			found = true;
		}
		if (!found) {
			AIChatController.addChatLine("Command could not be found: "
					+ commandName);
		}
		return null;
	}

	private static void printHelp(CommandDefinition command,
			ArrayList<ArgumentDefinition> args) {
		AIChatController.addChatLine(command.getCommandName() + ": "
				+ command.getHelpText());
		for (ArgumentDefinition arg : args) {
			String[] help = arg.getDescriptionString().split("\n");
			for (final String text : help) {
				AIChatController.addChatLine(text);
			}
		}
	}
}