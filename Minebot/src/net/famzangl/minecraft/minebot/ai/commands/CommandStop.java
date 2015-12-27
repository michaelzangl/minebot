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
import net.famzangl.minecraft.minebot.ai.strategy.AIStrategy;
import net.famzangl.minecraft.minebot.ai.strategy.InventoryDefinition;
import net.famzangl.minecraft.minebot.ai.strategy.StopInStrategy;
import net.famzangl.minecraft.minebot.ai.strategy.StopOnConditionStrategy;
import net.famzangl.minecraft.minebot.ai.strategy.StopOnConditionStrategy.StopCondition;
import net.famzangl.minecraft.minebot.ai.strategy.StopStrategy;

@AICommand(name = "minebot", helpText = "Stop whatever you are doing.")
public class CommandStop {
	@AICommandInvocation()
	public static AIStrategy run(
			AIHelper helper,
			@AICommandParameter(type = ParameterType.FIXED, fixedName = "stop", description = "") String nameArg) {
		return new StopStrategy();
	}

	@AICommandInvocation()
	public static AIStrategy run(
			AIHelper helper,
			@AICommandParameter(type = ParameterType.FIXED, fixedName = "stop", description = "") String nameArg,
			@AICommandParameter(type = ParameterType.FIXED, fixedName = "after", description = "") String nameArg2,
			@AICommandParameter(type = ParameterType.NUMBER, description = "Seconds") int seconds,
			@AICommandParameter(type = ParameterType.FIXED, fixedName = "force", description = "", optional = true) String force) {
		return new StopInStrategy(seconds, force != null);
	}

	@AICommandInvocation()
	public static AIStrategy run(
			AIHelper helper,
			@AICommandParameter(type = ParameterType.FIXED, fixedName = "stop", description = "") String nameArg,
			@AICommandParameter(type = ParameterType.FIXED, fixedName = "on", description = "") String nameArg2,
			@AICommandParameter(type = ParameterType.FIXED, fixedName = "death", description = "") String nameArg3,
			@AICommandParameter(type = ParameterType.FIXED, fixedName = "force", description = "ignored", optional = true) String ignored) {
		return new StopOnConditionStrategy(new StopCondition() {
			@Override
			public boolean shouldStop(AIHelper helper) {
				return !helper.isAlive();
			}
		}, true, "death");
	}

	@AICommandInvocation()
	public static AIStrategy runInventory(
			AIHelper helper,
			@AICommandParameter(type = ParameterType.FIXED, fixedName = "stop", description = "") String nameArg,
			@AICommandParameter(type = ParameterType.FIXED, fixedName = "on", description = "") String nameArg2,
			@AICommandParameter(type = ParameterType.FIXED, fixedName = "fullinv", description = "") String nameArg3,
			@AICommandParameter(type = ParameterType.FIXED, fixedName = "force", description = "", optional = true) String force) {
		return new StopOnConditionStrategy(new StopCondition() {
			@Override
			public boolean shouldStop(AIHelper helper) {
				InventoryDefinition inventory = new InventoryDefinition(helper.getMinecraft().thePlayer.inventory);
				return inventory.searchFreeSlot() < 0;
			}
		}, force != null, "full inventory");
	}
}
