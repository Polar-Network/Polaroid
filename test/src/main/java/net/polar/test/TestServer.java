package net.polar.test;

import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.PlayerSkin;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.entity.EntityDamageEvent;
import net.minestom.server.event.player.PlayerSpawnEvent;
import net.polar.Polaroid;
import net.polar.entity.NPC;

import java.util.List;
import java.util.UUID;

public final class TestServer {

    public static void main(String[] args) {
        Polaroid.initServer();
        GlobalEventHandler eh = MinecraftServer.getGlobalEventHandler();

        eh.addListener(PlayerSpawnEvent.class, event -> {
           event.getPlayer().setGameMode(GameMode.CREATIVE);
           event.getPlayer().setAllowFlying(true);
           event.getPlayer().setFlying(true);
        });
        eh.addListener(EntityDamageEvent.class, event -> event.setCancelled(true));

        NPC npc = new NPC(UUID.randomUUID(), "testnpc", Pos.ZERO, PlayerSkin.fromUsername("tofaa"), "tofaa", List.of());
        npc.setInstance(Polaroid.getDefaultInstance(), Pos.ZERO);
    }

}
