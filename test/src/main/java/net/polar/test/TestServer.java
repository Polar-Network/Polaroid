package net.polar.test;

import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.PlayerSkin;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.entity.EntityAttackEvent;
import net.minestom.server.event.entity.EntityDamageEvent;
import net.minestom.server.event.player.PlayerSpawnEvent;
import net.polar.Polaroid;
import net.polar.entity.NPC;
import net.polar.utils.chat.ChatColor;
import net.polar.world.TickTrackingInstanceContainer;

import java.util.List;
import java.util.UUID;

public final class TestServer {

    public static void main(String[] args) {
        Polaroid.initServer();
        Polaroid.setDefaultInstance(new TickTrackingInstanceContainer(UUID.randomUUID(), TickTrackingInstanceContainer.FULLBRIGHT_DIMENSION));
        GlobalEventHandler eh = MinecraftServer.getGlobalEventHandler();

        eh.addListener(PlayerSpawnEvent.class, event -> {
           event.getPlayer().setGameMode(GameMode.CREATIVE);
           event.getPlayer().setAllowFlying(true);
           event.getPlayer().setFlying(true);
        });
        eh.addListener(EntityDamageEvent.class, event -> event.setCancelled(true));
        eh.addListener(EntityAttackEvent.class, event -> {
        });

        NPC npc = new NPC("random0", PlayerSkin.fromUsername("Tofaa"), List.of("<gold>CLICK ME", "<blue>Steven"));
        npc.setInstance(Polaroid.getDefaultInstance(), new Pos(0, 1, 0));
        npc.setInteractConsumer(player -> {
            player.sendMessage(ChatColor.color("<red>CLICKED"));
        });
    }

}
