package net.polar.world;

import net.kyori.adventure.key.Key;
import net.minestom.server.MinecraftServer;
import net.minestom.server.instance.IChunkLoader;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.utils.NamespaceID;
import net.minestom.server.world.DimensionType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * Represents an {@link InstanceContainer} but is
 * capable of tracking ticks.
 */
public class TickTrackingInstanceContainer extends InstanceContainer {

    /**
     * An {@link DimensionType} that cancels the idea of Lighting, effectively making server-side fullbright.
     */
    public static final DimensionType FULLBRIGHT_DIMENSION = DimensionType.builder(NamespaceID.from(Key.key("polaroid:fullbright")))
            .ambientLight(2.0f)
            .build();

    private long ticks = 0L;


    /**
     * Constructs a new {@link TickTrackingInstanceContainer} with the specified {@link UUID}, {@link DimensionType} and {@link IChunkLoader}.
     * @param uniqueId the unique ID of this instance
     * @param dimensionType the dimension type of this instance
     * @param loader the chunk loader of this instance
     */
    public TickTrackingInstanceContainer(@NotNull UUID uniqueId, @NotNull DimensionType dimensionType, @Nullable IChunkLoader loader) {
        super(uniqueId, dimensionType, loader);
        MinecraftServer.getInstanceManager().registerInstance(this);
    }

    /**
     * Constructs a new {@link TickTrackingInstanceContainer} with the specified {@link UUID} and {@link DimensionType}. The {@link IChunkLoader} is set to null.
     * @param uniqueId  the unique ID of this instance
     * @param dimensionType the dimension type of this instance
     */
    public TickTrackingInstanceContainer(@NotNull UUID uniqueId, @NotNull DimensionType dimensionType) {
        this(uniqueId, dimensionType, null);
    }

    /**
     * Constructs a new {@link TickTrackingInstanceContainer} with the specified {@link UUID}. The {@link DimensionType} is set to {@link DimensionType#OVERWORLD} and the {@link IChunkLoader} is set to null.
     * @param uniqueId the unique ID of this instance
     */
    public TickTrackingInstanceContainer(@NotNull UUID uniqueId) {
        this(uniqueId, DimensionType.OVERWORLD, null);
    }

    @Override
    public void tick(long time) {
        super.tick(time);
        ticks++;
    }

    /**
     * @return the amount of ticks this instance has been running for.
     */
    public long getTicks() {
        return ticks;
    }


}
