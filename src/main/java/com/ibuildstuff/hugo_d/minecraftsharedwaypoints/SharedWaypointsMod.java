package com.ibuildstuff.hugo_d.minecraftsharedwaypoints;

import com.ibuildstuff.hugo_d.minecraftsharedwaypoints.data.SharedWaypointsEntry;
import com.ibuildstuff.hugo_d.minecraftsharedwaypoints.data.SharedWaypointsState;
import com.ibuildstuff.hugo_d.minecraftsharedwaypoints.network.SharedWaypointsNetworking;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.level.ServerLevel;

import java.util.UUID;

public class SharedWaypointsMod implements ModInitializer {

    private void artificallyAddState(SharedWaypointsState state) {
        state.add(
            new SharedWaypointsEntry("Test", 0, 0, 0, "minecraft:overworld", UUID.randomUUID(), 0)
        );
    }

    @Override
    public void onInitialize() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            ServerLevel level = server.overworld();
            SharedWaypointsState state = SharedWaypointsState.get(level);
            artificallyAddState(state);
            SharedWaypointsLogger.info("Shared Waypoints loaded: {}", state.getAll().size());
            SharedWaypointsNetworking.sendTo(handler.player, state.getAll());
        });
    }
}