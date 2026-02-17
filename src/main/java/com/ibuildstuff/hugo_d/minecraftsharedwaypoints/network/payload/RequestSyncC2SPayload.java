package com.ibuildstuff.hugo_d.minecraftsharedwaypoints.network.payload;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import static com.ibuildstuff.hugo_d.minecraftsharedwaypoints.SharedWaypointsLogger.MOD_ID;

public record RequestSyncC2SPayload(int clientVersion) implements CustomPacketPayload {
    public static final ResourceLocation REQUEST_SYNC_SHARED_WAYPOINTS = ResourceLocation.fromNamespaceAndPath(MOD_ID, "request_shared_waypoints_sync");


    public static final CustomPacketPayload.Type<RequestSyncC2SPayload> TYPE =
        new CustomPacketPayload.Type<>(REQUEST_SYNC_SHARED_WAYPOINTS);

    public static final StreamCodec<FriendlyByteBuf, RequestSyncC2SPayload> CODEC = new StreamCodec<>() {
        @Override
        public @NotNull RequestSyncC2SPayload decode(FriendlyByteBuf buf) {
            return RequestSyncC2SPayload.read(buf);
        }

        @Override
        public void encode(FriendlyByteBuf buf, RequestSyncC2SPayload payload) {
            payload.write(buf);
        }
    };

    // Decode
    public static RequestSyncC2SPayload read(FriendlyByteBuf buf) {
        int clientVersion = buf.readVarInt();
        return new RequestSyncC2SPayload(clientVersion);
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    // Encode
    public void write(FriendlyByteBuf buf) {
        buf.writeVarInt(clientVersion);
    }
}
