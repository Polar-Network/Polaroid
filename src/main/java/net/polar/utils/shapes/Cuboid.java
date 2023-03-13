package net.polar.utils.shapes;

import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Vec;
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

    /**
     * Constructs a new cuboid.
     * @param bottomLeft the bottom left point of the cuboid
     * @param topRight the top right point of the cuboid
     * @param bottomRight the bottom right point of the cuboid
     * @param topLeft the top left point of the cuboid
     */
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

    /**
     * Constructs a new cuboid with the given bottom left and top right points. This predicts the rest of the points
     * @param bottomLeft the bottom left point of the cuboid
     * @param topRight the top right point of the cuboid
     */
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


    /**
     * @return the center point of the cuboid
     */
    public @NotNull Point getCenter() {
        return new Vec(
                (topLeft.x() + bottomRight.x()) / 2,
                (topLeft.y() + bottomRight.y()) / 2,
                (topLeft.z() + bottomRight.z()) / 2
        );
    }

    /**
     * @return the bottom left point of the cuboid
     */
    public @NotNull Point getBottomLeft() {
        return bottomLeft;
    }

    /**
     * @return the top right point of the cuboid
     */
    public @NotNull Point getTopRight() {
        return topRight;
    }

    /**
     * @return the bottom right point of the cuboid
     */
    public @NotNull Point getBottomRight() {
        return bottomRight;
    }

    /**
     * @return the top left point of the cuboid
     */
    public @NotNull Point getTopLeft() {
        return topLeft;
    }

}
