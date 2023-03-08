package net.polar.entity;

import net.kyori.adventure.text.Component;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.PlayerSkin;
import net.minestom.server.entity.fakeplayer.FakePlayer;
import net.minestom.server.entity.fakeplayer.FakePlayerOption;
import net.minestom.server.entity.metadata.PlayerMeta;
import net.minestom.server.instance.Instance;
import net.minestom.server.network.packet.server.play.PlayerInfoPacket;
import net.minestom.server.network.packet.server.play.TeamsPacket;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.function.Consumer;

public class NPC extends FakePlayer {

    private static final TeamsPacket createTeamPacket = MinecraftServer.getTeamManager().createBuilder("npcTeam")
            .nameTagVisibility(TeamsPacket.NameTagVisibility.NEVER)
            .build().createTeamsCreationPacket();

    private final MultiLineHologram hologram;

    private final PlayerInfoPacket addPlayerPacket;
    private final PlayerInfoPacket removePlayerPacket;
    private final TeamsPacket teamPacket;
    private Consumer<Player> interactConsumer;
    private boolean hasCooldown = true;
    private final Set<Player> cooldownPlayers = ConcurrentHashMap.newKeySet();

    public NPC(@NotNull String id, @NotNull PlayerSkin skin, @NotNull List<String> hologram, @Nullable Consumer<Player> interactConsumer) {
        super(UUID.randomUUID(), id, new FakePlayerOption(), null);
        this.hologram = new MultiLineHologram(hologram);
        this.addPlayerPacket = new PlayerInfoPacket(
                PlayerInfoPacket.Action.ADD_PLAYER,
                new PlayerInfoPacket.AddPlayer(getUuid(), id, List.of(
                        new PlayerInfoPacket.AddPlayer.Property("textures",skin.textures(),skin.signature())),
                        GameMode.CREATIVE,
                        0,
                        Component.empty(),
                        null)
        );
        this.interactConsumer = interactConsumer;
        this.removePlayerPacket = new PlayerInfoPacket(PlayerInfoPacket.Action.REMOVE_PLAYER, new PlayerInfoPacket.RemovePlayer(getUuid()));
        this.teamPacket = new TeamsPacket("npcTeam", new TeamsPacket.AddEntitiesToTeamAction(List.of(getUuid().toString())));
        initFullSkin();
        setTeam(MinecraftServer.getTeamManager().getTeam("npcTeam"));
    }

    public NPC(@NotNull String id, @NotNull PlayerSkin skin, @NotNull List<String> hologram) {
        this(id, skin, hologram, null);
    }

    @Override
    public CompletableFuture<Void> setInstance(@NotNull Instance instance, @NotNull Pos spawnPosition) {
        return super.setInstance(instance, spawnPosition).thenRun(() -> {
            hologram.ride(instance, this);
        });
    }

    public void onInteract(Player player) {
        if (hasCooldown && cooldownPlayers.contains(player)) {
            cooldownPlayers.remove(player);
            return;
        }
        if (interactConsumer != null) {
            interactConsumer.accept(player);
            cooldownPlayers.add(player);
        }
    }

    @Override @SuppressWarnings("UnstableApiUsage")
    public void updateNewViewer(@NotNull Player player) {
        player.sendPacket(addPlayerPacket);
        player.sendPacket(teamPacket);
        MinecraftServer.getSchedulerManager().scheduleNextTick(() -> player.sendPacket(removePlayerPacket));
        super.updateNewViewer(player);
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

    public void setInteractConsumer(Consumer<Player> interactConsumer) {
        this.interactConsumer = interactConsumer;
    }

    public void setHasCooldown(boolean hasCooldown) {
        this.hasCooldown = hasCooldown;
    }

    public @Nullable Consumer<Player> getInteractConsumer() {
        return interactConsumer;
    }

    public boolean isHasCooldown() {
        return hasCooldown;
    }
}
