package net.polar.gui.pattern;

import net.polar.gui.Gui;

/**
 * A basic implementation of {@link GuiPattern} that provides a few basic patterns.
 * These are by default used in {@link Gui#rebuildInventory()} to fill the inventory with the specified pattern.
 */
public enum BasicGuiPatterns implements GuiPattern {

    /** Should never be used */
    NONE,

    /** Represents one type of filler that's across the entire Gui */
    FULL,

    /** Represents filler styling of a checkerboard pattern */
    CHECKERBOARD

}
