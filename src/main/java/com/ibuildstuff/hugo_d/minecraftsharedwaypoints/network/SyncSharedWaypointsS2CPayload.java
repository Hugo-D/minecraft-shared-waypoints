package com.ibuildstuff.hugo_d.minecraftsharedwaypoints.network;

import com.ibuildstuff.hugo_d.minecraftsharedwaypoints.data.SharedWaypointsEntry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record SyncSharedWaypointsS2CPayload(List<SharedWaypointsEntry> entries) implements CustomPacketPayload {

    public static final Type<SyncSharedWaypointsS2CPayload> TYPE = new Type<>(SharedWaypointsNetworking.SYNC_SHARED_WAYPOINTS);

    public static final StreamCodec<RegistryFriendlyByteBuf, SyncSharedWaypointsS2CPayload> CODEC = new StreamCodec<>() {
        @Override
        public @NotNull SyncSharedWaypointsS2CPayload decode(RegistryFriendlyByteBuf buf) {
            return SyncSharedWaypointsS2CPayload.read(buf);
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buf, SyncSharedWaypointsS2CPayload payload) {
            payload.write(buf);
        }
    };

    public static SyncSharedWaypointsS2CPayload read(FriendlyByteBuf buf) {
        int size = buf.readVarInt();
        List<SharedWaypointsEntry> list = new java.util.ArrayList<>(size);

        for (int i = 0; i < size; i++) {
            String name = buf.readUtf();
            int x = buf.readInt();
            int y = buf.readInt();
            int z = buf.readInt();
            String dim = buf.readUtf();
            java.util.UUID sender = buf.readUUID();
            long ts = buf.readLong();

            list.add(new SharedWaypointsEntry(name, x, y, z, dim, sender, ts));
        }

        return new SyncSharedWaypointsS2CPayload(list);
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeVarInt(entries.size());
        for (SharedWaypointsEntry e : entries) {
            buf.writeUtf(e.name());
            buf.writeInt(e.x());
            buf.writeInt(e.y());
            buf.writeInt(e.z());
            buf.writeUtf(e.dimension());
            buf.writeUUID(e.sender());
            buf.writeLong(e.timestamp());
        }
    }
}
