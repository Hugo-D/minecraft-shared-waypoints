package com.ibuildstuff.hugo_d.minecraftsharedwaypoints.codec;

import com.ibuildstuff.hugo_d.minecraftsharedwaypoints.data.WaypointDTO;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import xaero.hud.minimap.waypoint.WaypointColor;
import xaero.hud.minimap.waypoint.WaypointPurpose;

import java.util.UUID;

public final class WaypointCodec {

    public static void encode(WaypointDTO wp, FriendlyByteBuf buf) {
        buf.writeInt(wp.getX());
        buf.writeInt(wp.getY());
        buf.writeInt(wp.getZ());

        buf.writeUtf(wp.getName());
        buf.writeUtf(wp.getInitials());

        buf.writeVarInt(wp.getWaypointColor().ordinal());
        buf.writeVarInt(wp.getPurpose().ordinal());

        buf.writeBoolean(wp.isTemporary());
        buf.writeBoolean(wp.isYIncluded());

        buf.writeUUID(wp.getId());
        buf.writeUUID(wp.getOwner());
        buf.writeResourceLocation(wp.getDimension().location());
    }

    public static WaypointDTO decode(FriendlyByteBuf buf) {
        int x = buf.readInt();
        int y = buf.readInt();
        int z = buf.readInt();

        String name = buf.readUtf();
        String initials = buf.readUtf();

        WaypointColor color = WaypointColor.values()[buf.readVarInt()];
        WaypointPurpose purpose = WaypointPurpose.values()[buf.readVarInt()];

        boolean temporary = buf.readBoolean();
        boolean yIncluded = buf.readBoolean();

        UUID id = buf.readUUID();
        UUID owner = buf.readUUID();
        ResourceKey<Level> dimension = ResourceKey.create(Registries.DIMENSION, buf.readResourceLocation());

        return new WaypointDTO(
            x, y, z, name, initials, color, purpose, temporary, yIncluded, id, owner, dimension
        );
    }
}
