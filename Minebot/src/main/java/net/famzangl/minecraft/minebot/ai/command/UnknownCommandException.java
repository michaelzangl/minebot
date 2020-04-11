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
import java.util.Arrays;

public class UnknownCommandException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3166540443273412972L;
	private final ArrayList<CommandDefinition> evaluateable;
	private final String commandID;
	private final String[] arguments;

	public UnknownCommandException(String commandID, String[] arguments, ArrayList<CommandDefinition> evaluateable) {
		this.commandID = commandID;
		this.arguments = arguments;
		this.evaluateable = evaluateable;
	}

	public ArrayList<CommandDefinition> getEvaluateable() {
		return evaluateable;
	}
	
	@Override
	public String getMessage() {
		if (evaluateable.isEmpty()) {
			return "Could not find a meaning for /" + commandID + " " + Arrays.toString(arguments);
		} else {
			return "Those arguments are ambigious for /" + commandID + " " + Arrays.toString(arguments);
		}
	}
}
