package com.ibuildstuff.hugo_d.minecraftsharedwaypoints.client.network;

import com.ibuildstuff.hugo_d.minecraftsharedwaypoints.data.ChangeType;
import com.ibuildstuff.hugo_d.minecraftsharedwaypoints.data.WaypointDTO;
import com.ibuildstuff.hugo_d.minecraftsharedwaypoints.network.payload.ModifySharedWaypointC2SPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

import java.util.UUID;

public final class ClientMutationSender {

    private ClientMutationSender() {
    }

    public static void sendAdd(WaypointDTO dto) {
        send(ChangeType.ADD, dto, null);
    }

    public static void sendUpdate(WaypointDTO dto) {
        send(ChangeType.UPDATE, dto, null);
    }

    public static void sendRemove(UUID id) {
        send(ChangeType.REMOVE, null, id);
    }

    private static void send(ChangeType changeType, WaypointDTO waypoint, UUID waypointId) {
        ClientPlayNetworking.send(new ModifySharedWaypointC2SPayload(changeType, waypoint, waypointId));
    }
}
