package net.famzangl.minecraft.minebot.ai.command;

import java.util.ArrayList;

import net.famzangl.minecraft.minebot.ai.AIHelper;

public class StringNameBuilder extends ParameterBuilder {

	private final static class StringArgumentDefinition extends
			ArgumentDefinition {
		
	}
	
	public StringNameBuilder(AICommandParameter annot) {
		super(annot);
	}

	@Override
	public void addArguments(ArrayList<ArgumentDefinition> list) {
		list.add(new StringArgumentDefinition());
	}

	@Override
	public Object getParameter(AIHelper helper, String[] arguments) {
		return arguments[0];
	}

}
