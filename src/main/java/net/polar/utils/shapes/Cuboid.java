package net.polar.utils.shapes;

import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Vec;
import org.checkerframework.checker.signature.qual.BinaryNameOrPrimitiveType;
import org.jetbrains.annotations.NotNull;

import static java.lang.Math.min;
import static java.lang.Math.max;

/**
 * Represents a Cuboid shape in a 3D space.
 */
public class Cuboid {

    private final Point bottomLeft;
    private final Point topRight;
    private final Point bottomRight;
    private final Point topLeft;

    public Cuboid(
            @NotNull Point bottomLeft,
            @NotNull Point topRight,
            @NotNull Point bottomRight,
            @NotNull Point topLeft
    ) {
        this.bottomLeft = bottomLeft;
        this.topRight = topRight;
        this.bottomRight = bottomRight;
        this.topLeft = topLeft;
    }

    public Cuboid(
            @NotNull Point bottomLeft,
            @NotNull Point topRight
    ) {
        this.bottomLeft = bottomLeft;
        this.topRight = topRight;
        this.bottomRight = new Vec(topRight.x(), bottomLeft.y(), bottomLeft.z());
        this.topLeft = new Vec(bottomLeft.x(), topRight.y(), topRight.z());
    }

    /**
     * Checks if the given point is contained in the cuboid.
     * @param point the point to check
     * @return true if the point is contained in the cuboid, false otherwise
     */
    public boolean contains(@NotNull Point point) {
        return min(topLeft.x(), bottomRight.x()) <= point.x() && point.x() <= max(topLeft.x(), bottomRight.x()) &&
                min(topLeft.y(), bottomRight.y()) <= point.y() && point.y() <= max(topLeft.y(), bottomRight.y()) &&
                min(topLeft.z(), bottomRight.z()) <= point.z() && point.z() <= max(topLeft.z(), bottomRight.z());
    }

    public @NotNull Point getCenter() {
        return new Vec(
                (topLeft.x() + bottomRight.x()) / 2,
                (topLeft.y() + bottomRight.y()) / 2,
                (topLeft.z() + bottomRight.z()) / 2
        );
    }

    public @NotNull Point getBottomLeft() {
        return bottomLeft;
    }

    public @NotNull Point getTopRight() {
        return topRight;
    }

    public @NotNull Point getBottomRight() {
        return bottomRight;
    }

    public @NotNull Point getTopLeft() {
        return topLeft;
    }

}
