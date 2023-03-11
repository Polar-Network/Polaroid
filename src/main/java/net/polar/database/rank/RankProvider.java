package net.polar.database.rank;

import net.polar.entity.player.PolaroidPlayer;
import org.jetbrains.annotations.NotNull;

public interface RankProvider {

    static @NotNull RankProvider defaultProvider() {
        return new RankProviderImpl();
    }

    @NotNull Rank provide(@NotNull PolaroidPlayer player);

    @NotNull Rank getDefault();

    void appendRank(@NotNull Rank rank);

}
