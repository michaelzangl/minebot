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

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.minecraft.command.ISuggestionProvider;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

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
		public List<CommandNode<ISuggestionProvider>> addTabCompleteNodesTo(CommandNode<ISuggestionProvider> node) {
			LiteralCommandNode<ISuggestionProvider> literal = LiteralArgumentBuilder.<ISuggestionProvider>literal(fixedName).build();
			node.addChild(literal);
			return Collections.singletonList(literal);
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
