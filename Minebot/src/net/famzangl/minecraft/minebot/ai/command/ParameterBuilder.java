package net.famzangl.minecraft.minebot.ai.command;

import java.util.ArrayList;

import net.famzangl.minecraft.minebot.ai.AIHelper;

public abstract class ParameterBuilder {

	protected final AICommandParameter annot;

	public ParameterBuilder(AICommandParameter annot) {
		this.annot = annot;
	}

	public abstract void addArguments(ArrayList<ArgumentDefinition> list);

	/**
	 * Gets a parameter
	 * 
	 * @param helper
	 * @param arguments
	 *            The arguments this parameter should be constructed for. This
	 *            array has exactly as many elements as were added to the list
	 *            in {@link #addArguments(ArrayList)}
	 * @return The parameter or <code>null</code> if it could not be
	 *         constructed.
	 */
	public abstract Object getParameter(AIHelper helper, String[] arguments);

	boolean isOptional() {
		return annot != null && annot.optional();
	}
}
