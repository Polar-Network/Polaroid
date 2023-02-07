package net.polar.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minestom.server.utils.validate.Check;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

public final class ChatColor {

    private ChatColor() {}
    private static final MiniMessage miniMessage = MiniMessage.miniMessage();
    
    public static @NotNull Component color(@NotNull String text) {
        return miniMessage.deserialize(text);
    }

    public static @NotNull String reverse(@NotNull Component component) {
        Check.argCondition(!(component instanceof TextComponent), "Component must be a TextComponent");
        return ((TextComponent) component).content();
    }

    public static @NotNull List<Component> color(@NotNull List<String> text) {
        return text.stream().map(ChatColor::color).collect(Collectors.toList());
    }

    public static @NotNull List<Component> color (@NotNull String... text) {
        return color(List.of(text));
    }

    public static @NotNull List<String> reverse(@NotNull List<Component> components) {
        return components.stream().map(ChatColor::reverse).collect(Collectors.toList());
    }

    public static @NotNull List<String> reverse(@NotNull Component... components) {
        return reverse(List.of(components));
    }

}
