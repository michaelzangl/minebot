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

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.command.IAIControllable;
import net.famzangl.minecraft.minebot.ai.command.SafeStrategyRule;
import net.famzangl.minecraft.minebot.ai.path.TunnelPathFinder;
import net.famzangl.minecraft.minebot.ai.path.TunnelPathFinder.TorchSide;
import net.famzangl.minecraft.minebot.ai.strategy.PathFinderStrategy;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;

import java.util.EnumSet;

/**
 * Build a tunnel with the given profile
 */
public class CommandTunnel {
    enum TunnelModeEnum implements TunnelPathFinder.TunnelMode {
        TUNNEL_1x2(0, 0),
        TUNNEL_1x3(0, 1),
        TUNNEL_1x4(0, 2),
        TUNNEL_1x5(0, 3),
        TUNNEL_1x6(0, 4),
        TUNNEL_1x7(0, 5),
        TUNNEL_3x2(1, 0),
        TUNNEL_3x3(1, 1),
        TUNNEL_3x4(1, 2),
        TUNNEL_3x5(1, 3),
        TUNNEL_3x6(1, 4),
        TUNNEL_5x2(2, 0),
        TUNNEL_5x3(2, 1),
        TUNNEL_5x4(2, 2),
        TUNNEL_5x5(2, 3),
        TUNNEL_5x6(2, 4),
        TUNNEL_7x2(3, 0),
        TUNNEL_7x3(3, 1),
        TUNNEL_7x4(3, 2),
        TUNNEL_7x5(3, 3),
        TUNNEL_9x2(4, 0),
        TUNNEL_9x3(4, 1),
        TUNNEL_9x4(4, 2),
        TUNNEL_BRANCHES(true);

        private final int addToSide;
        private final int addToTop;
        private final boolean branches;

        TunnelModeEnum(int addToSide, int addToTop) {
            this.addToSide = addToSide;
            this.addToTop = addToTop;
            this.branches = false;
        }

        TunnelModeEnum(boolean branches) {
            this.addToSide = 0;
            this.addToTop = 0;
            this.branches = branches;
        }

        @Override
        public int getAddToSide() {
            return addToSide;
        }

        @Override
        public int getAddToTop() {
            return addToTop;
        }

        @Override
        public boolean addBranches() {
            return branches;
        }
    }

    public static void register(LiteralArgumentBuilder<IAIControllable> dispatcher) {
        dispatcher.then(Commands.optional(
                Commands.literal("tunnel"),
                context -> context.getSource().getAiHelper().getLookDirection(),
                "direction",
                EnumArgument.of(Direction.EAST, Direction.NORTH, Direction.WEST, Direction.SOUTH),
                Direction.class,
                (builder, direction) ->
                        Commands.optional(builder,
                                __ -> TunnelModeEnum.TUNNEL_1x2,
                                "mode",
                                EnumArgument.of(EnumSet.allOf(TunnelModeEnum.class), e -> e.name().replace("TUNNEL_", "")),
                                TunnelModeEnum.class,

                                (builder2, tunnelMode) ->
                                        Commands.optional(builder2,
                                                __ -> TorchSide.TORCH_NONE,
                                                "torch_side",
                                                EnumArgument.of(TorchSide.class),
                                                TorchSide.class,
                                                (builder3, torchSide) ->
                                                        Commands.optional(builder3,
                                                                __ -> -1,
                                                                "length",
                                                                IntegerArgumentType.integer(1),
                                                                Integer.class,
                                                                (builder4, length) -> builder4.executes(context -> {
                                                                    Direction inDirection = direction.get(context);
                                                                    final BlockPos pos = context.getSource().getAiHelper().getPlayerPosition();
                                                                    final TunnelPathFinder tunnel = new TunnelPathFinder(
                                                                            inDirection.getXOffset(), inDirection.getZOffset(),
                                                                            pos.getX(), pos.getY(), pos.getZ(), tunnelMode.get(context),
                                                                            torchSide.get(context), length.get(context));
                                                                    return context.getSource().requestUseStrategy(new PathFinderStrategy(tunnel, null) {
                                                                        public String getDescription(AIHelper helper) {
                                                                            return "Tunneling " + tunnel.getProgress();
                                                                        }
                                                                    }, SafeStrategyRule.DEFEND_MINING);
                                                                })
                                                        )
                                        ))
        ));
    }
}
