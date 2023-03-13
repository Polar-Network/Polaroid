package net.polar.utils.animation;

import org.jetbrains.annotations.NotNull;

/**
 * Represents an Animatable object.
 * @param <T> The type of the object.
 */
public interface Animatable<T> {

    /**
     * @return The latest updated animation.
     */
    @NotNull T get();

    /**
     * @return The previous animation.
     */
    @NotNull T getPrevious();

    /**
     * @return The next animation.
     */
    @NotNull T getNext();

    /**
     * Updates the animations.
     */
    void update();

    /**
     * @return The length of the animation. This should represent the amount of frames per complete cycle of the animation.
     */
    int getLength();

}
