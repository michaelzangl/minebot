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
