package net.polar.utils.shapes;

import net.minestom.server.coordinate.Point;

/**
 * TODO
 */
public interface PointBasedShape {

    double getArea();

    double getPerimeter();

    double getVolume();

    double getSurfaceArea();

    double getRadius();

    Point getCenter();

}
