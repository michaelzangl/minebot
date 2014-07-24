package net.famzangl.minecraft.minebot.ai.command;

import java.util.ArrayList;

import net.famzangl.minecraft.minebot.ai.AIHelper;

public class AIHelperBuilder extends ParameterBuilder {

	public AIHelperBuilder(AICommandParameter annot) {
		super(annot);
	}

	@Override
	public void addArguments(ArrayList<ArgumentDefinition> list) {
		// ignored.
	}
	
	@Override
	public Object getParameter(AIHelper helper, String[] arguments) {
		return helper;
	}

}
