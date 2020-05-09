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

import com.mojang.brigadier.tree.CommandNode;
import net.minecraft.command.ISuggestionProvider;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

/**
 * This is the definition of a single argument to be passed on the command line.
 * 
 * @author michael
 * 
 */
public class ArgumentDefinition {
	private final String descriptionType;
	private final String descriptionInfo;

	public ArgumentDefinition() {
		this("", "");
	}

	public ArgumentDefinition(String descriptionType) {
		this(descriptionType, "");
	}

	public ArgumentDefinition(String descriptionType, String descriptionInfo) {
		super();
		this.descriptionType = descriptionType;
		this.descriptionInfo = descriptionInfo;
	}

	public void getTabCompleteOptions(List<String> previousArguments, String currentStart,
			Collection<String> addTo) {
		getTabCompleteOptions(currentStart, addTo);
	}
	
	public void getTabCompleteOptions(String currentStart,
			Collection<String> addTo) {
	}


	public boolean couldEvaluateAgainst(List<String> previousArguments, String string) {
		return couldEvaluateAgainst(string);
	}
	
	/**
	 * Check if this argument could be from that parameter. Checks e.g. for
	 * integer values.
	 * 
	 * @param string
	 * @return
	 */
	public boolean couldEvaluateAgainst(String string) {
		return true;
	}

	public final String getDescriptionString() {
		final String info = getDescriptionInfo();
		return getDescriptionType()
				+ (info == null || info.isEmpty() ? "" : ": " + info);
	}

	public String getDescriptionType() {
		return descriptionType;
	}

	private String getDescriptionInfo() {
		return descriptionInfo;
	}

	/**
	 * Adds my tab complete options to the given command node
	 * @param node The node to add the completions to
	 * @return A set of leave nodes that the next argument can be added to.
	 */
    public List<CommandNode<ISuggestionProvider>> addTabCompleteNodesTo(CommandNode<ISuggestionProvider> node) {
    	return new ArrayList<>();
    }
}
