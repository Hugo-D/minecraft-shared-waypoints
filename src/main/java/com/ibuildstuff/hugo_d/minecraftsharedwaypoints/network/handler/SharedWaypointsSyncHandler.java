package com.ibuildstuff.hugo_d.minecraftsharedwaypoints.network.handler;

import com.ibuildstuff.hugo_d.minecraftsharedwaypoints.data.ChangeEntry;
import com.ibuildstuff.hugo_d.minecraftsharedwaypoints.data.ChangeLog;
import com.ibuildstuff.hugo_d.minecraftsharedwaypoints.data.SharedWaypointsState;
import com.ibuildstuff.hugo_d.minecraftsharedwaypoints.data.WaypointDTO;
import com.ibuildstuff.hugo_d.minecraftsharedwaypoints.network.payload.RequestSyncC2SPayload;
import com.ibuildstuff.hugo_d.minecraftsharedwaypoints.network.payload.SyncSharedWaypointsS2CPayload;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public final class SharedWaypointsSyncHandler {

    private SharedWaypointsSyncHandler() {
    }

    public static void handle(
        RequestSyncC2SPayload payload,
        ServerPlayer player
    ) {
        ServerLevel level = player.serverLevel();
        SharedWaypointsState state = SharedWaypointsState.get(level);

        int clientVersion = payload.clientVersion();
        int serverVersion = state.getVersion();

        // If client is already up to date, send an empty delta with current version
        if (clientVersion >= serverVersion) {
            sendDelta(player, serverVersion,
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList()
            );
            return;
        }

        List<ChangeEntry> changes = ChangeLog.getChangesSince(clientVersion);

        List<WaypointDTO> added = new ArrayList<>();
        List<WaypointDTO> updated = new ArrayList<>();
        List<UUID> removed = new ArrayList<>();

        for (ChangeEntry entry : changes) {
            switch (entry.type()) {
                case ADD -> added.add(entry.waypoint());
                case UPDATE -> updated.add(entry.waypoint());
                case REMOVE -> removed.add(entry.waypointId());
            }
        }

        sendDelta(player, serverVersion, added, updated, removed);
    }

    private static void sendDelta(
        ServerPlayer player,
        int serverVersion,
        List<WaypointDTO> added,
        List<WaypointDTO> updated,
        List<UUID> removed
    ) {
        SyncSharedWaypointsS2CPayload payload =
            new SyncSharedWaypointsS2CPayload(serverVersion, added, updated, removed);

        ServerPlayNetworking.send(player, payload);
    }
}
