package com.ibuildstuff.hugo_d.minecraftsharedwaypoints.network.payload;

import com.ibuildstuff.hugo_d.minecraftsharedwaypoints.codec.WaypointCodec;
import com.ibuildstuff.hugo_d.minecraftsharedwaypoints.data.WaypointDTO;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import static com.ibuildstuff.hugo_d.minecraftsharedwaypoints.SharedWaypointsLogger.MOD_ID;

public record ShareWaypointC2SPayload(ResourceKey<Level> dimension,
                                      WaypointDTO waypoint) implements CustomPacketPayload {
    public static final ResourceLocation ADD_SHARED_WAYPOINTS = ResourceLocation.fromNamespaceAndPath(MOD_ID, "add_shared_waypoints");

    public static final CustomPacketPayload.Type<ShareWaypointC2SPayload> TYPE =
        new CustomPacketPayload.Type<>(ADD_SHARED_WAYPOINTS);

    public static final StreamCodec<FriendlyByteBuf, ShareWaypointC2SPayload> CODEC = new StreamCodec<>() {
        @Override
        public @NotNull ShareWaypointC2SPayload decode(FriendlyByteBuf buf) {
            return ShareWaypointC2SPayload.read(buf);
        }

        @Override
        public void encode(FriendlyByteBuf buf, ShareWaypointC2SPayload payload) {
            payload.write(buf);
        }
    };

    // Decode
    public static ShareWaypointC2SPayload read(FriendlyByteBuf buf) {
        ResourceKey<Level> dimension = ResourceKey.create(Registries.DIMENSION, buf.readResourceLocation());
        WaypointDTO waypoint = WaypointCodec.decode(buf);
        return new ShareWaypointC2SPayload(dimension, waypoint);
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    // Encode
    public void write(FriendlyByteBuf buf) {
        buf.writeResourceLocation(dimension.location());
        WaypointCodec.encode(waypoint, buf);
    }
}
