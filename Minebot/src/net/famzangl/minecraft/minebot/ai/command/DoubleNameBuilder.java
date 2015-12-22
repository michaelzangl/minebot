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

public class DoubleNameBuilder extends ParameterBuilder {

	public DoubleNameBuilder(AICommandParameter annot) {
		super(annot);
	}

	@Override
	public void addArguments(ArrayList<ArgumentDefinition> list) {
		list.add(new ArgumentDefinition("Number", annot.description()) {
			@Override
			public boolean couldEvaluateAgainst(String string) {
				return string.matches("^[+-]?(\\d+\\.?\\d*|\\.\\d+)([eE]-?\\d+)?$");
			}
		});
	}

	@Override
	public Object getParameter(AIHelper helper, String[] arguments) {
		return Double.parseDouble(arguments[0]);
	}
	
	@Override
	public boolean isTypeValid(Class<?> class1) {
		return super.isTypeValid(class1) || (class1 == Double.TYPE && !isOptional());
	}

	@Override
	protected Class<?> getRequiredParameterClass() {
		return Double.class;
	}
}
