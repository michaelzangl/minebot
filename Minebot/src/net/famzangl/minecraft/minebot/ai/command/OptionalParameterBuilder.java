package net.famzangl.minecraft.minebot.ai.command;

import java.util.ArrayList;

import net.famzangl.minecraft.minebot.ai.AIHelper;

public class OptionalParameterBuilder extends ParameterBuilder {

	public OptionalParameterBuilder(AICommandParameter annot) {
		super(annot);
	}

	@Override
	public void addArguments(ArrayList<ArgumentDefinition> list) {
	}

	@Override
	public Object getParameter(AIHelper helper, String[] arguments) {
		return null;
	}

}
