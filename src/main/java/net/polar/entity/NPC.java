package net.polar.entity;

import com.extollit.gaming.ai.path.HydrazinePathFinder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.*;
import net.minestom.server.entity.metadata.PlayerMeta;
import net.minestom.server.entity.pathfinding.NavigableEntity;
import net.minestom.server.entity.pathfinding.Navigator;
import net.minestom.server.event.player.PlayerEntityInteractEvent;
import net.minestom.server.instance.Instance;
import net.minestom.server.network.packet.server.play.PlayerInfoPacket;
import net.minestom.server.network.packet.server.play.TeamsPacket;
import net.minestom.server.network.player.PlayerConnection;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class NPC extends LivingEntity implements NavigableEntity {

    private static final TeamsPacket createTeamPacket = MinecraftServer.getTeamManager().createBuilder("npcTeam")
            .nameTagVisibility(TeamsPacket.NameTagVisibility.NEVER)
            .build().createTeamsCreationPacket();


    private final @NotNull Navigator navigator = new Navigator(this);
    private final @NotNull String id;
    private final @NotNull Pos homePosition;
    private final @NotNull String name;
    private final @NotNull PlayerInfoPacket PLAYER_ADD_INFO;
    private final @NotNull PlayerInfoPacket PLAYER_HIDE_INFO;
    private final @NotNull PlayerSkin skin;
    private final MultiLineHologram hologram;
    private final @NotNull TeamsPacket teamPacket;

    public NPC(
            @NotNull UUID uuid,
            @NotNull String id,
            @NotNull Pos homePosition,
            @NotNull PlayerSkin skin,
            @NotNull String username,
            @NotNull List<String> displayName
    ) {
        super(EntityType.PLAYER, uuid);
        this.id = id;
        this.homePosition = homePosition;
        this.hologram = new MultiLineHologram(displayName);
        this.skin = skin;
        this.name = username.substring(0, Math.min(username.length(), 16));
        this.PLAYER_ADD_INFO = generatePlayerAddInfo();
        this.PLAYER_HIDE_INFO = generatePlayerHideInfo();
        this.teamPacket = new TeamsPacket("npcTeam", new TeamsPacket.AddEntitiesToTeamAction(List.of(getUuid().toString())));
    }

    /**
     * Gets the internal id of this npc
     */
    public @NotNull String getId() {
        return id;
    };

    /**
     * Gets the home position of this npc
     */
    public @NotNull Pos getHomePosition() {
        return homePosition;
    };

    /**
     * This is run whenever a player interacts with this npc
     */
    public void onInteract(PlayerEntityInteractEvent event) {}

    @NotNull
    @Override
    public Navigator getNavigator() {
        return navigator;
    }

    @Override
    public void update(long time) {
        super.update(time);
        this.navigator.tick(); // Pathfinding
        if (previousPosition != position) {
            hologram.teleportSmoothly(position.add(0, 1, 0));
        }

    }

    @Override
    public CompletableFuture<Void> setInstance(@NotNull Instance instance, @NotNull Pos spawnPosition) {
        this.navigator.setPathFinder(new HydrazinePathFinder(navigator.getPathingEntity(), instance.getInstanceSpace()));
        return super.setInstance(instance, spawnPosition).thenRun(() -> {
            hologram.remove();
            hologram.create(instance, getHomePosition().add(0, 1, 0));
        });
    }

    private @NotNull PlayerInfoPacket generatePlayerAddInfo() {
        Component customName = getCustomName();
        Component displayName = customName == null ? Component.text(name) : customName;

        return new PlayerInfoPacket(
                PlayerInfoPacket.Action.ADD_PLAYER,
                List.of(new PlayerInfoPacket.AddPlayer(uuid, name, List.of(new PlayerInfoPacket.AddPlayer.Property("textures",skin.textures(),skin.signature())), GameMode.CREATIVE, 0, displayName, null))
        );
    }

    private @NotNull PlayerInfoPacket generatePlayerHideInfo() {
        return new PlayerInfoPacket(
                PlayerInfoPacket.Action.REMOVE_PLAYER,
                new PlayerInfoPacket.RemovePlayer(uuid)
        );
    }

    @Override
    public void updateNewViewer(@NotNull Player player) {
        final PlayerConnection connection = player.getPlayerConnection();
        connection.sendPacket(PLAYER_ADD_INFO);
        connection.sendPacket(teamPacket);

        // Hide npc from tablist
        // This needs to be delayed, otherwise the player does not render.
        this.scheduleNextTick((ignored) -> connection.sendPacket(PLAYER_HIDE_INFO));
        super.updateNewViewer(player);
    }

    public void enableFullSkinProperties() {
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


    public interface Supplier {
        @NotNull NPC create(
                @NotNull UUID uuid,
                @NotNull String id,
                @NotNull Pos homePosition,
                @NotNull TextComponent displayName
        );
    }
}