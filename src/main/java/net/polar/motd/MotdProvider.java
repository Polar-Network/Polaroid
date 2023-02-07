package net.polar.motd;

import net.minestom.server.ping.ResponseData;
import net.polar.utils.ChatColor;
import org.jetbrains.annotations.NotNull;

public abstract class MotdProvider {

    public static ResponseData DEFAULT_MOTD = new ResponseData();
    static {
        DEFAULT_MOTD.setDescription(ChatColor.color("A Polaroid server"));
    }



    @NotNull public abstract ResponseData provide();

}
