package net.polar.entity;

import net.kyori.adventure.text.Component;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.metadata.other.ArmorStandMeta;
import net.minestom.server.instance.Instance;
import net.polar.utils.chat.ChatColor;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class MultiLineHologram {

    private final List<Component> lines;

    private final List<Entity> entities;

    public MultiLineHologram(@NotNull List<String> lines) {
        this.lines = ChatColor.color(lines);
        this.entities = new ArrayList<>(lines.size());
    }

    public MultiLineHologram(@NotNull String... lines) {
        this.lines = ChatColor.color(lines);
        this.entities = new ArrayList<>(lines.length);
    }

    public void remove() {
        entities.forEach(Entity::remove);
        entities.clear();
    }

    public void setLine(int index, @NotNull Component line) {
        this.lines.set(index, line);
        this.entities.get(index).setCustomName(line);
    }

    public void setLine(int index, @NotNull String line) {
        this.setLine(index, ChatColor.color(line));
    }

    public void create(@NotNull Instance instance, @NotNull Pos position) {
        for (int i = 0; i < lines.size(); i++) {
            Entity entity = new Entity(EntityType.ARMOR_STAND);
            ArmorStandMeta meta = (ArmorStandMeta) entity.getEntityMeta();

            meta.setNotifyAboutChanges(false);

            meta.setSmall(true);
            meta.setHasNoGravity(true);
            meta.setHasNoBasePlate(true);
            meta.setMarker(true);
            meta.setInvisible(true);
            meta.setCustomName(lines.get(i));
            meta.setCustomNameVisible(true);

            meta.setNotifyAboutChanges(true);

            double yLevel = 0.5 + (0.3 * (lines.size() - i));
            entity.setInstance(instance, position.add(0, yLevel, 0)).thenRun(() -> entities.add(entity));
        }
    }

    public void ride(@NotNull Instance instance, @NotNull Entity toRide) {
        create(instance, toRide.getPosition());
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
