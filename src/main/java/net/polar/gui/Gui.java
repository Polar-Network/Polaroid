package net.polar.gui;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.entity.Player;
import net.minestom.server.inventory.Inventory;
import net.minestom.server.inventory.InventoryType;
import net.minestom.server.inventory.click.ClickType;
import net.minestom.server.item.ItemHideFlag;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.polar.gui.pattern.BasicGuiPatterns;
import net.polar.gui.pattern.GuiPattern;
import net.polar.utils.chat.ChatColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Represents a GUI that can be opened and interacted by a player.
 */
public abstract class Gui extends Inventory {

    private final Map<Integer, GuiClickable> entries = new ConcurrentHashMap<>();

    private final Gui parent;
    private final GuiPattern pattern;

    /**
     * Forms a new GUI with the specified pattern and inventory type.
     * @param parent the parent GUI of this GUI. Null if this GUI has no parent.
     * @param pattern the pattern to use for this GUI
     * @param inventoryType the inventory type to use for this GUI
     * @param title the title of this GUI. This is defined a {@link String} since
     *              it automatically gets colorized by {@link ChatColor#color(String)}
     */
    public Gui(
            @Nullable Gui parent,
            @NotNull GuiPattern pattern,
            @NotNull InventoryType inventoryType,
            @NotNull String title
    ) {
        super(inventoryType, ChatColor.color(title));
        this.parent = parent;
        this.pattern = pattern;
        if (parent != null) {
            int lastRowFirstSlot = (inventoryType.getSize() - 9);
            addClickable(new GuiClickable(BACK_ITEM) {
                @Override
                public void onClick(@NotNull ClickType type, @NotNull Player player) {
                    parent.open(player);
                }
            }, lastRowFirstSlot);
        }

        int lastRowMiddleSlot = (inventoryType.getSize() - 5);
        addClickable(new GuiClickable(CLOSE_ITEM) {
            @Override
            public void onClick(@NotNull ClickType type, @NotNull Player player) {
                close(player);
            }
        }, lastRowMiddleSlot);
    }

    /**
     * Opens this GUI for the specified player.
     * @param player the player to open this GUI for
     */
    public void open(@NotNull Player player) {
        rebuildInventory();
        player.openInventory(this);
    }

    /**
     * Rebuilds the inventory based on the pattern and clickables.
     * This method is called when the GUI is opened.
     * You can call this method to update a clickable's display item or completely change it
     */
    public void rebuildInventory() {
        if (!pattern.getClass().isAssignableFrom(BasicGuiPatterns.class)) return;
        BasicGuiPatterns basicPattern = (BasicGuiPatterns) pattern;
        switch (basicPattern) {
            case NONE -> {
            }
            case FULL -> {
                for (int i = 0; i < getSize(); i++) {
                    setItemStack(i, FILLER_MAIN);
                }
            }
            case CHECKERBOARD -> {
                for (int i = 0; i < getSize(); i++) {
                    if (i % 2 == 0) {
                        setItemStack(i, FILLER_MAIN);
                    } else {
                        setItemStack(i, FILLER_SECONDARY);
                    }
                }
            }
        }
        buildClickables();
    }

    /**
     * Closes the GUI for the specified player.
     * @param player the player to close the GUI for
     */
    public void close(@NotNull Player player) {
        player.closeInventory();
    }

    /**
     * Adds a clickable to the GUI.
     * @param clickable the clickable to add
     * @param slot the slot to add the clickable to
     */
    public void addClickable(@NotNull GuiClickable clickable, int slot) {
        entries.put(slot, clickable);
    }

    /**
     * Clears all clickables from the GUI.
     */
    public void clearClickables() {
        entries.clear();
    }

    /**
     * @return the parent GUI of this GUI. Null if this GUI has no parent.
     */
    public @Nullable Gui getParent() {
        return parent;
    }

    /**
     * Gets the clickable at the specified slot.
     * @param slot the slot to get the clickable from
     * @return the clickable at the specified slot. Null if no clickable is present.
     */
    public @Nullable GuiClickable getClickableAt(int slot) {
        return entries.get(slot); // null if no entry
    }

    private void buildClickables() {
        entries.forEach((slot, clickable) -> {
            setItemStack(slot, clickable.getDisplayItem());
        });
    }


    /**
     * Represents the main filler item
     */
    public static final ItemStack FILLER_MAIN = ItemStack.builder(Material.GRAY_STAINED_GLASS_PANE)
            .displayName(Component.empty())
            .meta(meta -> meta.hideFlag(ItemHideFlag.HIDE_ATTRIBUTES))
            .build();

    /**
     * Represents the secondary filler item
     */
    public static final ItemStack FILLER_SECONDARY = ItemStack.builder(Material.BLACK_STAINED_GLASS_PANE)
            .displayName(Component.empty())
            .meta(meta -> meta.hideFlag(ItemHideFlag.HIDE_ATTRIBUTES))
            .build();

    private static final ItemStack CLOSE_ITEM = ItemStack.builder(Material.BARRIER)
            .displayName(Component.text("Close", NamedTextColor.RED))
            .lore(ChatColor.color("", "<gray>Click to close this menu."))
            .meta(meta -> meta.hideFlag(ItemHideFlag.HIDE_ATTRIBUTES))
            .build();

    private static final ItemStack BACK_ITEM = ItemStack.builder(Material.ARROW)
            .displayName(Component.text("Back", NamedTextColor.RED))
            .lore(ChatColor.color("", "<gray>Click to go back.", "<gray>To the previous menu."))
            .meta(meta -> meta.hideFlag(ItemHideFlag.HIDE_ATTRIBUTES))
            .build();

}
