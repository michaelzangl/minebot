package net.famzangl.minecraft.minebot.ai.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.famzangl.minecraft.minebot.ai.command.AICommand;
import net.famzangl.minecraft.minebot.ai.command.IAIControllable;
import net.famzangl.minecraft.minebot.ai.command.SafeStrategyRule;
import net.famzangl.minecraft.minebot.ai.strategy.AirbridgeStrategy;
import net.minecraft.command.arguments.ILocationArgument;
import net.minecraft.command.arguments.Vec3Argument;

@AICommand(helpText = "Builds an airbridge using the half-slabs in your inventory.", name = "minebot")
public class CommandAirbridge {
    public static void register(LiteralArgumentBuilder<IAIControllable> dispatcher) {
        dispatcher.then(
                Commands.optional(
                        Commands.literal("airbridge"),
                        context -> context.getSource().getAiHelper().getPlayerPosition(),
                        "position",
                        Vec3Argument.vec3(),
                        ILocationArgument.class,
                        (value, context) -> value.getBlockPos(context.getMinecraft().player.getCommandSource()),
                        (builder, pos) ->
                                Commands.optionalHorizontalDirection(
                                        builder,
                                        (builder2, direction) ->
                                                Commands.optional(
                                                        builder2,
                                                        context -> AirbridgeWidth.SMALL,
                                                        "width",
                                                        EnumArgument.of(AirbridgeWidth.class),
                                                        AirbridgeWidth.class,
                                                        (builder3, width) ->
																builder3.executes(context -> context.getSource().requestUseStrategy(
                                                                        new AirbridgeStrategy(pos.get(context),
                                                                                direction.get(context), -1,
                                                                                width.get(context).toLeft, width.get(context).toRight)
                                                                        , SafeStrategyRule.DEFEND)
                                                                )
                                                ))));

    }

    public enum AirbridgeWidth {
        SMALL(0, 0),
        WIDE(1, 1),
        WIDER(2, 2),
        MAXIMUM(3, 3);

        private final int toLeft, toRight;

        private AirbridgeWidth(int toLeft, int toRight) {
            this.toLeft = toLeft;
            this.toRight = toRight;
        }
    }

}
