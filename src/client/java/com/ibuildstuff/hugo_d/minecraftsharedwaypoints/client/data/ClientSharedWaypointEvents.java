package com.ibuildstuff.hugo_d.minecraftsharedwaypoints.client.data;

import com.ibuildstuff.hugo_d.minecraftsharedwaypoints.SharedWaypointsLogger;

import java.util.ArrayList;
import java.util.List;

public final class ClientSharedWaypointEvents {

    private static final List<Runnable> listeners = new ArrayList<>();

    private ClientSharedWaypointEvents() {
    }

    public static void subscribe(Runnable listener) {
        listeners.add(listener);
    }

    public static void unsubscribe(Runnable listener) {
        listeners.remove(listener);
    }

    public static void clear() {
        listeners.clear();
    }

    public static void next() {
        for (Runnable listener : listeners) {
            try {
                listener.run();
            } catch (Exception e) {
                SharedWaypointsLogger.error("Error while firing shared waypoint change event", e);
            }
        }
    }
}

