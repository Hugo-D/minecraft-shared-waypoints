package com.ibuildstuff.hugo_d.minecraftsharedwaypoints.client.network;

import com.ibuildstuff.hugo_d.minecraftsharedwaypoints.network.payload.RequestSyncC2SPayload;
import com.ibuildstuff.hugo_d.minecraftsharedwaypoints.network.payload.SyncSharedWaypointsS2CPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public final class ClientSharedWaypointsSync {

    private static int clientVersion = 0; // TODO when does it get updated / what about client storage?

    public static void requestSync() {
        RequestSyncC2SPayload payload = new RequestSyncC2SPayload(clientVersion);

        ClientPlayNetworking.send(payload);
    }

    public static void handleDelta(SyncSharedWaypointsS2CPayload payload) {
        clientVersion = payload.serverVersion();

        // Step 7 will define how we apply added/updated/removed
        // to client-side storage + local customizations.
    }
}
