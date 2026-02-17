package com.ibuildstuff.hugo_d.minecraftsharedwaypoints;

import com.ibuildstuff.hugo_d.minecraftsharedwaypoints.network.payload.ShareWaypointC2SPayload;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerPlayer;

public class SharedWaypointsMod implements ModInitializer {

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

                SharedWaypointsNetworking.handleShareWaypoint(payload, player);
            }
        );

        // Sync shared removedWaypoints to player on join
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            ServerPlayer player = handler.player;

            SharedWaypointsNetworking.syncTo(player, player.level().dimension());
        });
    }

}