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
package net.famzangl.minecraft.minebot.ai.commands;

import com.google.common.base.Function;
import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.command.AIChatController;
import net.famzangl.minecraft.minebot.ai.command.AICommand;
import net.famzangl.minecraft.minebot.ai.command.AICommandInvocation;
import net.famzangl.minecraft.minebot.ai.command.AICommandParameter;
import net.famzangl.minecraft.minebot.ai.command.ArgumentDefinition;
import net.famzangl.minecraft.minebot.ai.command.CommandDefinition;
import net.famzangl.minecraft.minebot.ai.command.FixedNameBuilder.FixedArgumentDefinition;
import net.famzangl.minecraft.minebot.ai.command.ParameterType;
import net.famzangl.minecraft.minebot.ai.strategy.AIStrategy;
import net.famzangl.minecraft.minebot.ai.strategy.StopStrategy;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.util.text.StringTextComponent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@AICommand(helpText = "Gives help about the minebot commands.\n"
		+ "It can display an index of commands or can give help to a specific command.", name = "minebot")
final public class CommandHelp {

	private static final class CommandToTextConverter implements
			Function<CommandDefinition, String> {
		@Override
		public String apply(CommandDefinition command) {
			final StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append("/");
			stringBuilder.append(command.getCommandName());
			for (final ArgumentDefinition arg : command.getArguments()) {
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
			final int res = o1.getCommandName().compareTo(o2.getCommandName());
			if (res != 0) {
				return res;
			} else {
				final ArgumentDefinition a1 = o1.getArguments().get(0);
				final ArgumentDefinition a2 = o2.getArguments().get(0);
				if (a1 instanceof FixedArgumentDefinition
						&& a2 instanceof FixedArgumentDefinition) {
					final String n1 = ((FixedArgumentDefinition) a1)
							.getFixedName();
					final String n2 = ((FixedArgumentDefinition) a2)
							.getFixedName();
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
		final List<CommandDefinition> commands = new ArrayList<CommandDefinition>(
				AIChatController.getRegistry().getAllCommands());
		Collections.sort(commands, new CommandComperator());
		AIChatController.addToChatPaged("Help", page, commands,
				new CommandToTextConverter());
		return new StopStrategy();

	}

	@AICommandInvocation()
	public static AIStrategy run(
			AIHelper helper,
			@AICommandParameter(type = ParameterType.FIXED, fixedName = "help", description = "") String nameArg,
			@AICommandParameter(type = ParameterType.COMMAND, description = "command help") String commandName) {
		boolean found = false;
		final ClientPlayerEntity player = helper.getMinecraft().player;
		for (final CommandDefinition command : AIChatController.getRegistry()
				.getAllCommands()) {
			final ArrayList<ArgumentDefinition> args = command.getArguments();
			if (args.get(0).couldEvaluateAgainst(commandName)) {
				if (found) {
					player.sendMessage(new StringTextComponent(""));
				}
				printHelp(player, command);
			}
			found = true;
		}
		if (!found) {
			AIChatController.addChatLine("Command could not be found: "
					+ commandName);
		}
		return new StopStrategy();
	}

	private static void printHelp(ClientPlayerEntity player,
			CommandDefinition command) {
		final CommandToTextConverter conv = new CommandToTextConverter();

		final StringTextComponent headline = new StringTextComponent(
				conv.apply(command));
		headline.getStyle().setBold(true);
		player.sendMessage(headline);
		for (final String line : command.getHelpText().split("\n")) {
			final StringTextComponent text = new StringTextComponent(line);
			text.getStyle().setItalic(true);
			player.sendMessage(text);
		}

		for (final ArgumentDefinition arg : command.getArguments()) {
			final String[] help = arg.getDescriptionString().split("\n");
			for (final String text : help) {
				player.sendMessage(new StringTextComponent("   " + text));
			}
		}
	}
}