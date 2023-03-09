package net.polar.entity.npc;

import net.minestom.server.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;

public record NPCChatMessage(@NotNull String message, @Nullable Action... actions) {


    public record Action(@NotNull String text, @NotNull BiConsumer<NPC, Player> action) {

    }

}
