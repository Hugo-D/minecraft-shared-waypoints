package com.ibuildstuff.hugo_d.minecraftsharedwaypoints.client.observer;

import com.ibuildstuff.hugo_d.minecraftsharedwaypoints.SharedWaypointsLogger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public abstract class AListener<TypeEvent> {

    private final String errorMessage;
    private final List<Consumer<TypeEvent>> listeners = new ArrayList<>();

    protected AListener(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public void subscribe(Consumer<TypeEvent> listener) {
        listeners.add(listener);
    }

    public void unsubscribe(Consumer<TypeEvent> listener) {
        listeners.remove(listener);
    }

    public void clear() {
        listeners.clear();
    }

    public void next(TypeEvent eventPayload) {
        for (Consumer<TypeEvent> listener : listeners) {
            try {
                listener.accept(eventPayload);
            } catch (Exception e) {
                SharedWaypointsLogger.error(errorMessage, e);
            }
        }
    }

    public enum Operation {
        ADD, UPDATE, REMOVE, FULL_SYNC
    }

    public record Event(Operation operation, Collection<UUID> waypointIds) {
    }
}