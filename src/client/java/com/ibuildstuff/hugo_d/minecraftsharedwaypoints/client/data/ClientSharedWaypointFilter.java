package com.ibuildstuff.hugo_d.minecraftsharedwaypoints.client.data;

import com.ibuildstuff.hugo_d.minecraftsharedwaypoints.data.WaypointDTO;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class ClientSharedWaypointFilter {

    private ClientSharedWaypointFilter() {
    }

    public static List<WaypointDTO> getMergedForDimension(ResourceKey<Level> dimension) {

        Map<UUID, WaypointDTO> shared = ClientSharedWaypointsState.getSharedWaypoints();
        List<WaypointDTO> waypointsOfDim = new ArrayList<>();

        for (WaypointDTO serverDto : shared.values()) {
            if (!serverDto.getDimension().equals(dimension)) {
                continue;
            }

            WaypointDTO merged = ClientSharedWaypointMerger.merge(serverDto);
            waypointsOfDim.add(merged);
        }

        return waypointsOfDim;
    }
}

