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
import net.famzangl.minecraft.minebot.ai.path.TunnelPathFinder;
import net.famzangl.minecraft.minebot.ai.path.TunnelPathFinder.TorchSide;
import net.famzangl.minecraft.minebot.ai.strategy.AIStrategy;
import net.famzangl.minecraft.minebot.ai.strategy.PathFinderStrategy;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumFacing;

@AICommand(helpText = "Build a tunnel with the given profile", name = "minebot")
public class CommandTunnel {
	@AICommandInvocation(safeRule = SafeStrategyRule.DEFEND_MINING)
	public static AIStrategy run(
			AIHelper helper,
			@AICommandParameter(type = ParameterType.FIXED, fixedName = "tunnel", description = "") String nameArg,
			@AICommandParameter(type = ParameterType.ENUM, description = "direction", optional = true) EnumFacing inDirection,
			@AICommandParameter(type = ParameterType.FIXED, fixedName = "branches", description = "add sideward branches", optional = true) String sidewardBranches,
			@AICommandParameter(type = ParameterType.ENUM, description = "torch side", optional = true) TorchSide torches,
			@AICommandParameter(type = ParameterType.NUMBER, description = "max length", optional = true) Integer length) {
		return run(helper, nameArg, inDirection, sidewardBranches != null ? -1
				: 0, 0, torches, length);
	}

	@AICommandInvocation(safeRule = SafeStrategyRule.DEFEND_MINING)
	public static AIStrategy run(
			AIHelper helper,
			@AICommandParameter(type = ParameterType.FIXED, fixedName = "tunnel", description = "") String nameArg,
			@AICommandParameter(type = ParameterType.ENUM, description = "direction", optional = true) EnumFacing inDirection,
			@AICommandParameter(type = ParameterType.NUMBER, description = "add to side") int addToSide,
			@AICommandParameter(type = ParameterType.NUMBER, description = "add to top") int addToTop,
			@AICommandParameter(type = ParameterType.ENUM, description = "torch side", optional = true) TorchSide torches,
			@AICommandParameter(type = ParameterType.NUMBER, description = "max length", optional = true) Integer length) {
		if (inDirection == null) {
			inDirection = helper.getLookDirection();
		}

		if (torches == null) {
			torches = TorchSide.NONE;
		}

		if (length == null) {
			length = -1;
		}

		final BlockPos pos = helper.getPlayerPosition();
		final TunnelPathFinder tunnel = new TunnelPathFinder(
				inDirection.getFrontOffsetX(), inDirection.getFrontOffsetZ(),
				pos.getX(), pos.getY(), pos.getZ(), addToSide, addToTop,
				torches, length);
		return new PathFinderStrategy(tunnel, null) {
			public String getDescription(AIHelper helper) {
				return "Tunneling " + tunnel.getProgress();
			}
		};
	}
}
