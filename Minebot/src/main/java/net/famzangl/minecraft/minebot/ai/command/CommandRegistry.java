package net.famzangl.minecraft.minebot.ai.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import net.famzangl.minecraft.minebot.ai.AIHelper;
import net.famzangl.minecraft.minebot.ai.commands.Commands;
import net.famzangl.minecraft.minebot.ai.strategy.AIStrategy;
import net.minecraft.command.ISuggestionProvider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class CommandRegistry {
	private static final Marker MARKER_REGISTER = MarkerManager.getMarker("register");
    private static final Logger LOGGER = LogManager.getLogger(CommandRegistry.class);
    private IAIControllable controllable;
    private final CommandDispatcher<IAIControllable> commands = new CommandDispatcher<>();

    public CommandRegistry() {
        LiteralArgumentBuilder<IAIControllable> minebot = Commands.literal("minebot");
        LiteralArgumentBuilder<IAIControllable> minebuild = Commands.literal("minebuild");
        Commands.register(minebot, minebuild);
        commands.register(minebot);
        commands.register(minebuild);

        if (LOGGER.isDebugEnabled(MARKER_REGISTER)) {
            LOGGER.debug(MARKER_REGISTER, "Commands that are registered:");
            debugCommands(commands.getRoot(), 0);
        }
    }

    private void debugCommands(CommandNode<IAIControllable> root, int depth) {
        String prefix = IntStream.range(0, depth).mapToObj(it -> "  ").collect(Collectors.joining());
        root.getChildren().forEach(child -> {
            String commandDescr = "?";
            if (child instanceof LiteralCommandNode) {
                commandDescr = ((LiteralCommandNode<IAIControllable>) child).getLiteral();
            } else if (child instanceof ArgumentCommandNode) {
                commandDescr = "[" + child.getName() + "] of type " + ((ArgumentCommandNode) child).getType();
            }
            LOGGER.debug(MARKER_REGISTER, "{}{}", prefix, commandDescr);

            debugCommands(child, depth + 1);
        });
    }

    public IAIControllable getControlled() {
        if (controllable == null) {
            throw new IllegalStateException("Controllable not set");
        }
        return controllable;
    }

    public void setControlled(IAIControllable controllable) {
        Objects.requireNonNull(controllable, "controllable");
        this.controllable = controllable;
    }

    public AIStrategy evaluateCommand(AIHelper helper, String command) throws CommandSyntaxException {
        StrategyReceiver receiver = new StrategyReceiver(helper);
        commands.execute(command, receiver);
        return receiver.get();
    }

    public AIStrategy evaluateCommandWithSaferule(AIHelper helper, String command) throws CommandSyntaxException {
        StrategyReceiver receiver = new StrategyReceiver(helper);
        commands.execute(command, receiver);
        return receiver.get();
    }

    public boolean interceptCommand(String message) {
        try {
            StringReader reader = new StringReader(message);
            if (reader.canRead()) {
                char first = reader.read();
                if (first == '/') {
                    commands.execute(reader, controllable);
                    return true;
                }
            }
        } catch (Throwable e) {
            // Do not send on to server if message starts with /minebot
            for (CommandNode<IAIControllable> literal : commands.getRoot().getChildren()) {
                if (message.startsWith("/" + ((LiteralCommandNode<IAIControllable>) literal).getLiteral())) {
                    // Show chat message to client. TODO: Use nice minecraft message
                    AIChatController.addChatLine("ERROR while evaluating: "
                            + e.getMessage());
                    LOGGER.warn("Error during command evaluation", e);
                }
            }
            // Otherwise ignored, let Minecraft handle this
        }
        return false;
    }

    public void addCommandsTo(RootCommandNode<ISuggestionProvider> root) {
        Collection<CommandNode<IAIControllable>> toConvert = commands.getRoot().getChildren();
        convertInto(root, toConvert);
    }

    private void convertInto(CommandNode<ISuggestionProvider> into, Collection<CommandNode<IAIControllable>> toConvert) {
        toConvert.forEach(
                child -> {
                    CommandNode<ISuggestionProvider> node;
                    if (child.getRedirect() != null) {
                        throw new IllegalArgumentException("Cannot redirect");
                    }
                    if (child instanceof LiteralCommandNode) {
                        LiteralCommandNode<IAIControllable> literal = (LiteralCommandNode<IAIControllable>) child;
                        node = new LiteralCommandNode<>(literal.getLiteral(),
                                suggestionContext -> literal.getCommand().run(convertCommandContext(suggestionContext)),
                                suggestionContext -> literal.getRequirement().test(controllable),
                                null,
                                null,
                                literal.isFork());
                    } else if (child instanceof ArgumentCommandNode){
                        ArgumentCommandNode<IAIControllable, ?> argumentNode = (ArgumentCommandNode<IAIControllable, ?>) child;

                        node = new ArgumentCommandNode<>(
                                argumentNode.getName(),
                                argumentNode.getType(),
                                suggestionContext -> argumentNode.getCommand().run(convertCommandContext(suggestionContext)),
                                suggestionContext -> argumentNode.getRequirement().test(controllable),
                                null,
                                null,
                                argumentNode.isFork(),
                                argumentNode.getCustomSuggestions() == null ? null : (suggestionContext, builder) -> argumentNode.getCustomSuggestions().getSuggestions(convertCommandContext(suggestionContext), builder)

                        );
                    } else {
                        throw new IllegalStateException("Could not find node type " + child.getClass().getSimpleName());
                    }

                    convertInto(node, child.getChildren());

                    into.addChild(node);
                }
        );
    }

    private CommandContext<IAIControllable> convertCommandContext(CommandContext<ISuggestionProvider> suggestionContext) {
        // Generics get erased on runtime => this works
        return ((CommandContext<IAIControllable>) (CommandContext<?>) suggestionContext).copyFor(controllable);
    }

    private static class StrategyReceiver implements IAIControllable {
        private final AIHelper helper;
        AIStrategy strategy;

        public StrategyReceiver(AIHelper helper) {
            this.helper = helper;
            strategy = null;
        }

        @Override
        public AIHelper getAiHelper() {
            return helper;
        }

        @Override
        public int requestUseStrategy(AIStrategy strategy) {
            return 0;
        }

        public AIStrategy get() {
            if (strategy == null) {
                throw new IllegalStateException("No strategy has been set");
            } else {
                return strategy;
            }
        }
    }
}
