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
package net.famzangl.minecraft.minebot.build.commands;

import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.command.AICommand;
import net.famzangl.minecraft.minebot.ai.command.AICommandInvocation;
import net.famzangl.minecraft.minebot.ai.command.AICommandParameter;
import net.famzangl.minecraft.minebot.ai.command.ParameterType;
import net.famzangl.minecraft.minebot.ai.strategy.AIStrategy;
import net.famzangl.minecraft.minebot.ai.strategy.RunOnceStrategy;
import net.minecraft.util.math.BlockPos;

@AICommand(helpText = "Set bounding position manually.", name = "minebuild")
public class CommandSetPos {

	private static final class SetPositionStrategy extends RunOnceStrategy {
		private final BlockPos pos;
		private final boolean pos2;

		private SetPositionStrategy(BlockPos pos, boolean pos2) {
			this.pos = pos;
			this.pos2 = pos2;
		}

		@Override
		protected void singleRun(AIHelper helper) {
			helper.setPosition(pos == null ? helper.getPlayerPosition() : pos,
					pos2);
		}
	}

	@AICommandInvocation()
	public static AIStrategy run1(
			AIHelper helper,
			@AICommandParameter(type = ParameterType.FIXED, fixedName = "pos1", description = "") String nameArg,
			@AICommandParameter(type = ParameterType.POSITION, description = "The position", optional = true) BlockPos pos) {
		return setPos(pos, false);
	}

	@AICommandInvocation()
	public static AIStrategy run2(
			AIHelper helper,
			@AICommandParameter(type = ParameterType.FIXED, fixedName = "pos2", description = "") String nameArg,
			@AICommandParameter(type = ParameterType.POSITION, description = "The position", optional = true) BlockPos pos) {
		return setPos(pos, true);
	}

	private static AIStrategy setPos(final BlockPos pos, final boolean pos2) {
		return new SetPositionStrategy(pos, pos2);
	}
}
