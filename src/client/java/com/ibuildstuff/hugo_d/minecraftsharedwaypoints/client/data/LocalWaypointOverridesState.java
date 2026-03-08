package com.ibuildstuff.hugo_d.minecraftsharedwaypoints.client.data;

import com.ibuildstuff.hugo_d.minecraftsharedwaypoints.client.observer.AListener;
import com.ibuildstuff.hugo_d.minecraftsharedwaypoints.client.observer.ClientSharedWaypointListener;
import com.ibuildstuff.hugo_d.minecraftsharedwaypoints.client.observer.LocalWaypointOverrideListener;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public final class LocalWaypointOverridesState {

    private static final Map<UUID, LocalWaypointOverride> OVERRIDES = new HashMap<>();

    private LocalWaypointOverridesState() {
        ClientSharedWaypointListener.INSTANCE.subscribe(event -> {
            if (event.operation() == ClientSharedWaypointListener.Operation.REMOVE) {
                for (UUID idToRemove : event.waypointIds()) {
                    remove(idToRemove);
                }
            }
        });
    }

    public static void replaceAll(Map<UUID, LocalWaypointOverride> map) {
        OVERRIDES.clear();
        OVERRIDES.putAll(map);
        LocalWaypointOverrideListener.INSTANCE.next(
            new LocalWaypointOverrideListener.Event(AListener.Operation.FULL_SYNC, map.keySet())
        );
    }

    public static LocalWaypointOverride getOrCreate(UUID id) {
        return OVERRIDES.computeIfAbsent(id, LocalWaypointOverride::new);
    }

    public static void update(UUID id, Consumer<LocalWaypointOverride> editLocalOverride) {
        LocalWaypointOverride localOverride = getOrCreate(id);
        editLocalOverride.accept(localOverride);
        LocalWaypointOverrideListener.INSTANCE.next(
            new LocalWaypointOverrideListener.Event(AListener.Operation.UPDATE, Collections.singleton(id))
        );
    }

    public static void remove(UUID id) {
        OVERRIDES.remove(id);
        LocalWaypointOverrideListener.INSTANCE.next(
            new LocalWaypointOverrideListener.Event(AListener.Operation.REMOVE, Collections.singleton(id))
        );
    }

    public static LocalWaypointOverride get(UUID id) {
        return OVERRIDES.get(id);
    }

    public static Map<UUID, LocalWaypointOverride> getAll() {
        return OVERRIDES;
    }
}
