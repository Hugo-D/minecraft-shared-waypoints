package com.ibuildstuff.hugo_d.minecraftsharedwaypoints;

import com.ibuildstuff.hugo_d.minecraftsharedwaypoints.data.SharedWaypointsEntry;
import com.ibuildstuff.hugo_d.minecraftsharedwaypoints.network.SharedWaypointsNetworking;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.client.Minecraft;

import java.util.ArrayList;
import java.util.List;

import static net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking.registerGlobalReceiver;

public class SharedWaypointsModClient implements ClientModInitializer {

    public static List<SharedWaypointsEntry> CLIENT_SHARED = new ArrayList<>();

    @Override
    public void onInitializeClient() {
        PayloadTypeRegistry.playS2C().register(
            SharedWaypointsNetworking.SyncSharedWaypointsPayload.TYPE,
            SharedWaypointsNetworking.SyncSharedWaypointsPayload.CODEC
        );

        registerGlobalReceiver(
            SharedWaypointsNetworking.SyncSharedWaypointsPayload.TYPE,
            (payload, context) -> Minecraft.getInstance().execute(() -> {
                CLIENT_SHARED = payload.entries();
                SharedWaypointsLogger.info("Shared Waypoints client loaded and synced {} shared waypoints.", CLIENT_SHARED.size());
            })
        );
    }
}