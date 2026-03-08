package com.ibuildstuff.hugo_d.minecraftsharedwaypoints.client.observer;

public final class LocalWaypointOverrideListener extends AListener<LocalWaypointOverrideListener.Event> {
    public static final LocalWaypointOverrideListener INSTANCE = new LocalWaypointOverrideListener();

    private LocalWaypointOverrideListener() {
        super("Error while firing local waypoint override change event");
    }
}
