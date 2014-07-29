package net.famzangl.minecraft.minebot.ai.command;

import java.util.ArrayList;
import java.util.Collection;

import net.famzangl.minecraft.minebot.ai.AIHelper;

public class EnumNameBuilder<T extends Enum<T>> extends ParameterBuilder {

	private final Class<T> enumClass;

	private final static class EnumArgumentDefinition extends
			ArgumentDefinition {

		private final Enum<?>[] options;

		public EnumArgumentDefinition(String descriptionType,
				String descriptionInfo, Enum<?>[] options) {
			super(descriptionType, descriptionInfo);
			this.options = options;
		}

		@Override
		public boolean couldEvaluateAgainst(String string) {
			for (Enum<?> type : options) {
				if (type.name().equalsIgnoreCase(string)) {
					return true;
				}
			}
			return false;
		}

		@Override
		public void getTabCompleteOptions(String currentStart,
				Collection<String> addTo) {
			for (Enum<?> type : options) {
				if (type.name().toLowerCase()
						.startsWith(currentStart.toLowerCase())) {
					addTo.add(type.name().toLowerCase());
				}
			}
		}
	}

	public EnumNameBuilder(AICommandParameter annot, Class<T> enumClass) {
		super(annot);
		this.enumClass = enumClass;
	}

	@Override
	public void addArguments(ArrayList<ArgumentDefinition> list) {
		list.add(new EnumArgumentDefinition(enumClass.getSimpleName(), annot.description(),
				enumClass.getEnumConstants()));
	}

	@Override
	public Object getParameter(AIHelper helper, String[] arguments) {
		return Enum.valueOf(enumClass, arguments[0].toUpperCase());
	}

}
