package net.famzangl.minecraft.minebot.ai.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.famzangl.minecraft.minebot.ai.command.AIChatController;
import net.famzangl.minecraft.minebot.ai.command.IAIControllable;
import net.famzangl.minecraft.minebot.ai.strategy.AIStrategy;

public class CommandStack {

    public static void register(LiteralArgumentBuilder<IAIControllable> dispatcher) {
        dispatcher.then(
                Commands.literal("stack")
                        .then(
                                Commands.literal("begin")
                                        .executes(CommandStack::runStart)
                        )
                        .then(
                                Commands.literal("done")
                                        .executes(CommandStack::runDone)
                        )
                        .then(
                                Commands.literal("abort")
                                        .executes(CommandStack::runAbort)
                        )
        );
    }

    private static int runStart(CommandContext<IAIControllable> context) {
        context.getSource().getStackBuilder().startCollecting();
        if (context.getSource().getStackBuilder().isCollecting() && context.getSource().getStackBuilder().hasCollectedAnyStrategies()) {
            AIChatController.addChatLine("Dropped all old stack contents.");
        }
        AIChatController.addChatLine("Enter the minebot commands you want to have stacked (run in parallel).");
        AIChatController.addChatLine("When done, enter: /minebot stack done");
        AIChatController.addChatLine("To abort, enter: /minebot stack abort");
        return 1;
    }

    private static int runDone(CommandContext<IAIControllable> context) {
        if (!context.getSource().getStackBuilder().hasCollectedAnyStrategies()) {
            AIChatController.addChatLine("No commands scheduled. Aborting");
            return 0;
        } else {
            AIStrategy newStrategy = context.getSource().getStackBuilder().getStrategy();
            return context.getSource().requestUseStrategy(newStrategy, context.getSource().getStackBuilder().getSafeRule());
        }
    }

    private static int runAbort(CommandContext<IAIControllable> context) {
        context.getSource().getStackBuilder().abort();
        if (context.getSource().getStackBuilder().isCollecting()) {
            AIChatController.addChatLine("Aborted");
        }
        return 0;
    }
}
