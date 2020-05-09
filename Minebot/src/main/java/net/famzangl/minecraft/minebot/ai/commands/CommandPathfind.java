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

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.famzangl.minecraft.minebot.ai.command.IAIControllable;
import net.famzangl.minecraft.minebot.ai.command.SafeStrategyRule;
import net.famzangl.minecraft.minebot.ai.path.GoToPathfinder;
import net.famzangl.minecraft.minebot.ai.strategy.PathFinderStrategy;
import net.minecraft.command.arguments.BlockPosArgument;
import net.minecraft.util.math.BlockPos;

public class CommandPathfind {
    public static void register(LiteralArgumentBuilder<IAIControllable> dispatcher) {
    	dispatcher.then(
				Commands.literal("pathfind").then(
						Commands.argument("to", BlockPosArgument.blockPos())
								.executes(context -> {
									BlockPos position = Commands.getBlockPos(context, "to");
									return context.getSource().requestUseStrategy(new PathFinderStrategy(new GoToPathfinder(position), "Go to "
											+ position.getX() + "," + position.getY() + "," + position.getZ()), SafeStrategyRule.DEFEND);
								})
				)
		);
	}
}
