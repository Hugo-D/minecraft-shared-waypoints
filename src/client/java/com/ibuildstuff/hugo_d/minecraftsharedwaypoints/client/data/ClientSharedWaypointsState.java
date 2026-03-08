package com.ibuildstuff.hugo_d.minecraftsharedwaypoints.client.data;

import com.ibuildstuff.hugo_d.minecraftsharedwaypoints.client.observer.AListener;
import com.ibuildstuff.hugo_d.minecraftsharedwaypoints.client.observer.ClientSharedWaypointListener;
import com.ibuildstuff.hugo_d.minecraftsharedwaypoints.data.WaypointDTO;

import java.util.*;

public final class ClientSharedWaypointsState {

    private static final Map<UUID, WaypointDTO> SHARED_WAYPOINTS = new HashMap<>();
    private static int clientVersion = 0;

    private ClientSharedWaypointsState() {
    }

    // -------------------------
    // Accessors
    // -------------------------

    public static Map<UUID, WaypointDTO> getAll() {
        return Collections.unmodifiableMap(SHARED_WAYPOINTS);
    }

    public static int getClientVersion() {
        return clientVersion;
    }

    // -------------------------
    // Mutators
    // -------------------------

    public static void setClientVersion(int version) {
        clientVersion = version;
    }

    public static void replaceAll(Map<UUID, WaypointDTO> newMap, int newVersion) {
        SHARED_WAYPOINTS.clear();
        SHARED_WAYPOINTS.putAll(newMap);
        clientVersion = newVersion;
        ClientSharedWaypointListener.INSTANCE.next(
            new ClientSharedWaypointListener.Event(AListener.Operation.FULL_SYNC, newMap.keySet())
        );
    }

    public static void applyDelta(
        int serverVersion,
        List<WaypointDTO> added,
        List<WaypointDTO> updated,
        List<UUID> removed
    ) {
        // Apply ADD
        for (WaypointDTO dto : added) {
            put(dto);
        }

        // Apply UPDATE
        for (WaypointDTO dto : updated) {
            put(dto);
        }

        // Apply REMOVE
        for (UUID id : removed) {
            remove(id);
        }

        // Update version
        setClientVersion(serverVersion);

        // Notify listeners
        ClientSharedWaypointListener.INSTANCE.next(
            new ClientSharedWaypointListener.Event(AListener.Operation.ADD, added.stream().map(WaypointDTO::getId).toList())
        );
        ClientSharedWaypointListener.INSTANCE.next(
            new ClientSharedWaypointListener.Event(AListener.Operation.UPDATE, updated.stream().map(WaypointDTO::getId).toList())
        );
        ClientSharedWaypointListener.INSTANCE.next(
            new ClientSharedWaypointListener.Event(AListener.Operation.REMOVE, removed)
        );
    }

    // -------------------------
    // Internal mutation helpers
    // -------------------------

    static void put(WaypointDTO dto) {
        SHARED_WAYPOINTS.put(dto.getId(), dto);
    }

    static void remove(UUID id) {
        SHARED_WAYPOINTS.remove(id);
    }
}

