package com.ibuildstuff.hugo_d.minecraftsharedwaypoints.client.data;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public final class LocalWaypointOverridesState {

    private static final Map<UUID, LocalWaypointOverride> overrides = new HashMap<>();

    private LocalWaypointOverridesState() {
    }

    public static LocalWaypointOverride getOrCreate(UUID id) {
        return overrides.computeIfAbsent(id, LocalWaypointOverride::new);
    }

    public static void updateOverride(UUID id, Consumer<LocalWaypointOverride> editLocalOverride) {
        LocalWaypointOverride localOverride = getOrCreate(id);
        editLocalOverride.accept(localOverride);
        ClientSharedWaypointEvents.next();
    }

    public static LocalWaypointOverride get(UUID id) {
        return overrides.get(id);
    }

    public static Map<UUID, LocalWaypointOverride> getAll() {
        return overrides;
    }
}
