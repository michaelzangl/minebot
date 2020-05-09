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
import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.command.AICommand;
import net.famzangl.minecraft.minebot.ai.command.IAIControllable;
import net.famzangl.minecraft.minebot.ai.command.SafeStrategyRule;
import net.famzangl.minecraft.minebot.ai.path.TreePathFinder;
import net.famzangl.minecraft.minebot.ai.strategy.PathFinderStrategy;
import net.famzangl.minecraft.minebot.build.block.WoodType;

@AICommand(helpText = "Gets wood", name = "minebot")
public class CommandLumberjack {
	public static class TreePathFinderStrategy extends PathFinderStrategy {

		public TreePathFinderStrategy(TreePathFinder pathFinder,
				String description) {
			super(pathFinder, description);
		}

		public TreePathFinderStrategy(WoodType type, boolean replant) {
			this(new TreePathFinder(type, replant), "Getting some " + (type == null ? "wood" : type.toString().toLowerCase()));
		}

		@Override
		public void searchTasks(AIHelper helper) {
			super.searchTasks(helper);
		}
	}

    public static void register(LiteralArgumentBuilder<IAIControllable> dispatcher) {
		dispatcher.then(
				Commands.optional(
						Commands.literal("lumberjack"),
						x -> null,
						"wood",
						EnumArgument.of(WoodType.class),
						WoodType.class,
						(builder, wood) ->
							builder.executes(context -> context.getSource().requestUseStrategy(
									new TreePathFinderStrategy(wood.get(context), false),
									SafeStrategyRule.DEFEND_MINING
							))
							.then(
									Commands.literal("replant").executes(context -> context.getSource().requestUseStrategy(
											new TreePathFinderStrategy(wood.get(context), true),
											SafeStrategyRule.DEFEND_MINING
									))
							)
				)
		);
	}

}
