package com.ibuildstuff.hugo_d.minecraftsharedwaypoints;

import com.ibuildstuff.hugo_d.minecraftsharedwaypoints.data.SharedWaypointsEntry;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class SharedWaypointsClientStorage {

    // Local overrides: key = dimension:x:y:z
    private static final Map<String, LocalOverride> overrides = new HashMap<>();
    private static List<SharedWaypointsEntry> cached = List.of();
    private static int selectedIndex = -1;

    public static void update(List<SharedWaypointsEntry> list) {
        cached = list;
        selectedIndex = -1;
    }

    public static List<SharedWaypointsEntry> getEntries() {
        return cached;
    }

    public static int getSelectedIndex() {
        return selectedIndex;
    }

    public static void setSelectedIndex(int index) {
        selectedIndex = index;
    }

    public static String keyOf(SharedWaypointsEntry e) {
        return e.dimension() + ":" + e.x() + ":" + e.y() + ":" + e.z();
    }

    public static void applyOverride(String key, LocalOverride override) {
        overrides.put(key, override);
    }

    public static Optional<LocalOverride> getOverride(SharedWaypointsEntry e) {
        return Optional.ofNullable(overrides.get(keyOf(e)));
    }

    // Local override structure
    public record LocalOverride(String name, int colorIndex, boolean disabled) {
    }
}
