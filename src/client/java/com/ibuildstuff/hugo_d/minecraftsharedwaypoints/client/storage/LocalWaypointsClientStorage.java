package com.ibuildstuff.hugo_d.minecraftsharedwaypoints.client.storage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public final class LocalWaypointsClientStorage {

    private static final List<LocalWaypointEntry> entries = new ArrayList<>();
    private static int selectedIndex = -1;
    private static int scrollOffset = 0;

    public static void update(Collection<LocalWaypointEntry> newEntries) {
        entries.clear();
        entries.addAll(newEntries);
        selectedIndex = -1;
        scrollOffset = 0;
    }

    public static List<LocalWaypointEntry> getEntries(String dimension) {
        return entries.stream().filter(e -> Objects.equals(e.dimension(), dimension)).toList();
    }

    public static int getSelectedIndex() {
        return selectedIndex;
    }

    public static void setSelectedIndex(int index) {
        if (index < 0 || index >= entries.size()) {
            selectedIndex = -1;
        } else {
            selectedIndex = index;
        }
    }

    public static int getScrollOffset() {
        return scrollOffset;
    }

    public static void addScroll(int delta) {
        scrollOffset = Math.max(0, scrollOffset + delta);
    }
}
