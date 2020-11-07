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
import net.famzangl.minecraft.minebot.ai.path.MineBySettingsPathFinder;
import net.famzangl.minecraft.minebot.ai.path.MineNerbyPathFinder;
import net.famzangl.minecraft.minebot.ai.path.MineSinglePathFinder;
import net.famzangl.minecraft.minebot.ai.path.OrebfuscatedMinePathFinder;
import net.famzangl.minecraft.minebot.ai.path.world.BlockSet;
import net.famzangl.minecraft.minebot.ai.path.world.BlockSets;
import net.famzangl.minecraft.minebot.ai.strategy.PathFinderStrategy;
import net.minecraft.block.Blocks;
import net.minecraft.command.arguments.BlockStateArgument;
import net.minecraft.command.arguments.BlockStateInput;

@AICommand(helpText = "Mines for ores.\n"
		+ "Uses the minebot.properties file to find ores."
		+ "If blockName is given, only the block that is given is searched for.", name = "minebot")
public class CommandMine {

	public static BlockSet MINEABLE = BlockSet.builder().add(
			Blocks.LAVA, Blocks.WATER,
			Blocks.LILY_PAD, Blocks.BEDROCK).add(BlockSets.AIR).build().invert();

	private static final class MineNearbyStrategy extends
			PathFinderStrategy {
		private MineNerbyPathFinder nearby;

		private MineNearbyStrategy(MineNerbyPathFinder pathFinder,
								   String description) {
			super(pathFinder, description);
			nearby = pathFinder;
		}

		@Override
		protected void onActivate(AIHelper helper) {
			nearby.setActivationPoint(helper.getPlayerPosition());
			super.onActivate(helper);
		}

		@Override
		protected void onDeactivate(AIHelper helper) {
			nearby.setActivationPoint(null);
			super.onDeactivate(helper);
		}
	}

	public static void register(LiteralArgumentBuilder<IAIControllable> dispatcher) {
		dispatcher.then(
				Commands.literal("mine")
						// /minebot mine
						.executes(context -> context.getSource().requestUseStrategy(new PathFinderStrategy(new MineBySettingsPathFinder(
								context.getSource().getAiHelper().getLookDirection(), context.getSource().getAiHelper().getPlayerPosition().getY()),
								"Mining ores"), SafeStrategyRule.DEFEND_MINING))
						// /minebot mine <what>
						.then(Commands.argument("block", BlockStateArgument.blockState())
								.executes(context -> context.getSource().requestUseStrategy(new PathFinderStrategy(
										new MineSinglePathFinder(BlockSet.builder().add(context.getArgument("block", BlockStateInput.class).getState()).build(),
												context.getSource().getAiHelper().getLookDirection(), context.getSource().getAiHelper().getPlayerPosition().getY()),
										"Mining " + context.getArgument("block", BlockStateInput.class).getState().getBlock().toString()), SafeStrategyRule.DEFEND_MINING)))
						// /minebot mine orebfuscated
						.then(
								Commands.literal("orebfuscated").executes(
										context -> context.getSource().requestUseStrategy(new PathFinderStrategy(new OrebfuscatedMinePathFinder(
														context.getSource().getAiHelper().getLookDirection(), context.getSource().getAiHelper().getPlayerPosition().getY()),
														"Mining ores (orebfuscated)")
												, SafeStrategyRule.DEFEND_MINING)))
										// /minebot mine nearbyor

										.then(
												Commands.literal("nearby").executes(
														context -> context.getSource().requestUseStrategy(new MineNearbyStrategy(new MineNerbyPathFinder(
																		context.getSource().getAiHelper().getLookDirection()), "Mining nearby ores")
																, SafeStrategyRule.DEFEND_MINING))
										));

	}
}
