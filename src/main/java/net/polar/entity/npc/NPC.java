package net.polar.entity.npc;

import net.kyori.adventure.text.Component;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.*;
import net.minestom.server.entity.metadata.PlayerMeta;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.player.PlayerPacketEvent;
import net.minestom.server.instance.Instance;
import net.minestom.server.network.packet.client.play.ClientInteractEntityPacket;
import net.minestom.server.network.packet.server.play.PlayerInfoPacket;
import net.minestom.server.network.packet.server.play.TeamsPacket;
import net.minestom.server.scoreboard.Team;
import net.minestom.server.utils.time.TimeUnit;
import net.polar.entity.MultiLineHologram;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

@SuppressWarnings("UnstableApiUsage")
public class NPC extends LivingEntity {

    private static final EventNode<Event> EVENT_NODE = EventNode.all("npcs");
    private static Team NPC_TEAM = MinecraftServer.getTeamManager().createBuilder("npcTeam")
            .nameTagVisibility(TeamsPacket.NameTagVisibility.NEVER)
            .collisionRule(TeamsPacket.CollisionRule.NEVER)
            .build();
    static {
        MinecraftServer.getGlobalEventHandler().addChild(EVENT_NODE);
    }

    private final MultiLineHologram display;
    private final Consumer<Player> onInteraction;
    private final PlayerInfoPacket infoPacket;
    private final PlayerInfoPacket removePacket;
    public NPC(
        @NotNull PlayerSkin skin,
        @NotNull MultiLineHologram display,
        @NotNull Consumer<Player> onInteraction
    ) {
        super(EntityType.PLAYER);
        checkNPCTeam();
        this.display = display;
        this.onInteraction = onInteraction;
        setupInteraction();
        scheduleNextTick((ignored) -> {
            initFullSkin();
        });
        List<PlayerInfoPacket.AddPlayer.Property> prop = List.of(new PlayerInfoPacket.AddPlayer.Property("textures", skin.textures(), skin.signature()));
        infoPacket = new PlayerInfoPacket(
                PlayerInfoPacket.Action.ADD_PLAYER,
                new PlayerInfoPacket.AddPlayer(getUuid(), "", prop, GameMode.CREATIVE, 0, Component.empty(), null)
        );
        removePacket = new PlayerInfoPacket(PlayerInfoPacket.Action.REMOVE_PLAYER, new PlayerInfoPacket.RemovePlayer(getUuid()));
    }

    @Override
    public CompletableFuture<Void> setInstance(@NotNull Instance instance, @NotNull Pos spawnPosition) {
        return super.setInstance(instance, spawnPosition).thenRun(() -> {
            setTeam(NPC_TEAM);
            display.ride(instance, this);
        });
    }

    @Override
    public void updateNewViewer(@NotNull Player player) {
        player.sendPacket(infoPacket);
        MinecraftServer.getSchedulerManager().buildTask(() -> player.sendPacket(removePacket)).delay(20, TimeUnit.SERVER_TICK).schedule();
        super.updateNewViewer(player);
    }

    private void checkNPCTeam() {
        if (NPC_TEAM == null) {
            NPC_TEAM = MinecraftServer.getTeamManager().createBuilder("npcTeam")
                    .nameTagVisibility(TeamsPacket.NameTagVisibility.NEVER)
                    .collisionRule(TeamsPacket.CollisionRule.NEVER)
                    .build();
        }
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
