package net.polar.test;

import net.minestom.server.MinecraftServer;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.entity.EntityDamageEvent;
import net.minestom.server.event.player.PlayerTickEvent;
import net.polar.Polaroid;
import net.polar.utils.chat.ChatColor;

public final class TestServer {

    public static void main(String[] args) {
        Polaroid.initServer();
        GlobalEventHandler eh = MinecraftServer.getGlobalEventHandler();

        eh.addListener(PlayerTickEvent.class, event -> event.getPlayer().sendMessage(ChatColor.color("<polar>Hello World!")));
        eh.addListener(EntityDamageEvent.class, event -> event.setCancelled(true));
    }

}
