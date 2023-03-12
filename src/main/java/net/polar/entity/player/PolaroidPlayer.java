package net.polar.entity.player;

import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.network.player.PlayerConnection;
import net.minestom.server.timer.ExecutionType;
import net.polar.Polaroid;
import net.polar.database.rank.Rank;
import net.polar.database.suffix.Suffix;
import net.polar.utils.StringReplacer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

/**
 * A player class that extends the default Minestom player class, this is required for Polaroid to work properly
 */
public class PolaroidPlayer extends Player {

    private @NotNull Rank rank = Polaroid.getRankProvider().getDefault();
    private @NotNull List<Suffix> suffixes;
    private @Nullable Suffix activeSuffix;


    /**
     * Creates a new PolaroidPlayer
     * @param uuid the player's uuid
     * @param username the player's username
     * @param playerConnection the player's connection
     */
    public PolaroidPlayer(
            @NotNull UUID uuid,
            @NotNull String username,
            @NotNull PlayerConnection playerConnection
    ) {
        super(uuid, username, playerConnection);
        MinecraftServer.getSchedulerManager().buildTask(() -> {
            this.rank = Polaroid.getRankProvider().provide(this);
            this.suffixes = Polaroid.getSuffixProvider().getSuffixes(this);
            this.activeSuffix = Polaroid.getSuffixProvider().getActiveSuffix(this);
        }).executionType(ExecutionType.ASYNC).schedule();
    }


    public @NotNull Rank getRank() {
        return rank;
    }

    public @NotNull List<Suffix> getSuffixes() {
        return suffixes;
    }

    public @Nullable Suffix getActiveSuffix() {
        return activeSuffix;
    }

    @Override
    public void remove() {
        super.remove();
        Polaroid.getRankProvider().save(this);
        Polaroid.getSuffixProvider().save(this);
    }

    protected static final StringReplacer<PolaroidPlayer> REPLACER = new StringReplacer<>();
    private static @NotNull String CHAT_FORMAT = "%rank% %name% %suffix% <gray>: <white>%message%";
    static {
        REPLACER.addReplacement(
                "%rank%", (player -> player.rank.getRankPrefix() + "<reset>")
        );
        REPLACER.addReplacement(
                "%suffix%", (player -> {
                    if (player.activeSuffix == null) {
                        return "<reset>";
                    }
                    return player.activeSuffix.getSuffix() + "<reset>";
                })
        );
        REPLACER.addReplacement(
                "%name%", (player -> player.rank.getNameColor() + player.getUsername() + "<reset>")
        );
    }

    /**
     * @return the chat format for players
     */
    public static @NotNull String getChatFormat() {
        return CHAT_FORMAT;
    }

    /**
     * Replaces the chat-format with a given one, can utilize everything from the REPLACER in this class
     * @param chatFormat the new chat format
     */
    public static void setChatFormat(@NotNull String chatFormat) {
        CHAT_FORMAT = chatFormat;
    }

    /**
     * @return the player string replacer
     */
    public static @NotNull StringReplacer<PolaroidPlayer> getReplacer() {
        return REPLACER;
    }
}
