package net.famzangl.minecraft.minebot.ai.command;

import java.util.ArrayList;

public class UnknownCommandException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3166540443273412972L;
	private final ArrayList<CommandDefinition> evaluateable;

	public UnknownCommandException(ArrayList<CommandDefinition> evaluateable) {
		this.evaluateable = evaluateable;
	}

	public ArrayList<CommandDefinition> getEvaluateable() {
		return evaluateable;
	}

}
