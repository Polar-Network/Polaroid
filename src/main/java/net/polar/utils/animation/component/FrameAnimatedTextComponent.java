package net.polar.utils.animation.component;

import net.kyori.adventure.text.Component;
import net.polar.utils.animation.Animatable;
import net.polar.utils.chat.ChatColor;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public class FrameAnimatedTextComponent implements Animatable<Component> {

    private final @NotNull Component[] frames;

    private int currentFrame = 0;
    private int framesPerUpdate = 1;
    private int currentUpdate = 0;

    public FrameAnimatedTextComponent(int framesPerUpdate, @NotNull String... frames) {
        this.frames = Arrays.stream(frames).map(ChatColor::color).toArray(Component[]::new);
        this.framesPerUpdate = framesPerUpdate;
    }

    public FrameAnimatedTextComponent(int framesPerUpdate, @NotNull Component... frames) {
        this.frames = frames;
        this.framesPerUpdate = framesPerUpdate;
    }

    public FrameAnimatedTextComponent(int framesPerUpdate, @NotNull List<Component> frames) {
        this.frames = frames.toArray(new Component[0]);
        this.framesPerUpdate = framesPerUpdate;
    }

    @Override
    public @NotNull Component get() {
        return frames[currentFrame];
    }

    @Override
    public @NotNull Component getPrevious() {
        return frames[currentFrame - 1];
    }

    @Override
    public @NotNull Component getNext() {
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
