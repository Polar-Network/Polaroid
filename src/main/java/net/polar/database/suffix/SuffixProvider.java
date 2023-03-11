package net.polar.database.suffix;

import net.polar.entity.player.PolaroidPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface SuffixProvider {

    static @NotNull SuffixProvider defaultProvider() {
        return new SuffixProviderImpl();
    }


    @NotNull List<Suffix> getSuffixes(@NotNull PolaroidPlayer player);

    @Nullable Suffix getActiveSuffix(@NotNull PolaroidPlayer player);


    void giveSuffix(@NotNull PolaroidPlayer player, @NotNull Suffix suffix);

}
