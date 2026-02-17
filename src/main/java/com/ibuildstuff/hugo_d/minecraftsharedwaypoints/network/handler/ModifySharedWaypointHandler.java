package com.ibuildstuff.hugo_d.minecraftsharedwaypoints.network.handler;

import com.ibuildstuff.hugo_d.minecraftsharedwaypoints.data.SharedWaypointsState;
import com.ibuildstuff.hugo_d.minecraftsharedwaypoints.network.payload.ModifySharedWaypointC2SPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

public final class ModifySharedWaypointHandler {

    public static void handle(ModifySharedWaypointC2SPayload payload, ServerPlayer player) {
        ServerLevel level = player.serverLevel();
        SharedWaypointsState state = SharedWaypointsState.get(level);

        switch (payload.changeType()) {
            case ADD -> state.addWaypoint(payload.waypoint());
            case UPDATE -> state.updateWaypoint(payload.waypoint());
            case REMOVE -> state.removeWaypoint(payload.waypointId());
        }

        // Step 6.2 will broadcast the delta
    }
}
