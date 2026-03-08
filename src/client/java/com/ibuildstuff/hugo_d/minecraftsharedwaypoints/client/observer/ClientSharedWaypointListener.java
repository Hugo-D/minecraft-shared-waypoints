package com.ibuildstuff.hugo_d.minecraftsharedwaypoints.client.observer;

public final class ClientSharedWaypointListener extends AListener<ClientSharedWaypointListener.Event> {
    public static final ClientSharedWaypointListener INSTANCE = new ClientSharedWaypointListener();

    private ClientSharedWaypointListener() {
        super("Error while firing shared waypoint change event");
    }
}

