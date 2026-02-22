package com.ibuildstuff.hugo_d.minecraftsharedwaypoints.client.data;


import xaero.hud.minimap.waypoint.WaypointColor;

import java.util.UUID;

public final class LocalWaypointOverride {

    private final UUID id;

    private String name;
    private String initials;
    private WaypointColor color;
    private Boolean temporary;
    private Boolean yIncluded;

    public LocalWaypointOverride(UUID id) {
        this.id = id;
    }

    public UUID getId() {
        return id;
    }

    // -------------------------
    // Getters
    // -------------------------

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInitials() {
        return initials;
    }

    public void setInitials(String initials) {
        this.initials = initials;
    }

    public WaypointColor getWaypointColor() {
        return color;
    }

    // -------------------------
    // Setters
    // -------------------------

    public Boolean getTemporary() {
        return temporary;
    }

    public void setTemporary(boolean temporary) {
        this.temporary = temporary;
    }

    public Boolean getYIncluded() {
        return yIncluded;
    }

    public void setYIncluded(boolean yIncluded) {
        this.yIncluded = yIncluded;
    }

    public void setColor(WaypointColor color) {
        this.color = color;
    }
}
