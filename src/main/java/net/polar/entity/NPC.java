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

import java.util.List;
import java.util.UUID;

public class NPC extends FakePlayer {

    private static final TeamsPacket createTeamPacket = MinecraftServer.getTeamManager().createBuilder("npcTeam")
            .nameTagVisibility(TeamsPacket.NameTagVisibility.NEVER)
            .build().createTeamsCreationPacket();

    private final MultiLineHologram hologram;

    private final PlayerInfoPacket addPlayerPacket;
    private final PlayerInfoPacket removePlayerPacket;
    private final TeamsPacket teamPacket;

    public NPC(String username, PlayerSkin skin, List<String> hologram) {
        super(UUID.randomUUID(), username, new FakePlayerOption(), null);
        this.hologram = new MultiLineHologram(hologram);
        this.addPlayerPacket = new PlayerInfoPacket(
                PlayerInfoPacket.Action.ADD_PLAYER,
                new PlayerInfoPacket.AddPlayer(getUuid(), username, List.of(
                        new PlayerInfoPacket.AddPlayer.Property("textures",skin.textures(),skin.signature())),
                        GameMode.CREATIVE,
                        0,
                        Component.empty(),
                        null)
        );
        this.removePlayerPacket = new PlayerInfoPacket(PlayerInfoPacket.Action.REMOVE_PLAYER, new PlayerInfoPacket.RemovePlayer(getUuid()));
        this.teamPacket = new TeamsPacket("npcTeam", new TeamsPacket.AddEntitiesToTeamAction(List.of(getUuid().toString())));
        initFullSkin();
        setTeam(MinecraftServer.getTeamManager().getTeam("npcTeam"));
    }

    public void properlySpawn(Pos point, Instance instance) {
        this.setInstance(instance, point).thenRun(() -> {
            hologram.create(instance, point.add(0, 1, 0));
        });
    }

    public void onInteract(Player player) {}

    @Override
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
}
