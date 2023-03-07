package net.polar.entity;

import net.kyori.adventure.text.Component;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.*;
import net.minestom.server.event.Event;
import net.minestom.server.event.player.PlayerPacketEvent;
import net.minestom.server.instance.Instance;
import net.minestom.server.network.packet.client.play.ClientInteractEntityPacket;
import net.minestom.server.network.packet.server.play.EntityMetaDataPacket;
import net.minestom.server.network.packet.server.play.PlayerInfoPacket;
import net.minestom.server.network.packet.server.play.SpawnPlayerPacket;
import net.minestom.server.network.packet.server.play.TeamsPacket;
import org.jetbrains.annotations.NotNull;
import net.minestom.server.event.EventNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public class NPC {

    private static final EventNode<Event> EVENT_NODE = EventNode.all("npcs");
    static {
        MinecraftServer.getGlobalEventHandler().addChild(EVENT_NODE);
    }

    private final @NotNull EventNode<Event> eventNode;
    private final PlayerInfoPacket playerInfoPacket;
    private final SpawnPlayerPacket spawnPlayerPacket;
    private final TeamsPacket teamPacket;
    private final EntityMetaDataPacket entityMetaPacket;
//    private final PlayerInfoPacket listRemovePacket;
//    private final MultiLineHologram hologram;

    public NPC(@NotNull Instance instance, @NotNull Pos position, @NotNull PlayerSkin skin, @NotNull MultiLineHologram hologram, @NotNull Consumer<@NotNull Player> onClick) {
        UUID uuid = UUID.randomUUID();
        int entityId = Entity.generateId();

//        this.hologram = hologram;
        this.eventNode = EventNode.all(String.valueOf(entityId));
        EVENT_NODE.addChild(this.eventNode);

        this.eventNode.addListener(PlayerPacketEvent.class, event -> {
            if (!(event.getPacket() instanceof ClientInteractEntityPacket packet)) return;
            if (packet.targetId() != entityId) return;

            ClientInteractEntityPacket.Type type = packet.type();
            if (type.id() == 0) return; // Type is either Attack or InteractAt now
            if (type instanceof ClientInteractEntityPacket.InteractAt interactAt) {
                if (interactAt.hand() == Player.Hand.OFF) return;
            }

            onClick.accept(event.getPlayer());
        });

        String npcName = uuid.toString().substring(0, 8);
        List<PlayerInfoPacket.AddPlayer.Property> properties = new ArrayList<>();
        properties.add(
                new PlayerInfoPacket.AddPlayer.Property(
                        "textures",
                        skin.textures(),
                        skin.signature()
                )
        );

        this.playerInfoPacket = new PlayerInfoPacket(PlayerInfoPacket.Action.ADD_PLAYER, new PlayerInfoPacket.AddPlayer(uuid, npcName, properties, GameMode.CREATIVE, 0, Component.empty(), null));
        this.spawnPlayerPacket = new SpawnPlayerPacket(entityId, uuid, position);
        this.teamPacket = new TeamsPacket("npcTeam", new TeamsPacket.AddEntitiesToTeamAction(List.of(npcName)));
        this.entityMetaPacket = new EntityMetaDataPacket(entityId, Map.of(17, Metadata.Byte((byte) 127 /*All layers enabled*/)));
//        this.listRemovePacket = new PlayerInfoPacket(PlayerInfoPacket.Action.REMOVE_PLAYER, new PlayerInfoPacket.RemovePlayer(uuid));
        MinecraftServer.getSchedulerManager().scheduleNextTick(() -> {
            hologram.create(instance, position.add(0, 1, 0));
        });
    }

    public void addViewer(@NotNull Player player) {
        player.sendPackets(
                this.playerInfoPacket,
                this.spawnPlayerPacket,
                this.teamPacket,
                this.entityMetaPacket
        );
    }

}