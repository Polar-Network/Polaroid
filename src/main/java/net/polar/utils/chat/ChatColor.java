package net.polar.utils.chat;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minestom.server.item.ItemStack;
import net.minestom.server.utils.validate.Check;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

/**
 * A utility class for handling {@link Component}s.
 */
public final class ChatColor {

    private ChatColor() {}
    private static MiniMessage miniMessage = MiniMessage.miniMessage();

    /**
     * Translates a string into a {@link Component}.
     * @param text The string to translate.
     * @return The translated {@link Component}.
     */
    public static @NotNull Component color(@NotNull String text) {
        return miniMessage.deserialize(text);
    }

    /**
     * Translates a {@link Component} into a string.
     * @param component The {@link Component} to translate.
     * @return The translated string.
     */
    public static @NotNull String reverse(@NotNull Component component) {
        Check.argCondition(!(component instanceof TextComponent), "Component must be a TextComponent");
        return ((TextComponent) component).content();
    }

    /**
     * Translates a list of strings into a list of {@link Component}s.
     * @param text The list of strings to translate.
     * @return The translated list of {@link Component}s.
     */
    public static @NotNull List<Component> color(@NotNull List<String> text) {
        return text.stream().map(ChatColor::color).collect(Collectors.toList());
    }

    /**
     * Translates an array of strings into a list of {@link Component}s.
     * @param text The array of strings to translate.
     * @return The translated list of {@link Component}s.
     */
    public static @NotNull List<Component> color (@NotNull String... text) {
        return color(List.of(text));
    }


    /**
     * Translates a list of {@link Component}s into a list of strings.
     * @param components The list of {@link Component}s to translate.
     * @return The translated list of strings.
     */
    public static @NotNull List<String> reverse(@NotNull List<Component> components) {
        return components.stream().map(ChatColor::reverse).collect(Collectors.toList());
    }

    /**
     * Translates an array of {@link Component}s into a list of strings.
     * @param components The array of {@link Component}s to translate.
     * @return The translated list of strings.
     */
    public static @NotNull List<String> reverse(@NotNull Component... components) {
        return reverse(List.of(components));
    }

    /**
     * Creates a component that represents an {@link ItemStack}, useful for players to show off items in chat.
     * @param item the item to represent
     * @return the component
     */
    public static @NotNull Component composeItem(@NotNull ItemStack item) {
        Component component = Component.empty();
        if (item.getDisplayName() == null) {
            component = component.append(Component.text(item.material().name().replaceAll("_", " "), NamedTextColor.GRAY));
        }
        else {
            component = component.append(item.getDisplayName());
        }
        if (!item.getLore().isEmpty()) {
            component = component.append(Component.newline());
            for (Component lore : item.getLore()) {
                component = component.append(lore);
            }
        }
        return component;
    }

    /**
     * Replaces the default MiniMessage object
     * @param replacement the object to replace to
     */
    public static void replaceHandler(@NotNull MiniMessage replacement) {
        miniMessage = replacement;
    }

}
