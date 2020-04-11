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
import net.famzangl.minecraft.minebot.ai.strategy.AIStrategy;
import net.famzangl.minecraft.minebot.ai.strategy.FurnaceStrategy;
import net.famzangl.minecraft.minebot.ai.strategy.FurnaceStrategy.FurnaceTaskList;

@AICommand(helpText = "Put/get from furnace.", name = "minebot")
public class CommandFurnace {
	public enum FurnaceTodo {
		ALL(new FurnaceTaskList(true, true, true)), TAKE(new FurnaceTaskList(
				false, false, true)), PUTFUEL(new FurnaceTaskList(false, true,
				false)), PUTBURNABLE(new FurnaceTaskList(true, false, false));
		FurnaceTaskList list;

		private FurnaceTodo(FurnaceTaskList list) {
			this.list = list;
		}

		public FurnaceTaskList getTaskList() {
			return list;
		}
	}

	@AICommandInvocation(safeRule = SafeStrategyRule.DEFEND)
	public static AIStrategy run(
			AIHelper helper,
			@AICommandParameter(type = ParameterType.FIXED, fixedName = "furnace", description = "") String nameArg,
			@AICommandParameter(type = ParameterType.ENUM, description = "What to do with the furnace", optional = true) FurnaceTodo task) {
		if (task == null) {
			task = FurnaceTodo.ALL;
		}
		return new FurnaceStrategy(task.getTaskList());
	}

}
