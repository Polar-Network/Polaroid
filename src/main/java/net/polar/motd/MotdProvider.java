package net.polar.motd;

import net.minestom.server.ping.ResponseData;
import net.polar.Polaroid;
import net.polar.utils.ChatColor;
import org.jetbrains.annotations.NotNull;

/**
 * A class that provides a {@link ResponseData} object to be used as the server's MOTD.
 */
public abstract class MotdProvider {


    /**
     * Default MOTD to be used if no other MOTD is provided.
     */
    public static ResponseData DEFAULT_MOTD = new ResponseData();
    static {
        DEFAULT_MOTD.setDescription(ChatColor.color("A Polaroid server"));
        DEFAULT_MOTD.setMaxPlayer(Polaroid.getMaxPlayers());
    }


    /**
     * @return The MOTD to be used by the server.
     */
    @NotNull public abstract ResponseData provide();

}
