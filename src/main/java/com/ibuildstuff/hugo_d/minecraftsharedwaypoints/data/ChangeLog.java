package com.ibuildstuff.hugo_d.minecraftsharedwaypoints.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class ChangeLog {

    private static final List<ChangeEntry> entries = new ArrayList<>();

    private ChangeLog() {
    }

    public static void appendAddEvent(WaypointDTO dto, int version) {
        entries.add(new ChangeEntry(
            version,
            ChangeType.ADD,
            dto.getId(),
            dto
        ));
    }

    public static void appendUpdateEvent(WaypointDTO dto, int version) {
        entries.add(new ChangeEntry(
            version,
            ChangeType.UPDATE,
            dto.getId(),
            dto
        ));
    }

    public static void appendRemoveEvent(UUID id, int version) {
        entries.add(new ChangeEntry(
            version,
            ChangeType.REMOVE,
            id,
            null
        ));
    }

    public static List<ChangeEntry> getChangesSince(int clientVersion) {
        // simple, linear scan; can be optimized later if needed
        List<ChangeEntry> result = new ArrayList<>();
        for (ChangeEntry entry : entries) {
            if (entry.version() > clientVersion) {
                result.add(entry);
            }
        }
        return result;
    }

    public static void rebuildSyntheticAdds(
        Map<UUID, WaypointDTO> allWaypoints,
        int version
    ) {
        entries.clear(); // ensure clean state

        for (WaypointDTO dto : allWaypoints.values()) {
            entries.add(new ChangeEntry(
                version,
                ChangeType.ADD,
                dto.getId(),
                dto
            ));
        }
    }

}

