package net.polar.utils.animation.scoreboard;

import net.minestom.server.scoreboard.Sidebar;
import net.polar.utils.animation.Animatable;
import net.polar.utils.animation.component.FrameAnimatedTextComponent;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * A sidebar that can have each of its lines and title animated.
 * This might be a bit laggy, so you should use a higher framesPerUpdate value or cache requests if a ton of players are connecting at the same time.
 */
public class FrameAnimatedSidebar implements Animatable<Sidebar> {

    private final FrameAnimatedTextComponent title;
    private final List<FrameAnimatedTextComponent> lines;
    private final Sidebar sidebar;
    private final int framesPerUpdate;
    private int currentFrame = 0;
    private int currentUpdate = 0;
    private final int length;

    /**
     * Creates a new FrameAnimatedSidebar.
     * @param framesPerUpdate The amount of times {@link #update()} needs to be called before the animation updates.
     * @param title The title of the sidebar.
     * @param lines The lines of the sidebar.
     */
    public FrameAnimatedSidebar(
            int framesPerUpdate,
            @NotNull FrameAnimatedTextComponent title,
            @NotNull List<FrameAnimatedTextComponent> lines
    ) {
        this.framesPerUpdate = framesPerUpdate;
        this.title = title;
        this.lines = lines;
        this.sidebar = new Sidebar(title.get());
        Sidebar.ScoreboardLine[] scoreboardLines = new Sidebar.ScoreboardLine[this.lines.size()];
        int largestSize = title.getLength();
        for(int i = 0; i < lines.size(); i++) {
            int fromBottom = lines.size() - i;
            FrameAnimatedTextComponent line = lines.get(i);
            if (line.getLength() > largestSize) largestSize = line.getLength();
            scoreboardLines[i] = new Sidebar.ScoreboardLine("line-" + i, line.get(), fromBottom);
        }
        this.length = largestSize;
        for (var line : scoreboardLines) sidebar.createLine(line);
    }

    @Override
    public @NotNull Sidebar get() {
        sidebar.setTitle(title.get());
        for (int i = 0; i < lines.size(); i++) {
            FrameAnimatedTextComponent line = lines.get(i);
            sidebar.updateLineContent("line-" + i, line.get());
        }
        return sidebar;
    }

    @Override
    public @NotNull Sidebar getPrevious() {
        sidebar.setTitle(title.getPrevious());
        for (int i = 0; i < lines.size(); i++) {
            FrameAnimatedTextComponent line = lines.get(i);
            sidebar.updateLineContent("line-" + i, line.getPrevious());
        }
        return sidebar;
    }

    @Override
    public @NotNull Sidebar getNext() {
        sidebar.setTitle(title.getNext());
        for (int i = 0; i < lines.size(); i++) {
            FrameAnimatedTextComponent line = lines.get(i);
            sidebar.updateLineContent("line-" + i, line.getNext());
        }
        return sidebar;
    }

    @Override
    public void update() {
        if (currentUpdate >= framesPerUpdate) {
            currentUpdate = 0;
            currentFrame++;
            if (currentFrame >= length) currentFrame = 0;
            return;
        }
        currentUpdate++;
        title.update();
        sidebar.setTitle(title.get());

        for (int i = 0; i < lines.size(); i++) {
            FrameAnimatedTextComponent line = lines.get(i);
            line.update();
            sidebar.updateLineContent("line-" + i, line.get());
        }
    }

    @Override
    public int getLength() {
        return length;
    }

}
