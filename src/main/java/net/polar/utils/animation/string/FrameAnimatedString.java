package net.polar.utils.animation.string;

import net.polar.utils.animation.Animatable;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class FrameAnimatedString implements Animatable<String> {

    private final @NotNull String[] frames;

    private int currentFrame = 0;
    private int framesPerUpdate = 1;
    private int currentUpdate = 0;

    public FrameAnimatedString(int framesPerUpdate, @NotNull String... frames) {
        this.frames = frames;
        this.framesPerUpdate = framesPerUpdate;
    }

    public FrameAnimatedString(int framesPerUpdate, @NotNull List<String> frames) {
        this.frames = frames.toArray(new String[0]);
        this.framesPerUpdate = framesPerUpdate;
    }

    @Override
    public @NotNull String get() {
        return frames[currentFrame];
    }

    @Override
    public @NotNull String getPrevious() {
        return frames[currentFrame - 1];
    }

    @Override
    public @NotNull String getNext() {
        if (currentFrame + 1 >= frames.length) return frames[0];
        else return frames[currentFrame + 1];
    }

    @Override
    public void update() {
        if (currentUpdate >= framesPerUpdate) {
            currentUpdate = 0;
            currentFrame++;
            if (currentFrame >= frames.length) currentFrame = 0;
        } else currentUpdate++;
    }
}
