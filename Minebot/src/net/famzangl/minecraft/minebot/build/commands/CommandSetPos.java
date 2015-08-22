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

import net.famzangl.minecraft.minebot.Pos;
import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.command.AICommand;
import net.famzangl.minecraft.minebot.ai.command.AICommandInvocation;
import net.famzangl.minecraft.minebot.ai.command.AICommandParameter;
import net.famzangl.minecraft.minebot.ai.command.ParameterType;
import net.famzangl.minecraft.minebot.ai.strategy.AIStrategy;
import net.minecraft.util.BlockPos;

@AICommand(helpText = "Set bounding position manually.", name = "minebuild")
public class CommandSetPos {

	@AICommandInvocation()
	public static AIStrategy run1(
			AIHelper helper,
			@AICommandParameter(type = ParameterType.FIXED, fixedName = "pos1", description = "") String nameArg,
			@AICommandParameter(type = ParameterType.POSITION, description = "The position", optional = true) BlockPos pos) {
		setPos(helper, pos, false);
		return null;
	}

	@AICommandInvocation()
	public static AIStrategy run2(
			AIHelper helper,
			@AICommandParameter(type = ParameterType.FIXED, fixedName = "pos2", description = "") String nameArg,
			@AICommandParameter(type = ParameterType.POSITION, description = "The position", optional = true) BlockPos pos) {
		setPos(helper, pos, true);
		return null;
	}

	private static void setPos(AIHelper helper, BlockPos pos, boolean b) {
		helper.setPosition(pos == null ? helper.getPlayerPosition() : pos, b);
	}
}
