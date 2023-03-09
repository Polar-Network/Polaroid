package net.polar.entity;

import com.mongodb.annotations.NotThreadSafe;
import net.kyori.adventure.text.Component;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.metadata.other.AreaEffectCloudMeta;
import net.minestom.server.entity.metadata.other.ArmorStandMeta;
import net.minestom.server.instance.Instance;
import net.polar.utils.chat.ChatColor;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * A lightweight holograms implementation that supports multiple lines.
 */
public class MultiLineHologram {

    private final List<Component> lines;

    private final List<Entity> entities;

    /**
     * Constructs a new MultiLineHologram.
     * @param lines The lines to use.
     */
    public MultiLineHologram(@NotNull List<String> lines) {
        this.lines = ChatColor.color(lines);
        this.entities = new ArrayList<>(lines.size());
    }

    /**
     * Constructs a new MultiLineHologram.
     * @param lines The lines to use.
     */
    public MultiLineHologram(@NotNull String... lines) {
        this.lines = ChatColor.color(lines);
        this.entities = new ArrayList<>(lines.length);
    }

    /**
     * Removes the hologram entirely.
     */
    public void remove() {
        entities.forEach(Entity::remove);
        entities.clear();
    }

    /**
     * Sets the given index to the given line.
     * @param index The index to set.
     * @param line The line to set.
     */
    public void setLine(int index, @NotNull Component line) {
        this.lines.set(index, line);
        this.entities.get(index).setCustomName(line);
    }

    /**
     * Sets the given index to the given line.
     * @param index The index to set.
     * @param line The line to set.
     */
    public void setLine(int index, @NotNull String line) {
        this.setLine(index, ChatColor.color(line));
    }

    /**
     * Adds a line to the hologram.
     * @param line The line to add.
     */
    public void addLine(@NotNull Component line) {
        this.setLine(lines.size(), line);
    }

    /**
     * Adds a line to the hologram.
     * @param line The line to add.
     */
    public void addLine(@NotNull String line) {
        this.addLine(ChatColor.color(line));
    }

    /**
     * Creates a hologram using the area effect cloud method. This is much faster for the client to render but is seen from closer.
     * @param instance The instance to create the hologram in.
     * @param position The position to create the hologram at.
     */
    public void create(@NotNull Instance instance, @NotNull Pos position) {
        for (int i = 0; i < lines.size(); i++) {
            Entity entity = new Entity(EntityType.AREA_EFFECT_CLOUD);
            AreaEffectCloudMeta meta = (AreaEffectCloudMeta) entity.getEntityMeta();

            meta.setNotifyAboutChanges(false);

            meta.setHasNoGravity(true);
            meta.setInvisible(true);
            meta.setCustomName(lines.get(i));
            meta.setCustomNameVisible(true);
            meta.setRadius(0);

            meta.setNotifyAboutChanges(true);
            double yLevel = 0.5 + (0.3 * (lines.size() - i));
            entity.setInstance(instance, position.add(0, yLevel, 0)).thenRun(() -> entities.add(entity));
        }
    }

    /**
     * Creates a hologram using the traditional armor stand method. This is slower for the client to render but is seen from further away.
     * @param instance The instance to create the hologram in.
     * @param position The position to create the hologram at.
     */
    public void createLarger(@NotNull Instance instance, @NotNull Pos position) {
        for (int i = 0; i < lines.size(); i++) {
            Entity entity = new Entity(EntityType.ARMOR_STAND);
            ArmorStandMeta meta = (ArmorStandMeta) entity.getEntityMeta();

            meta.setNotifyAboutChanges(false);

            meta.setHasNoGravity(true);
            meta.setInvisible(true);
            meta.setCustomName(lines.get(i));
            meta.setCustomNameVisible(true);
            meta.setSmall(true);
            meta.setMarker(true);
            meta.setHasNoBasePlate(true);

            meta.setNotifyAboutChanges(true);
            double yLevel = 0.5 + (0.3 * (lines.size() - i));
            entity.setInstance(instance, position.add(0, yLevel, 0)).thenRun(() -> entities.add(entity));
        }
    }

    /**
     * Has the hologram ride an entity.
     * @param instance The instance to create the hologram in.
     * @param toRide The entity to ride.
     */
    public void ride(@NotNull Instance instance, @NotNull Entity toRide) {
        remove();
        create(instance, toRide.getPosition().add(0, 1, 0));
        for (int i = 0; i < entities.size(); i++) {
            Entity entity = entities.get(i);
            if (i == 0) {
                toRide.addPassenger(entity);
            }
            else {
                entities.get(i - 1).addPassenger(entity);
            }
        }
    }
}
