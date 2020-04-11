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
import net.famzangl.minecraft.minebot.ai.strategy.KillAnimalsStrategy;
import net.minecraft.item.EnumDyeColor;

@AICommand(helpText = "Starts hitting animals.\nA filter can be given.\nAvoids animals of your team.", name = "minebot")
public class CommandKill {

	@AICommandInvocation(safeRule = SafeStrategyRule.DEFEND)
	public static AIStrategy run(
			AIHelper helper,
			@AICommandParameter(type = ParameterType.FIXED, fixedName = "kill", description = "") String nameArg,
			@AICommandParameter(type = ParameterType.ENUM, description = "Animal type", optional = true) AnimalyType type,
			@AICommandParameter(type = ParameterType.COLOR, description = "Color", optional = true) EnumDyeColor color,
			@AICommandParameter(type = ParameterType.NUMBER, description = "How many", optional = true) Integer count) {
		return new KillAnimalsStrategy(count == null ? -1 : count,
				type == null ? AnimalyType.ANY : type, color);
	}
}
