package com.ibuildstuff.hugo_d.minecraftsharedwaypoints.client.storage;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import xaero.common.minimap.waypoints.Waypoint;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class SharedWaypointsClientStorage {

    private static final List<Waypoint> sharedWaypoints = new ArrayList<>();
    private static int selectedIndex = -1;
    private static int scrollOffset = 0;

    public static void update(List<Waypoint> newWaypoints) {
        sharedWaypoints.clear();
        sharedWaypoints.addAll(newWaypoints);
        selectedIndex = -1;
    }

    public static List<Waypoint> getWaypoints(ResourceKey<Level> dimension) {
        return sharedWaypoints.stream().filter(e -> Objects.equals(e.dimension(), dimension)).toList();
    }

    public static int getSelectedIndex() {
        return selectedIndex;
    }

    public static void setSelectedIndex(int index) {
        if (index < 0 || index >= sharedWaypoints.size()) {
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
