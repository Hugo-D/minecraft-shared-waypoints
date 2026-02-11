package com.ibuildstuff.hugo_d.minecraftsharedwaypoints.network;

import com.ibuildstuff.hugo_d.minecraftsharedwaypoints.SharedWaypointsLogger;
import com.ibuildstuff.hugo_d.minecraftsharedwaypoints.data.SharedWaypointsEntry;
import com.ibuildstuff.hugo_d.minecraftsharedwaypoints.data.SharedWaypointsState;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

import static com.ibuildstuff.hugo_d.minecraftsharedwaypoints.SharedWaypointsLogger.MOD_ID;

public class SharedWaypointsNetworking {

    public static final ResourceLocation SYNC_SHARED_WAYPOINTS = ResourceLocation.fromNamespaceAndPath(MOD_ID, "sync_shared_waypoints");

    public static void syncTo(ServerPlayer player, ServerLevel level) {
        SharedWaypointsState state = SharedWaypointsState.get(level);

        SyncSharedWaypointsS2CPayload payload =
            new SyncSharedWaypointsS2CPayload(state.getAll());

        ServerPlayNetworking.send(player, payload);

        SharedWaypointsLogger.info("Synced {} shared waypoints to {}",
            state.getAll().size(),
            player.getGameProfile().getName()
        );
    }


    public static void handleShareWaypoint(ShareWaypointC2SPayload payload, ServerPlayer player, ServerLevel level) {
        if (payload.name().isBlank()) return;
        if (Math.abs(payload.x()) > 30_000_000) return;
        if (Math.abs(payload.z()) > 30_000_000) return;

        ResourceLocation dimId = ResourceLocation.tryParse(payload.dimension());
        if (dimId == null || !level.getServer().levelKeys().contains(ResourceKey.create(Registries.DIMENSION, dimId))) {
            SharedWaypointsLogger.warn("Received waypoint with invalid dimension: " + payload.dimension());
            return;
        }

        SharedWaypointsEntry entry = new SharedWaypointsEntry(
            payload.name(),
            payload.x(),
            payload.y(),
            payload.z(),
            payload.dimension(),
            player.getUUID(),
            System.currentTimeMillis()
        );

        SharedWaypointsState state = SharedWaypointsState.get(level);
        state.add(entry);

        SharedWaypointsLogger.info("Server stored shared waypoint: {}", entry.name());

        // Broadcast added waypoint to everyone (or to relevant players)
        for (ServerPlayer target : level.getServer().getPlayerList().getPlayers()) {
            syncTo(target, level);
        }
    }
}

