package net.famzangl.minecraft.minebot.ai.commands;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.util.text.StringTextComponent;

import java.util.Collection;
import java.util.EnumSet;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EnumArgument<T extends Enum<T>> implements ArgumentType<T> {

    private final EnumSet<T> constants;

    public static  <T extends Enum<T>> EnumArgument<T> of(Class<T> allOf) {
        return of(EnumSet.allOf(allOf));
    }
    public static  <T extends Enum<T>> EnumArgument<T> of(EnumSet<T> set) {
        return new EnumArgument<>(set);
    }

    @SafeVarargs
    public static <T extends Enum<T>> EnumArgument<T> of(T first, T... remaining) {
        return of(EnumSet.of(first, remaining));
    }

    public EnumArgument(EnumSet<T> allOf) {
        if (allOf.size() == 0) {
            throw new IllegalArgumentException("At least one enum constant needs to be provided");
        }
        this.constants = EnumSet.copyOf(allOf);
    }


    @Override
    public T parse(StringReader reader) throws CommandSyntaxException {
        String value = reader.readUnquotedString();
        Optional<T> result = constants
                .stream()
                .filter(it -> it.name().equalsIgnoreCase(value))
                .findFirst();
        if (result.isPresent()) {
            return result.get();
        } else {
            throw new DynamicCommandExceptionType((value2) -> new StringTextComponent("Value not allowed for parameter: " + value)).create(value);
        }
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return ISuggestionProvider.suggest(constantNames(), builder);
    }

    @Override
    public Collection<String> getExamples() {
        return constantNames()
                .limit(5)
                .collect(Collectors.toList());
    }

    private Stream<String> constantNames() {
        return constants
                .stream()
                .map(it -> it.name().toLowerCase(Locale.US));
    }

    @Override
    public String toString() {
        return "EnumArgument{" +
                "constants=" + constants +
                '}';
    }
}
