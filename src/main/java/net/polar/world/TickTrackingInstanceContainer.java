package net.polar.world;

import net.kyori.adventure.key.Key;
import net.minestom.server.instance.IChunkLoader;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.utils.NamespaceID;
import net.minestom.server.world.DimensionType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class TickTrackingInstanceContainer extends InstanceContainer {

    public static final DimensionType FULLBRIGHT_DIMENSION = DimensionType.builder(NamespaceID.from(Key.key("polaroid:fullbright")))
            .ambientLight(2.0f)
            .build();

    private long ticks = 0L;

    public TickTrackingInstanceContainer(@NotNull UUID uniqueId, @NotNull DimensionType dimensionType, @Nullable IChunkLoader loader) {
        super(uniqueId, dimensionType, loader);
    }

    public TickTrackingInstanceContainer(@NotNull UUID uniqueId, @NotNull DimensionType dimensionType) {
        this(uniqueId, dimensionType, null);
    }

    public TickTrackingInstanceContainer(@NotNull UUID uniqueId) {
        this(uniqueId, DimensionType.OVERWORLD, null);
    }

    @Override
    public void tick(long time) {
        super.tick(time);
        ticks++;
    }

    public long getTicks() {
        return ticks;
    }


}
