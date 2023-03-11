package net.polar.utils;

import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * A class that can replace strings in a string with a function. Useful for replacing placeholders in strings.
 * @param <T> The type of the context object.
 */
public final class StringReplacer<T> {


    private final Map<String, Function<T, String>> replacements = new ConcurrentHashMap<>();


    /**
     * Adds a replacement to the replacer.
     * @param key The key to replace.
     * @param replacement The replacement function.
     */
    public void addReplacement(@NotNull String key, @NotNull Function<T, String> replacement) {
        replacements.put(key, replacement);
    }

    /**
     * Removes a replacement from the replacer.
     * @param key The key to remove.
     */
    public void removeReplacement(@NotNull String key) {
        replacements.remove(key);
    }

    /**
     * Clears all replacements from the replacer.
     */
    public void clearReplacements() {
        replacements.clear();
    }

    /**
     * Replaces all keys in the input string with the corresponding replacement.
     * @param input The input string.
     * @param context The context object.
     * @return The replaced string.
     */
    public String replace(@NotNull String input, @NotNull T context) {
        for (var replacement : replacements.entrySet()) {
            input.replace(replacement.getKey(), replacement.getValue().apply(context));
        }
        return input;
    }

}
