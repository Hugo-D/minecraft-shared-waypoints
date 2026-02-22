package com.ibuildstuff.hugo_d.minecraftsharedwaypoints.client.network;

import com.ibuildstuff.hugo_d.minecraftsharedwaypoints.client.data.ClientSharedWaypointsState;
import com.ibuildstuff.hugo_d.minecraftsharedwaypoints.network.payload.SyncSharedWaypointsS2CPayload;

public final class ClientSharedWaypointsSync {

    private ClientSharedWaypointsSync() {
    }

    public static void handleDelta(SyncSharedWaypointsS2CPayload payload) {
        int serverVersion = payload.serverVersion();
        int clientVersion = ClientSharedWaypointsState.getClientVersion();

        //TODO reworked on 7.9: 1. FULL SYNC (serverVersion == 0 or explicit full sync)
//        if (payload.addedWaypoints() == null &&
//            payload.updatedWaypoints() == null &&
//            payload.removedWaypoints() == null) {
//
//            // full sync payload must contain a full state map
//            Map<UUID, WaypointDTO> fullState = payload.fullState();
//            ClientSharedWaypointsState.replaceAll(fullState, serverVersion);
//            return;
//        }

        // 2. DELTA SYNC (normal case)
        if (serverVersion > clientVersion) {
            ClientSharedWaypointsState.applyDelta(
                payload.serverVersion(),
                payload.addedWaypoints(),
                payload.updatedWaypoints(),
                payload.removedWaypoints()
            );
            return;
        }

        // 3. CLIENT ALREADY UP TO DATE
        if (serverVersion == clientVersion) {
        }

        //TODO rollback detection may be possible since full sync of 7.9: 4. ROLLBACK DETECTED (clientVersion > serverVersion)
        //  -> request a full sync
        //  RequestSyncSender.requestFullSync();
    }
}
