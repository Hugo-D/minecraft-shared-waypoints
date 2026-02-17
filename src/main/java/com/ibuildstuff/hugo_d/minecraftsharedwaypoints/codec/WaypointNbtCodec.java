package com.ibuildstuff.hugo_d.minecraftsharedwaypoints.codec;

import com.ibuildstuff.hugo_d.minecraftsharedwaypoints.data.WaypointDTO;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import xaero.hud.minimap.waypoint.WaypointColor;
import xaero.hud.minimap.waypoint.WaypointPurpose;

import java.util.UUID;

public final class WaypointNbtCodec {

    public static CompoundTag encode(WaypointDTO wp) {
        CompoundTag tag = new CompoundTag();

        tag.putInt("x", wp.getX());
        tag.putInt("y", wp.getY());
        tag.putInt("z", wp.getZ());

        tag.putString("name", wp.getName());
        tag.putString("initials", wp.getInitials());

        tag.putInt("color", wp.getWaypointColor().ordinal());
        tag.putInt("purpose", wp.getPurpose().ordinal());

        tag.putBoolean("temporary", wp.isTemporary());
        tag.putBoolean("yIncluded", wp.isYIncluded());

        tag.putUUID("id", wp.getId());
        tag.putUUID("owner", wp.getOwner());
        tag.putString("dimension", wp.getDimension().location().toString());

        return tag;
    }

    public static WaypointDTO decode(CompoundTag tag) {
        int x = tag.getInt("x");
        int y = tag.getInt("y");
        int z = tag.getInt("z");

        String name = tag.getString("name");
        String initials = tag.getString("initials");

        WaypointColor color = WaypointColor.values()[tag.getInt("color")];
        WaypointPurpose purpose = WaypointPurpose.values()[tag.getInt("purpose")];

        boolean temporary = tag.getBoolean("temporary");
        boolean yIncluded = tag.getBoolean("yIncluded");

        UUID id = tag.getUUID("id");
        UUID owner = tag.getUUID("owner");
        ResourceKey<Level> dimension = ResourceKey.create(Registries.DIMENSION, ResourceLocation.parse(tag.getString("dimension")));

        return new WaypointDTO(
            x, y, z, name, initials, color, purpose, temporary, yIncluded, id, owner, dimension
        );
    }
}
