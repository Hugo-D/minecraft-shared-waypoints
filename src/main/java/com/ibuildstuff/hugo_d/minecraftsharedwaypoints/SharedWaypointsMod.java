package com.ibuildstuff.hugo_d.minecraftsharedwaypoints;

import com.ibuildstuff.hugo_d.minecraftsharedwaypoints.data.SharedWaypointsEntry;
import com.ibuildstuff.hugo_d.minecraftsharedwaypoints.data.SharedWaypointsState;
import com.ibuildstuff.hugo_d.minecraftsharedwaypoints.network.ShareWaypointC2SPayload;
import com.ibuildstuff.hugo_d.minecraftsharedwaypoints.network.SharedWaypointsNetworking;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

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
        // in your main mod init (server side)

        // Register payload C2S type
        PayloadTypeRegistry.playC2S().register(
            ShareWaypointC2SPayload.TYPE,
            ShareWaypointC2SPayload.CODEC
        );

        // Register payload C2S handler
        ServerPlayNetworking.registerGlobalReceiver(
            ShareWaypointC2SPayload.TYPE,
            (payload, context) -> {
                ServerPlayer player = context.player();
                ServerLevel level = player.serverLevel();

                SharedWaypointsNetworking.handleShareWaypoint(payload, player, level);
            }
        );

        // Sync shared waypoints to player on join
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            ServerPlayer player = handler.player;
            ServerLevel level = player.serverLevel();

            SharedWaypointsNetworking.syncTo(player, level);
        });
    }

}