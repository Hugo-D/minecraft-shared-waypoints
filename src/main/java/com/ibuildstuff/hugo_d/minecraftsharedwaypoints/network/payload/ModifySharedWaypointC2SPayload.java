package com.ibuildstuff.hugo_d.minecraftsharedwaypoints.network.payload;

import com.ibuildstuff.hugo_d.minecraftsharedwaypoints.codec.WaypointCodec;
import com.ibuildstuff.hugo_d.minecraftsharedwaypoints.data.ChangeType;
import com.ibuildstuff.hugo_d.minecraftsharedwaypoints.data.WaypointDTO;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

import static com.ibuildstuff.hugo_d.minecraftsharedwaypoints.SharedWaypointsLogger.MOD_ID;

public record ModifySharedWaypointC2SPayload(
    ChangeType changeType,
    @Nullable WaypointDTO waypoint,
    @Nullable UUID waypointId
) implements CustomPacketPayload {
    public static final ResourceLocation MODIFY_SHARED_WAYPOINT = ResourceLocation.fromNamespaceAndPath(MOD_ID, "modify_shared_waypoint");

    public static final CustomPacketPayload.Type<ModifySharedWaypointC2SPayload> TYPE =
        new CustomPacketPayload.Type<>(MODIFY_SHARED_WAYPOINT);

    public static final StreamCodec<FriendlyByteBuf, ModifySharedWaypointC2SPayload> CODEC = new StreamCodec<>() {
        @Override
        public @NotNull ModifySharedWaypointC2SPayload decode(FriendlyByteBuf buf) {
            return ModifySharedWaypointC2SPayload.read(buf);
        }

        @Override
        public void encode(FriendlyByteBuf buf, ModifySharedWaypointC2SPayload payload) {
            payload.write(buf);
        }
    };

    public static ModifySharedWaypointC2SPayload read(FriendlyByteBuf buf) {
        ChangeType changeType = buf.readEnum(ChangeType.class);

        return switch (changeType) {
            case ADD, UPDATE -> {
                WaypointDTO dto = WaypointCodec.decode(buf);
                yield new ModifySharedWaypointC2SPayload(changeType, dto, null);
            }
            case REMOVE -> {
                UUID id = buf.readUUID();
                yield new ModifySharedWaypointC2SPayload(changeType, null, id);
            }
        };
    }

    @Override
    public @NotNull CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeEnum(changeType);

        switch (changeType) {
            case ADD, UPDATE -> {
                if (waypoint == null)
                    throw new IllegalStateException("Creating or updating a waypoint without the waypoint itself is not allowed.");
                WaypointCodec.encode(waypoint, buf);
            }
            case REMOVE -> {
                if (waypointId == null)
                    throw new IllegalStateException("Removing a waypoint without the ID is not allowed.");
                buf.writeUUID(waypointId);
            }
        }
    }
}
