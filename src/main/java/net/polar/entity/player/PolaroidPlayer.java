package net.polar.entity.player;

import net.minestom.server.entity.Player;
import net.minestom.server.network.player.PlayerConnection;
import net.minestom.server.timer.ExecutionType;
import net.polar.Polaroid;
import net.polar.database.rank.Rank;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class PolaroidPlayer extends Player {

    private Rank rank;

    public PolaroidPlayer(
            @NotNull UUID uuid,
            @NotNull String username,
            @NotNull PlayerConnection playerConnection
    ) {
        super(uuid, username, playerConnection);
        scheduler().buildTask(() -> {
            rank = Polaroid.getRankProvider().provide(this);
        }).executionType(ExecutionType.ASYNC).schedule();
    }
}
