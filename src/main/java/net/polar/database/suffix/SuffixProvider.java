package net.polar.database.suffix;

import net.polar.entity.player.PolaroidPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface SuffixProvider {

    static @NotNull SuffixProvider defaultProvider() {
        return new SuffixProviderImpl();
    }


    List<Suffix> getSuffixes(@NotNull PolaroidPlayer player);

}
