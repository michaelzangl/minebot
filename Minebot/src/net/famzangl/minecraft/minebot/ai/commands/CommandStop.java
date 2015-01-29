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
import net.famzangl.minecraft.minebot.ai.strategy.StopInStrategy;
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
			@AICommandParameter(type = ParameterType.NUMBER, fixedName = "", description = "Seconds") int seconds) {
		return new StopInStrategy(seconds, false);
	}

	@AICommandInvocation()
	public static AIStrategy run(
			AIHelper helper,
			@AICommandParameter(type = ParameterType.FIXED, fixedName = "stop", description = "") String nameArg,
			@AICommandParameter(type = ParameterType.FIXED, fixedName = "after", description = "") String nameArg2,
			@AICommandParameter(type = ParameterType.NUMBER, fixedName = "", description = "Seconds") int seconds,
			@AICommandParameter(type = ParameterType.FIXED, fixedName = "force", description = "") String nameArg3) {
		return new StopInStrategy(seconds, true);
	}

	@AICommandInvocation()
	public static AIStrategy run(
			AIHelper helper,
			@AICommandParameter(type = ParameterType.FIXED, fixedName = "stop", description = "") String nameArg,
			@AICommandParameter(type = ParameterType.FIXED, fixedName = "on", description = "") String nameArg2,
			@AICommandParameter(type = ParameterType.FIXED, fixedName = "death", description = "") String nameArg3) {
		return new AIStrategy() {
			@Override
			public boolean checkShouldTakeOver(AIHelper helper) {
				return !helper.isAlive();
			}
			
			@Override
			public boolean takesOverAnyTime() {
				return true;
			}
			
			@Override
			protected TickResult onGameTick(AIHelper helper) {
				if (!helper.isAlive()) {
					return TickResult.ABORT;
				} else {
					return TickResult.NO_MORE_WORK;
				}
			}
		};
	}
}
