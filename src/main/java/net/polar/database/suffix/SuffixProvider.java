package net.polar.database.suffix;

import net.polar.entity.player.PolaroidPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Represents a provider for player suffixes
 * TODO: Make this less terrible and actually work
 */
public interface SuffixProvider {

    /**
     * @return a default implementation of the suffix provider
     */
    static @NotNull SuffixProvider defaultProvider() {
        return new SuffixProviderImpl();
    }


    /**
     * Get all available suffixes for a player
     * @param player the player to get the suffixes for
     * @return a list of suffixes
     */
    @NotNull List<Suffix> getSuffixes(@NotNull PolaroidPlayer player);

    /**
     * Get the active suffix for a player, this can be null if a player has no active suffix
     * @param player the player to get the suffix for
     * @return the active suffix, or null if the player has no active suffix
     */
    @Nullable Suffix getActiveSuffix(@NotNull PolaroidPlayer player);


    /**
     * Give a suffix to a player
     * @param player the player to give the suffix to
     * @param suffix the suffix to give
     */
    void giveSuffix(@NotNull PolaroidPlayer player, @NotNull Suffix suffix);

    /**
     * Saves a player's suffixes to the database
     * @param player the player to save the suffixes for
     */
    void save(@NotNull PolaroidPlayer player);

}
