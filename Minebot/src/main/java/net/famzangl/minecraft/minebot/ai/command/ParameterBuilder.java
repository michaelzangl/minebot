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

	public boolean isTypeValid(Class<?> class1) {
		return class1.isAssignableFrom(getRequiredParameterClass());
	}

	protected Class<?> getRequiredParameterClass() {
		throw new UnsupportedOperationException("No parameter class registered for " + getClass().getCanonicalName());
	}
}
