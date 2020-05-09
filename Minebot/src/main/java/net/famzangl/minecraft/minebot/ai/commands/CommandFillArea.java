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
import com.mojang.brigadier.context.CommandContext;
import net.famzangl.minecraft.minebot.ai.command.AICommand;
import net.famzangl.minecraft.minebot.ai.command.CommandEvaluationException;
import net.famzangl.minecraft.minebot.ai.command.IAIControllable;
import net.famzangl.minecraft.minebot.ai.command.SafeStrategyRule;
import net.famzangl.minecraft.minebot.ai.path.FillAreaPathfinder;
import net.famzangl.minecraft.minebot.ai.path.world.WorldData;
import net.famzangl.minecraft.minebot.ai.strategy.PathFinderStrategy;
import net.famzangl.minecraft.minebot.ai.utils.BlockCuboid;
import net.famzangl.minecraft.minebot.build.commands.CommandClearArea;
import net.minecraft.block.BlockState;
import net.minecraft.command.arguments.BlockPosArgument;
import net.minecraft.command.arguments.BlockStateArgument;
import net.minecraft.command.arguments.BlockStateInput;

@AICommand(name = "minebuild", helpText = "Fill an area with a given block.")
public class CommandFillArea {
    public static void register(LiteralArgumentBuilder<IAIControllable> dispatcher, LiteralArgumentBuilder<IAIControllable> minebuild) {
        // /minebuild fill uses the area set by minebuild
        minebuild.then(
                Commands.literal("fill")
                        .then(
                                Commands.argument("block", BlockStateArgument.blockState())
                                        .executes(
                                                context -> {
                                                    BlockCuboid<WorldData> area = CommandClearArea.getArea(context.getSource().getAiHelper());
                                                    if (area != null) {
                                                        return requestUseStrategy(context, area);
                                                    } else {
                                                        throw new CommandEvaluationException("No area has been set yet. Set an area to fill using /minebot posN");
                                                    }
                                                }
                                        )
                        )
        );

        dispatcher.then(
                Commands.literal("fill")
                        // Minecraft syntax: /minebot [from] [to] [block]
                        .then(
                                Commands.argument("from", BlockPosArgument.blockPos()).then(
                                        Commands.argument("to", BlockPosArgument.blockPos()).then(
                                                Commands.argument("block", BlockStateArgument.blockState()).executes(
                                                        context -> requestUseStrategy(context, new BlockCuboid<>(
                                                                Commands.getBlockPos(context, "from"),
                                                                Commands.getBlockPos(context, "to")
                                                        ))
                                                )))

                        )
        );
    }

    private static int requestUseStrategy(CommandContext<IAIControllable> context, BlockCuboid<WorldData> area) {
        BlockState block = context.getArgument("block", BlockStateInput.class).getState();
        return context.getSource().requestUseStrategy(
                new PathFinderStrategy(
                        new FillAreaPathfinder(area, block),
                        "Filling with " + block.toString()),
                SafeStrategyRule.DEFEND);
    }
}