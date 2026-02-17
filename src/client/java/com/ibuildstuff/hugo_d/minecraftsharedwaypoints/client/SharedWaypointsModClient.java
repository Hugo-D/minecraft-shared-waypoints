package com.ibuildstuff.hugo_d.minecraftsharedwaypoints.client;

import com.ibuildstuff.hugo_d.minecraftsharedwaypoints.SharedWaypointsLogger;
import com.ibuildstuff.hugo_d.minecraftsharedwaypoints.client.storage.SharedWaypointsClientStorage;
import com.ibuildstuff.hugo_d.minecraftsharedwaypoints.network.payload.SyncSharedWaypointsS2CPayload;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.client.Minecraft;

import static net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking.registerGlobalReceiver;

public class SharedWaypointsModClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        PayloadTypeRegistry.playS2C().register(
            SyncSharedWaypointsS2CPayload.TYPE,
            SyncSharedWaypointsS2CPayload.CODEC
        );

        registerGlobalReceiver(
            SyncSharedWaypointsS2CPayload.TYPE,
            (payload, context) -> Minecraft.getInstance().execute(() -> {
                SharedWaypointsClientStorage.update(payload.removedWaypoints());
                SharedWaypointsLogger.info("Shared Waypoints client loaded and synced {} shared removedWaypoints.", SharedWaypointsClientStorage.getWaypoints(payload.dimension()).size());
            })
        );
    }
}