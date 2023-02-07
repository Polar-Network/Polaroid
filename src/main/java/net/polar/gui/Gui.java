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
import net.polar.utils.ChatColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class Gui extends Inventory {

    private final Map<Integer, GuiClickable> entries = new ConcurrentHashMap<>();

    private final Gui parent;
    private final GuiPattern pattern;

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

    public void open(@NotNull Player player) {
        rebuildInventory();
        player.openInventory(this);
    }

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

    public void close(@NotNull Player player) {
        player.closeInventory();
    }

    public void addClickable(@NotNull GuiClickable clickable, int slot) {
        entries.put(slot, clickable);
    }

    public void clearClickables() {
        entries.clear();
    }

    public @Nullable Gui getParent() {
        return parent;
    }

    private void buildClickables() {
        entries.forEach((slot, clickable) -> {
            setItemStack(slot, clickable.getDisplayItem());
        });
    }

    public @Nullable GuiClickable getClickableAt(int slot) {
        return entries.get(slot); // null if no entry
    }

    public static final ItemStack FILLER_MAIN = ItemStack.builder(Material.GRAY_STAINED_GLASS_PANE)
            .displayName(Component.empty())
            .meta(meta -> meta.hideFlag(ItemHideFlag.HIDE_ATTRIBUTES))
            .build();

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
