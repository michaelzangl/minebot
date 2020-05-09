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
import net.famzangl.minecraft.minebot.ai.command.AICommand;
import net.famzangl.minecraft.minebot.ai.command.IAIControllable;
import net.famzangl.minecraft.minebot.ai.command.SafeStrategyRule;
import net.famzangl.minecraft.minebot.ai.path.BuildWayPathfinder;
import net.famzangl.minecraft.minebot.ai.strategy.BuildWayStrategy;
import net.minecraft.util.Direction;

@AICommand(helpText = "Build a nice way", name = "minebot")
public class CommandBuildWay {

	public static void register(LiteralArgumentBuilder<IAIControllable> dispatcher) {
		dispatcher.then(
				Commands.optionalHorizontalDirection(
						Commands.literal("buildroad"),
						(builder2, direction) -> builder2.executes(context -> {
							Direction inDirection = direction.get(context);
							return context.getSource().requestUseStrategy(
									new BuildWayStrategy(BuildWayPathfinder.findContinue(inDirection, context.getSource().getAiHelper().getWorld())),
									SafeStrategyRule.DEFEND
							);
						})
				)
		);
	}
}
