package net.polar.world.schematic;

import net.minestom.server.utils.Rotation;
import org.jetbrains.annotations.NotNull;

public enum SchematicRotation {

    NONE,
    CLOCKWISE_90,
    CLOCKWISE_180,
    CLOCKWISE_270;

    public SchematicRotation rotate(SchematicRotation rotation) {
        return values()[(ordinal() + rotation.ordinal()) % 4];
    }

    public static @NotNull SchematicRotation from(@NotNull Rotation rotation) {
        return values()[rotation.ordinal() / 2];
    }

}
