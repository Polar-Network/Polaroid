package net.polar.entity;

import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.PlayerSkin;
import net.minestom.server.entity.fakeplayer.FakePlayer;
import net.minestom.server.entity.fakeplayer.FakePlayerOption;
import net.minestom.server.entity.metadata.PlayerMeta;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.player.PlayerPacketEvent;
import net.minestom.server.instance.Instance;
import net.minestom.server.network.packet.client.play.ClientInteractEntityPacket;
import net.minestom.server.network.packet.server.play.TeamsPacket;
import net.minestom.server.scoreboard.Team;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class NPC extends FakePlayer {

    private static final EventNode<Event> EVENT_NODE = EventNode.all("npcs");
    private static final Team NPC_TEAM = MinecraftServer.getTeamManager().createBuilder("npcTeam")
            .nameTagVisibility(TeamsPacket.NameTagVisibility.NEVER)
            .build();
    static {
        MinecraftServer.getGlobalEventHandler().addChild(EVENT_NODE);
    }

    private final MultiLineHologram display;
    private final Consumer<Player> onInteraction;

    public NPC(
        @NotNull String username,
        @NotNull PlayerSkin skin,
        @NotNull MultiLineHologram display,
        @NotNull Consumer<Player> onInteraction
    ) {
        super(UUID.randomUUID(), username, new FakePlayerOption().setRegistered(true).setInTabList(false), null);
        this.display = display;
        this.onInteraction = onInteraction;
        setupInteraction();
        setTeam(NPC_TEAM);
        scheduleNextTick((ignored) -> {
            initFullSkin();
            setSkin(skin);
        });
    }

    @Override
    public CompletableFuture<Void> setInstance(@NotNull Instance instance, @NotNull Pos spawnPosition) {
        return super.setInstance(instance, spawnPosition).thenRun(() -> {
            display.ride(instance, this);
        });
    }

    private void initFullSkin() {
        var meta = (PlayerMeta) getEntityMeta();
        meta.setNotifyAboutChanges(false);
        meta.setCapeEnabled(true);
        meta.setHatEnabled(true);
        meta.setJacketEnabled(true);
        meta.setLeftLegEnabled(true);
        meta.setLeftSleeveEnabled(true);
        meta.setRightLegEnabled(true);
        meta.setRightSleeveEnabled(true);
        meta.setNotifyAboutChanges(true);
    }

    private void setupInteraction() {
        EventNode<Event> eventNode = EventNode.all(String.valueOf(getEntityId()));
        EVENT_NODE.addChild(eventNode);
        eventNode.addListener(PlayerPacketEvent.class, event -> {
            if (!(event.getPacket() instanceof ClientInteractEntityPacket packet)) return;
            if (packet.targetId() != getEntityId()) return;

            ClientInteractEntityPacket.Type type = packet.type();
            if (type.id() == 0) return; // Type is either Attack or InteractAt now
            if (type instanceof ClientInteractEntityPacket.InteractAt interactAt) {
                if (interactAt.hand() == Player.Hand.OFF) return;
            }

            onInteraction.accept(event.getPlayer());
        });
    }

}
