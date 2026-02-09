package com.ibuildstuff.hugo_d.minecraftsharedwaypoints.data;

import java.util.UUID;

public record SharedWaypointsEntry(
        String name,
        int x, int y, int z,
        String dimension,
        UUID sender,
        long timestamp
) {}

