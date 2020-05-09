package net.famzangl.minecraft.minebot.ai.commands;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.famzangl.minecraft.minebot.ai.command.IAIControllable;
import net.minecraft.command.arguments.ILocationArgument;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

public class Commands {

    private Commands() {}

    public static LiteralArgumentBuilder<IAIControllable> literal(String name) {
        return LiteralArgumentBuilder.literal(name);
    }

    public static <T> RequiredArgumentBuilder<IAIControllable, T> argument(String name, ArgumentType<T> type) {
        return RequiredArgumentBuilder.argument(name, type);
    }

    public static <B extends ArgumentBuilder<IAIControllable, ?>> B optionalHorizontalDirection(
            B parent,
            BiConsumer<ArgumentBuilder<IAIControllable, ?>, ArgExecutorSupplier<Direction>> next
    ) {
        return optional(parent,
                context -> context.getSource().getAiHelper().getLookDirection(),
                "direction",
                EnumArgument.of(Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST),
                Direction.class,
                next);
    }

    public static <B extends ArgumentBuilder<IAIControllable, ?>, T> B optional(
            B parent,
            Function<CommandContext<IAIControllable>, T> defaultValue,
            String argName,
            ArgumentType<T> argT,
            Class<T> argTClass,
            BiConsumer<ArgumentBuilder<IAIControllable, ?>, ArgExecutorSupplier<T>> next
    ) {
        return optional(parent, defaultValue, argName, argT, argTClass, (it, __) -> it, next);
    }

    public static <B extends ArgumentBuilder<IAIControllable, ?>, T, ArgT> B optional(
            B parent,
            Function<CommandContext<IAIControllable>, T> defaultValue,
            String argName,
            ArgumentType<ArgT> argT,
            Class<ArgT> argTClass,
            BiFunction<ArgT, IAIControllable, T> argExtractor,
            BiConsumer<ArgumentBuilder<IAIControllable, ?>, ArgExecutorSupplier<T>> next
    ) {
        // Without parameter
        next.accept(parent, defaultValue::apply);

        // With the parameter
        RequiredArgumentBuilder<IAIControllable, ArgT> arg = argument(argName, argT);
        next.accept(arg, context -> argExtractor.apply(context.getArgument(argName, argTClass), context.getSource()));
        parent.then(arg);

        return parent;
    }

    public static BlockPos getBlockPos(CommandContext<IAIControllable> context, String parameterName) {
        return context.getArgument(parameterName, ILocationArgument.class).getBlockPos(context.getSource().getMinecraft().player.getCommandSource());
    }

    @FunctionalInterface
    public interface ArgExecutorSupplier<T> {
        public T get(CommandContext<IAIControllable> context);
    }

    public static void register(LiteralArgumentBuilder<IAIControllable> minebot,
                                LiteralArgumentBuilder<IAIControllable> minebuild) {
        CommandAirbridge.register(minebot);
        CommandBuildRail.register(minebot);
        CommandBuildWay.register(minebot);
        CommandEat.register(minebot);
        CommandFeed.register(minebot);
        CommandFish.register(minebot);
        CommandFillArea.register(minebot, minebuild);
        CommandPathfind.register(minebot);
        CommandLumberjack.register(minebot);
        CommandMine.register(minebot);
        CommandPathfind.register(minebot);
    }
}
