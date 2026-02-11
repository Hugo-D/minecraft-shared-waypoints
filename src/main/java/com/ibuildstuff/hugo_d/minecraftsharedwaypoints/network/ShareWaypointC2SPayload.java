package com.ibuildstuff.hugo_d.minecraftsharedwaypoints.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record ShareWaypointC2SPayload(String name, int x, int y, int z, String dimension)
    implements CustomPacketPayload {

    public static final ResourceLocation ID =
        ResourceLocation.fromNamespaceAndPath("sharedwaypoints", "share_waypoint");

    public static final Type<ShareWaypointC2SPayload> TYPE = new Type<>(ID);

    public static final StreamCodec<RegistryFriendlyByteBuf, ShareWaypointC2SPayload> CODEC =
        new StreamCodec<>() {
            @Override
            public @NotNull ShareWaypointC2SPayload decode(RegistryFriendlyByteBuf buf) {
                String name = buf.readUtf();
                int x = buf.readInt();
                int y = buf.readInt();
                int z = buf.readInt();
                String dim = buf.readUtf();
                return new ShareWaypointC2SPayload(name, x, y, z, dim);
            }

            @Override
            public void encode(RegistryFriendlyByteBuf buf, ShareWaypointC2SPayload payload) {
                buf.writeUtf(payload.name());
                buf.writeInt(payload.x());
                buf.writeInt(payload.y());
                buf.writeInt(payload.z());
                buf.writeUtf(payload.dimension());
            }
        };

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
