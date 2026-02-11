package com.ibuildstuff.hugo_d.minecraftsharedwaypoints.data;

import xaero.common.minimap.waypoints.Waypoint;

import java.util.Objects;
import java.util.UUID;

public record SharedWaypointsEntry(
        String name,
        int x, int y, int z,
        String dimension,
        UUID sender,
        long timestamp
) {
    public static Waypoint toLocalWaypoint(SharedWaypointsEntry e) {
        Waypoint w = new Waypoint(
            e.x(), e.y(), e.z(),
            e.name(),
            "S", // initials
            0 // color index (we can store this locally later)
        );
        return w;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        SharedWaypointsEntry that = (SharedWaypointsEntry) o;
        return x == that.x && y == that.y && z == that.z && Objects.equals(dimension, that.dimension);
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, z, dimension);
    }

}

