package com.ibuildstuff.hugo_d.minecraftsharedwaypoints.data;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import xaero.common.minimap.waypoints.Waypoint;
import xaero.hud.minimap.waypoint.WaypointColor;
import xaero.hud.minimap.waypoint.WaypointPurpose;

import java.util.UUID;

public class WaypointDTO extends Waypoint {

    private final UUID id;
    private final UUID owner;
    private final ResourceKey<Level> dimension;

    public WaypointDTO(int x, int y, int z,
                       String name, String initials,
                       WaypointColor color, WaypointPurpose purpose,
                       boolean temporary, boolean yIncluded,
                       UUID id, UUID owner, ResourceKey<Level> dimension) {
        super(x, y, z, name, initials, color, purpose, temporary, yIncluded);
        this.id = id;
        this.owner = owner;
        this.dimension = dimension;
    }

    public WaypointDTO(WaypointDTO other) {
        this(other.getX(), other.getY(), other.getZ(),
            other.getName(), other.getInitials(),
            other.getWaypointColor(), other.getPurpose(),
            other.isTemporary(), other.isYIncluded(),
            other.getId(), other.getOwner(), other.getDimension());
    }

    public UUID getId() {
        return id;
    }

    public UUID getOwner() {
        return owner;
    }

    public ResourceKey<Level> getDimension() {
        return dimension;
    }
}
