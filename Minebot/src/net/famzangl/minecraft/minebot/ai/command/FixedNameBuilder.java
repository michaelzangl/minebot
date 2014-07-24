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
		list.add(new FixedArgumentDefinition("'" + fixedName + "'", "fixed String"));
	}

	@Override
	public Object getParameter(AIHelper helper, String[] arguments) {
		return arguments[0];
	}

}
