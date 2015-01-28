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
