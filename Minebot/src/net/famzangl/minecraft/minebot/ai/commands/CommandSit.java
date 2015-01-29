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
import net.famzangl.minecraft.minebot.ai.animals.AnimalyType;
import net.famzangl.minecraft.minebot.ai.command.AICommand;
import net.famzangl.minecraft.minebot.ai.command.AICommandInvocation;
import net.famzangl.minecraft.minebot.ai.command.AICommandParameter;
import net.famzangl.minecraft.minebot.ai.command.ParameterType;
import net.famzangl.minecraft.minebot.ai.command.SafeStrategyRule;
import net.famzangl.minecraft.minebot.ai.strategy.AIStrategy;
import net.famzangl.minecraft.minebot.ai.strategy.LetAnimalsSitStrategy;
import net.minecraft.item.EnumDyeColor;

@AICommand(helpText = "Lets all dogs either sit or stand.", name = "minebot")
public class CommandSit {

	@AICommandInvocation(safeRule = SafeStrategyRule.DEFEND)
	public static AIStrategy runSit(
			AIHelper helper,
			@AICommandParameter(type = ParameterType.FIXED, fixedName = "sit", description = "") String nameArg,
			@AICommandParameter(type = ParameterType.COLOR, description = "The color of wolfes to feed.", optional = true) EnumDyeColor color) {
		return new LetAnimalsSitStrategy(AnimalyType.WOLF, true, color);
	}

	@AICommandInvocation(safeRule = SafeStrategyRule.DEFEND)
	public static AIStrategy runUnSit(
			AIHelper helper,
			@AICommandParameter(type = ParameterType.FIXED, fixedName = "unsit", description = "") String nameArg,
			@AICommandParameter(type = ParameterType.COLOR, description = "The color of wolfes to feed.", optional = true) EnumDyeColor color) {
		return new LetAnimalsSitStrategy(AnimalyType.WOLF, false, color);
	}
}
