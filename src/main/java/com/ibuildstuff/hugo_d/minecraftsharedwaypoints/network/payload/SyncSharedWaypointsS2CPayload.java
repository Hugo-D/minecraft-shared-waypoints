package com.ibuildstuff.hugo_d.minecraftsharedwaypoints.network.payload;

import com.ibuildstuff.hugo_d.minecraftsharedwaypoints.codec.WaypointCodec;
import com.ibuildstuff.hugo_d.minecraftsharedwaypoints.data.WaypointDTO;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.ibuildstuff.hugo_d.minecraftsharedwaypoints.SharedWaypointsLogger.MOD_ID;

public record SyncSharedWaypointsS2CPayload(
    int serverVersion,
    List<WaypointDTO> addedWaypoints,
    List<WaypointDTO> updatedWaypoints,
    List<UUID> removedWaypoints
) implements CustomPacketPayload {
    public static final ResourceLocation SYNC_SHARED_WAYPOINTS = ResourceLocation.fromNamespaceAndPath(MOD_ID, "sync_shared_waypoints");

    public static final Type<SyncSharedWaypointsS2CPayload> TYPE =
        new Type<>(SYNC_SHARED_WAYPOINTS);

    public static final StreamCodec<FriendlyByteBuf, SyncSharedWaypointsS2CPayload> CODEC = new StreamCodec<>() {
        @Override
        public @NotNull SyncSharedWaypointsS2CPayload decode(FriendlyByteBuf buf) {
            return SyncSharedWaypointsS2CPayload.read(buf);
        }

        @Override
        public void encode(FriendlyByteBuf buf, SyncSharedWaypointsS2CPayload payload) {
            payload.write(buf);
        }
    };

    // Decode (client reconstructs the payload)
    public static SyncSharedWaypointsS2CPayload read(FriendlyByteBuf buf) {
        int serverVersion = buf.readVarInt();

        int addedSize = buf.readVarInt();
        List<WaypointDTO> addedWaypoints = new ArrayList<>(addedSize);
        for (int i = 0; i < addedSize; i++) {
            addedWaypoints.add(WaypointCodec.decode(buf));
        }

        int updatedSize = buf.readVarInt();
        List<WaypointDTO> updatedWaypoints = new ArrayList<>(updatedSize);
        for (int i = 0; i < updatedSize; i++) {
            updatedWaypoints.add(WaypointCodec.decode(buf));
        }

        int removedSize = buf.readVarInt();
        List<UUID> removedWaypoints = new ArrayList<>(removedSize);
        for (int i = 0; i < removedSize; i++) {
            removedWaypoints.add(buf.readUUID());
        }

        return new SyncSharedWaypointsS2CPayload(serverVersion, addedWaypoints, updatedWaypoints, removedWaypoints);
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    // Encode (server → client)
    public void write(FriendlyByteBuf buf) {
        buf.writeVarInt(serverVersion);

        buf.writeVarInt(addedWaypoints.size());
        for (WaypointDTO wp : addedWaypoints) {
            WaypointCodec.encode(wp, buf);
        }

        buf.writeVarInt(updatedWaypoints.size());
        for (WaypointDTO wp : updatedWaypoints) {
            WaypointCodec.encode(wp, buf);
        }

        buf.writeVarInt(removedWaypoints.size());
        for (UUID id : removedWaypoints) {
            buf.writeUUID(id);
        }
    }
}
