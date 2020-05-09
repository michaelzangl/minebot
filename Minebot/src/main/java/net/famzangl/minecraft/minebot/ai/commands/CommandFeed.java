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
import net.famzangl.minecraft.minebot.ai.strategy.FeedAnimalsStrategy;
import net.minecraft.item.DyeColor;

@AICommand(helpText = "Feeds animals in range\n"
        + "The animals can be restricted by color.", name = "minebot")
public class CommandFeed {

    public static void register(LiteralArgumentBuilder<IAIControllable> dispatcher) {
        dispatcher.then(
                Commands.optional(
                        Commands.literal("feed"),
                        context -> null,
                        "animal",
                        EnumArgument.of(FeedAnimalsStrategy.FeedableType.class),
                        FeedAnimalsStrategy.FeedableType.class,
                        (builder, type) ->
                                Commands.optional(
                                        builder,
                                        context -> null,
                                        "color",
                                        EnumArgument.of(DyeColor.class),
                                        DyeColor.class,
                                        (builder2, color) ->
                                            builder2.executes(context -> context.getSource().requestUseStrategy(
                                                    new FeedAnimalsStrategy(color.get(context), type.get(context)),
                                                    SafeStrategyRule.DEFEND
                                            ))
                                )
                ));
    }
}
