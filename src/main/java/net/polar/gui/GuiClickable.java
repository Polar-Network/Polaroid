package net.polar.gui;

import net.minestom.server.entity.Player;
import net.minestom.server.inventory.click.ClickType;
import net.minestom.server.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public abstract class GuiClickable {

    private final ItemStack displayItem;

    public GuiClickable(@NotNull ItemStack displayItem) {
        this.displayItem = displayItem;
    }


    public abstract void onClick(@NotNull ClickType type, @NotNull Player player);


    public ItemStack getDisplayItem() {
        return displayItem;
    }

}
