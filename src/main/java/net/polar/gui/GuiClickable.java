package net.polar.gui;

import net.minestom.server.entity.Player;
import net.minestom.server.inventory.click.ClickType;
import net.minestom.server.item.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * A clickable item in a GUI.
 */
public abstract class GuiClickable {

    private final ItemStack displayItem;

    /**
     * Constructs a new GuiClickable with the specified display item.
     * @param displayItem The item to display in the GUI.
     */
    public GuiClickable(@NotNull ItemStack displayItem) {
        this.displayItem = displayItem;
    }


    /**
     * Called when the item is clicked.
     * @param type The type of click.
     * @param player The player who clicked the item.
     */
    public abstract void onClick(@NotNull ClickType type, @NotNull Player player);


    /**
     * @return The item to display in the GUI.
     */
    public ItemStack getDisplayItem() {
        return displayItem;
    }

}
