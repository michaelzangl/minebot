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
import java.util.Collection;

import net.famzangl.minecraft.minebot.ai.AIHelper;

public class FixedNameBuilder extends ParameterBuilder {

	public final class FixedArgumentDefinition extends ArgumentDefinition {
		private FixedArgumentDefinition(String descriptionType,
				String descriptionInfo) {
			super(descriptionType, descriptionInfo);
		}

		@Override
		public void getTabCompleteOptions(String currentStart,
				Collection<String> addTo) {
			super.getTabCompleteOptions(currentStart, addTo);
			if (fixedName.startsWith(currentStart)) {
				addTo.add(fixedName);
			}
		}

		@Override
		public boolean couldEvaluateAgainst(String string) {
			return string.equals(fixedName);
		}

		public String getFixedName() {
			return fixedName;
		}
	}

	private final String fixedName;

	public FixedNameBuilder(AICommandParameter annot) {
		super(annot);
		this.fixedName = annot.fixedName();
	}

	@Override
	public void addArguments(ArrayList<ArgumentDefinition> list) {
		list.add(new FixedArgumentDefinition("'" + fixedName + "'",
				"fixed String"));
	}

	@Override
	public Object getParameter(AIHelper helper, String[] arguments) {
		return arguments[0];
	}

	@Override
	protected Class<?> getRequiredParameterClass() {
		return String.class;
	}
}
