package com.ibuildstuff.hugo_d.minecraftsharedwaypoints.client.data;

import com.ibuildstuff.hugo_d.minecraftsharedwaypoints.data.WaypointDTO;

public final class ClientSharedWaypointMerger {

    private ClientSharedWaypointMerger() {
    }

    public static WaypointDTO merge(WaypointDTO serverDto) {

        // Clone the server DTO (deep copy)
        WaypointDTO merged = new WaypointDTO(serverDto);

        LocalWaypointOverride override = LocalWaypointOverridesState.get(serverDto.getId());
        if (override == null) return merged;

        // Apply overrides only where they exist
        if (override.getName() != null) merged.setName(override.getName());
        if (override.getInitials() != null) merged.setInitials(override.getInitials());
        if (override.getWaypointColor() != null) merged.setWaypointColor(override.getWaypointColor());

        if (override.getTemporary() != null) merged.setTemporary(override.getTemporary());
        if (override.getYIncluded() != null) merged.setYIncluded(override.getYIncluded());

        return merged;
    }
}
