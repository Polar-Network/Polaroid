package net.polar.test;

import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.PlayerSkin;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.player.PlayerSpawnEvent;
import net.polar.Polaroid;
import net.polar.entity.MultiLineHologram;
import net.polar.entity.npc.NPC;
import net.polar.world.TickTrackingInstanceContainer;

import java.util.UUID;

public final class TestServer {

    public static void main(String[] args) {
        Polaroid.initServer();
        Polaroid.setDefaultInstance(new TickTrackingInstanceContainer(UUID.randomUUID(), TickTrackingInstanceContainer.FULLBRIGHT_DIMENSION));
        GlobalEventHandler eh = MinecraftServer.getGlobalEventHandler();
        NPC npc = new NPC(
                PlayerSkin.fromUsername("Tofaa"),
                new MultiLineHologram("<red>CLICK ME!", "<green>CLICK ME!"),
                player -> player.sendMessage("You clicked me!")
        );
        npc.setInstance(Polaroid.getDefaultInstance(), Pos.ZERO);
        eh.addListener(PlayerSpawnEvent.class, event -> {
           event.getPlayer().setGameMode(GameMode.CREATIVE);
           event.getPlayer().setAllowFlying(true);
           event.getPlayer().setFlying(true);
        });
    }

}
