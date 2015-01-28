package net.famzangl.minecraft.minebot.ai.command;

import java.util.ArrayList;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.minecraft.command.CommandBase;
import net.minecraft.command.NumberInvalidException;

public class PositionNameBuilder extends ParameterBuilder {

	private final static class PositionArgumentDefinition extends
			ArgumentDefinition {
		public PositionArgumentDefinition(String description, String dir) {
			super("pos." + dir, description);
		}

		@Override
		public boolean couldEvaluateAgainst(String string) {
			return true;
		}
	}

	public PositionNameBuilder(AICommandParameter annot) {
		super(annot);
	}

	@Override
	public void addArguments(ArrayList<ArgumentDefinition> list) {
		list.add(new PositionArgumentDefinition(annot.description(), "x"));
		list.add(new PositionArgumentDefinition(annot.description(), "y"));
		list.add(new PositionArgumentDefinition(annot.description(), "z"));
	}

	@Override
	public Object getParameter(AIHelper helper, String[] arguments) {
		try {
			return CommandBase.func_175757_a(helper.getMinecraft().thePlayer,
					arguments, 0, false);
		} catch (final NumberInvalidException e) {
			throw new CommandEvaluationException("Number format not supported.");
		}
	}

}
