package net.polar.database.rank;

import net.minestom.server.entity.Player;
import net.polar.entity.player.PolaroidPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;

/**
 * Represents a rank provider
 */
public interface RankProvider {

    /**
     * @return The default rank provider, note this returns a new instance every time
     */
    static @NotNull RankProvider defaultProvider() {
        return new RankProviderImpl();
    }


    /**
     * @return all available ranks for the server
     */
    @NotNull Set<Rank> getAvailableRanks();

    /**
     * Provide a player with the rank specified in their database
     * @param player The player to provide the rank to
     * @return The rank provided
     */
    @NotNull Rank provide(@NotNull PolaroidPlayer player);

    /**
     * @return The default rank for the server
     */
    @NotNull Rank getDefault();

    /**
     * Adds a rank to the database
     * @param rank The rank to give the player
     */
    void appendRank(@NotNull Rank rank);

    /**
     * Refreshes all ranks, this is for cache purposes
     */
    void refreshRanks();

    /**
     * Saves all ranks to the database, this is for cache purposes
     */
    void saveAll();

    /**
     * Saves a player's rank to the database
     * @param player The player to save the rank for
     */
    void save(@NotNull PolaroidPlayer player);

    /**
     * Saves a list of players' ranks to the database
     * @param players The players to save the ranks for
     */
    default void save(@NotNull List<PolaroidPlayer> players) {
        players.forEach(this::save);
    }
}
