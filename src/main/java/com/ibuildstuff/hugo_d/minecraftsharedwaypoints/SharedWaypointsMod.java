package com.ibuildstuff.hugo_d.minecraftsharedwaypoints;

import com.ibuildstuff.hugo_d.minecraftsharedwaypoints.network.handler.SharedWaypointsSyncHandler;
import com.ibuildstuff.hugo_d.minecraftsharedwaypoints.network.payload.ModifySharedWaypointC2SPayload;
import com.ibuildstuff.hugo_d.minecraftsharedwaypoints.network.payload.SyncSharedWaypointsS2CPayload;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerPlayer;

public final class SharedWaypointsMod implements ModInitializer {

    @Override
    public void onInitialize() {

        // 1. Register C2S mutation handler
        ServerPlayNetworking.registerGlobalReceiver(
            ModifySharedWaypointC2SPayload.TYPE,
            (payload, context) -> {
                ServerPlayer player = context.player();

                SharedWaypointsSyncHandler.handle(payload, player);
            }
        );

        // 2. Register S2C sync payload type
        PayloadTypeRegistry.playS2C().register(
            SyncSharedWaypointsS2CPayload.TYPE,
            SyncSharedWaypointsS2CPayload.CODEC
        );
    }
}
