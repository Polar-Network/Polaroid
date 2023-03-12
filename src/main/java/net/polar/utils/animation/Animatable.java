package net.polar.utils.animation;

import org.jetbrains.annotations.NotNull;

public interface Animatable<T> {

    @NotNull T get();

    @NotNull T getPrevious();

    @NotNull T getNext();

    void update();

}
