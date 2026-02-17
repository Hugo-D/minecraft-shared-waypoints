package com.ibuildstuff.hugo_d.minecraftsharedwaypoints.data;

import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public record ChangeEntry(
    int version,
    ChangeType type,
    UUID waypointId,
    @Nullable WaypointDTO waypoint // null for REMOVE
) {
}
