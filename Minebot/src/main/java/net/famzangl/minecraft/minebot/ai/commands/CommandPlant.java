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
package net.famzangl.minecraft.minebot.ai.commands;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.command.AICommand;
import net.famzangl.minecraft.minebot.ai.command.AICommandInvocation;
import net.famzangl.minecraft.minebot.ai.command.AICommandParameter;
import net.famzangl.minecraft.minebot.ai.command.ParameterType;
import net.famzangl.minecraft.minebot.ai.command.SafeStrategyRule;
import net.famzangl.minecraft.minebot.ai.path.PlantPathFinder;
import net.famzangl.minecraft.minebot.ai.path.PlantPathFinder.PlantType;
import net.famzangl.minecraft.minebot.ai.path.SugarCanePathFinder;
import net.famzangl.minecraft.minebot.ai.strategy.AIStrategy;
import net.famzangl.minecraft.minebot.ai.strategy.PathFinderStrategy;

@AICommand(helpText = "Plants plants\n" + "Uses a hoe if needed.", name = "minebot")
public class CommandPlant {

	@AICommandInvocation(safeRule = SafeStrategyRule.DEFEND)
	public static AIStrategy run(
			AIHelper helper,
			@AICommandParameter(type = ParameterType.FIXED, fixedName = "plant", description = "") String nameArg,
			@AICommandParameter(type = ParameterType.ENUM, description = "plant type", optional = true) PlantType type) {
		PlantType type2 = type == null ? PlantType.NORMAL : type;
		return new PathFinderStrategy(
				new PlantPathFinder(type2), "Planting " + type2.toString().toLowerCase());
	}
	
	@AICommandInvocation(safeRule = SafeStrategyRule.DEFEND)
	public static AIStrategy run(
			AIHelper helper,
			@AICommandParameter(type = ParameterType.FIXED, fixedName = "plant", description = "") String nameArg,
			@AICommandParameter(type = ParameterType.FIXED, fixedName = "sugar_cane", description = "") String nameArg2) {
		return new PathFinderStrategy(
				new SugarCanePathFinder(), "Planting sugar cane");
	}

}
