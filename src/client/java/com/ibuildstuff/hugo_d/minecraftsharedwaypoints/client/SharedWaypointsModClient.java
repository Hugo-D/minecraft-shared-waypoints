package com.ibuildstuff.hugo_d.minecraftsharedwaypoints.client;

import com.ibuildstuff.hugo_d.minecraftsharedwaypoints.client.network.ClientSharedWaypointsSync;
import com.ibuildstuff.hugo_d.minecraftsharedwaypoints.client.persistence.ClientSharedWaypointsPersistence;
import com.ibuildstuff.hugo_d.minecraftsharedwaypoints.network.payload.ModifySharedWaypointC2SPayload;
import com.ibuildstuff.hugo_d.minecraftsharedwaypoints.network.payload.RequestSyncC2SPayload;
import com.ibuildstuff.hugo_d.minecraftsharedwaypoints.network.payload.SyncSharedWaypointsS2CPayload;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Path;

public final class SharedWaypointsModClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {

        // 1. Load locally persisted waypoints
        Path configDir = FabricLoader.getInstance().getConfigDir();
        ClientSharedWaypointsPersistence.initialize(configDir);

        // 2. Register S2C sync handler
        ClientPlayNetworking.registerGlobalReceiver(
            SyncSharedWaypointsS2CPayload.TYPE,
            (payload, context) -> ClientSharedWaypointsSync.handleDelta(payload)
        );

        // 3. Register C2S mutation payload type
        PayloadTypeRegistry.playC2S().register(
            ModifySharedWaypointC2SPayload.TYPE,
            ModifySharedWaypointC2SPayload.CODEC
        );

        // 4. Register C2S request-sync payload type
        PayloadTypeRegistry.playC2S().register(
            RequestSyncC2SPayload.TYPE,
            RequestSyncC2SPayload.CODEC
        );

        // 5. Initialize Xaero integration
        //TODO Step 8: XaeroIntegrationLayer.initialize();
    }
}
