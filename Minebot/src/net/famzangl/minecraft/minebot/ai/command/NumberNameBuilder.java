package net.famzangl.minecraft.minebot.ai.command;

import java.util.ArrayList;

import net.famzangl.minecraft.minebot.ai.AIHelper;

/**
 * A number.
 * @author michael
 *
 */
public class NumberNameBuilder extends ParameterBuilder {

	public NumberNameBuilder(AICommandParameter annot) {
		super(annot);
	}

	@Override
	public void addArguments(ArrayList<ArgumentDefinition> list) {
		list.add(new ArgumentDefinition("Number", annot.description()) {
			@Override
			public boolean couldEvaluateAgainst(String string) {
				return string.matches("\\d+");
			}
		});
	}

	@Override
	public Object getParameter(AIHelper helper, String[] arguments) {
		return Integer.parseInt(arguments[0]);
	}

}
