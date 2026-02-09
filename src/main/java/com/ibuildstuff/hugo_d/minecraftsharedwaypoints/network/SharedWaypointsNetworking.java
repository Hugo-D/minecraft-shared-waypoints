package com.ibuildstuff.hugo_d.minecraftsharedwaypoints.network;

import com.ibuildstuff.hugo_d.minecraftsharedwaypoints.data.SharedWaypointsEntry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static com.ibuildstuff.hugo_d.minecraftsharedwaypoints.SharedWaypointsLogger.MOD_ID;

public class SharedWaypointsNetworking {

    public static final ResourceLocation SYNC_SHARED_WAYPOINTS = ResourceLocation.fromNamespaceAndPath(MOD_ID, "sync_shared_waypoints");

    public record SyncSharedWaypointsPayload(List<SharedWaypointsEntry> entries) implements CustomPacketPayload {

        public static final Type<SyncSharedWaypointsPayload> TYPE = new Type<>(SYNC_SHARED_WAYPOINTS);

        public static final StreamCodec<RegistryFriendlyByteBuf, SyncSharedWaypointsPayload> CODEC = new StreamCodec<>() {
            @Override
            public @NotNull SyncSharedWaypointsPayload decode(RegistryFriendlyByteBuf buf) {
                return SyncSharedWaypointsPayload.read(buf);
            }

            @Override
            public void encode(RegistryFriendlyByteBuf buf, SyncSharedWaypointsPayload payload) {
                payload.write(buf);
            }
        };

        @Override
        public @NotNull Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }

        public static SyncSharedWaypointsPayload read(FriendlyByteBuf buf) {
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

            return new SyncSharedWaypointsPayload(list);
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

    public static void sendTo(ServerPlayer player, List<SharedWaypointsEntry> entries) {
        SyncSharedWaypointsPayload payload = new SyncSharedWaypointsPayload(entries);
        player.connection.send(new ClientboundCustomPayloadPacket(payload));
    }
}

