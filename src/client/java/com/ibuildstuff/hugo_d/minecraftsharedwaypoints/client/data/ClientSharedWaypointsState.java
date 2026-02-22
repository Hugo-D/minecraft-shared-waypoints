package com.ibuildstuff.hugo_d.minecraftsharedwaypoints.client.data;

import com.ibuildstuff.hugo_d.minecraftsharedwaypoints.data.WaypointDTO;

import java.util.*;

public final class ClientSharedWaypointsState {

    private static final Map<UUID, WaypointDTO> sharedWaypoints = new HashMap<>();
    private static int clientVersion = 0;

    private ClientSharedWaypointsState() {
    }

    // -------------------------
    // Accessors
    // -------------------------

    public static Map<UUID, WaypointDTO> getSharedWaypoints() {
        return Collections.unmodifiableMap(sharedWaypoints);
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
        sharedWaypoints.clear();
        sharedWaypoints.putAll(newMap);
        clientVersion = newVersion;
        ClientSharedWaypointEvents.next();
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
        ClientSharedWaypointEvents.next();
    }

    // -------------------------
    // Internal mutation helpers
    // -------------------------

    static void put(WaypointDTO dto) {
        sharedWaypoints.put(dto.getId(), dto);
    }

    static void remove(UUID id) {
        sharedWaypoints.remove(id);
    }
}

