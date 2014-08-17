package net.famzangl.minecraft.minebot.ai.command;

import java.util.ArrayList;
import java.util.Collection;

import net.famzangl.minecraft.minebot.ai.AIHelper;

public class CommandNameBuilder extends ParameterBuilder {

	private final static class CommandArgumentDefinition extends
			ArgumentDefinition {
		public CommandArgumentDefinition(String description) {
			super("Command", description);
		}

		@Override
		public boolean couldEvaluateAgainst(String string) {
			for (final CommandDefinition command : AIChatController
					.getRegistry().getAllCommands()) {
				final ArrayList<ArgumentDefinition> args = command
						.getArguments();
				if (args.get(0).couldEvaluateAgainst(string)) {
					return true;
				}
			}
			return false;
		}

		@Override
		public void getTabCompleteOptions(String currentStart,
				Collection<String> addTo) {
			for (final CommandDefinition command : AIChatController
					.getRegistry().getAllCommands()) {
				final ArrayList<ArgumentDefinition> args = command
						.getArguments();
				args.get(0).getTabCompleteOptions(currentStart, addTo);
			}
		}
	}

	public CommandNameBuilder(AICommandParameter annot) {
		super(annot);
	}

	@Override
	public void addArguments(ArrayList<ArgumentDefinition> list) {
		list.add(new CommandArgumentDefinition(annot.description()));
	}

	@Override
	public Object getParameter(AIHelper helper, String[] arguments) {
		return arguments[0];
	}

}
