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
import net.famzangl.minecraft.minebot.ai.ColoredBlockItemFilter;

public class ColorNameBuilder extends ParameterBuilder {

	private final static class ColorArgumentDefinition extends
			ArgumentDefinition {
		public ColorArgumentDefinition(String description) {
			super("Color", description);
		}

		@Override
		public boolean couldEvaluateAgainst(String string) {
			for (final String color : ColoredBlockItemFilter.COLORS) {
				if (color.equalsIgnoreCase(string)) {
					return true;
				}
			}
			return false;
		}

		@Override
		public void getTabCompleteOptions(String currentStart,
				Collection<String> addTo) {
			for (final String color : ColoredBlockItemFilter.COLORS) {
				if (color.toLowerCase().startsWith(currentStart.toLowerCase())) {
					addTo.add(color);
				}
			}
		}
	}

	public ColorNameBuilder(AICommandParameter annot) {
		super(annot);
	}

	@Override
	public void addArguments(ArrayList<ArgumentDefinition> list) {
		list.add(new ColorArgumentDefinition(annot.description()));
	}

	@Override
	public Object getParameter(AIHelper helper, String[] arguments) {
		final String[] colors = ColoredBlockItemFilter.COLORS;
		for (int i = 0; i < colors.length; i++) {
			final String color = colors[i];
			if (color.equalsIgnoreCase(arguments[0])) {
				// FIXME: REturn color object.
				// EnumDyeColor
				return i;
			}
		}
		throw new CommandEvaluationException("Not a color: " + arguments[0]);
	}

}
