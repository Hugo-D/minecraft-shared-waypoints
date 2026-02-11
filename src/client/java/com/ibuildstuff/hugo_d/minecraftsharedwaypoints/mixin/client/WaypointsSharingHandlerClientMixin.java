package com.ibuildstuff.hugo_d.minecraftsharedwaypoints.mixin.client;

import com.ibuildstuff.hugo_d.minecraftsharedwaypoints.SharedWaypointsLogger;
import com.ibuildstuff.hugo_d.minecraftsharedwaypoints.network.ShareWaypointC2SPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xaero.common.minimap.waypoints.Waypoint;
import xaero.hud.minimap.waypoint.WaypointSharingHandler;
import xaero.hud.minimap.world.MinimapWorld;

@Mixin(WaypointSharingHandler.class)
public class WaypointsSharingHandlerClientMixin {

    @Inject(method = "shareWaypoint", at = @At("TAIL"))
    private void onShareWaypoint(Screen currentScreen, Waypoint waypoint, MinimapWorld minimapWorld, CallbackInfo ci) {

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) {
            return;
        }

        String dim = mc.level.dimension().location().toString();

        ShareWaypointC2SPayload payload = new ShareWaypointC2SPayload(
            waypoint.getName(),
            waypoint.getX(),
            waypoint.getY(),
            waypoint.getZ(),
            dim
        );

        ClientPlayNetworking.send(payload);
        SharedWaypointsLogger.info("Sent shared waypoint C2S: " + waypoint.getName());
    }
}