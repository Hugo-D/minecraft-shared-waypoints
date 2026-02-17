package com.ibuildstuff.hugo_d.minecraftsharedwaypoints.client.mixin;

import com.ibuildstuff.hugo_d.minecraftsharedwaypoints.SharedWaypointsLogger;
import com.ibuildstuff.hugo_d.minecraftsharedwaypoints.network.payload.ShareWaypointC2SPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xaero.common.minimap.waypoints.Waypoint;
import xaero.hud.minimap.waypoint.WaypointSharingHandler;

@Mixin(WaypointSharingHandler.class)
public class WaypointsSharingHandlerClientMixin {

    @Shadow
    private Waypoint sharedWaypoint;

    @Inject(method = "onShareConfirmationResult", at = @At("HEAD"), cancellable = true)
    private void onShareWaypoint(boolean confirmed, CallbackInfo ci) {

        Minecraft mc = Minecraft.getInstance();
        if (!confirmed || mc.player == null || mc.level == null) {
            return;
        }

        ResourceKey<Level> dim = mc.level.dimension();

        ShareWaypointC2SPayload payload = new ShareWaypointC2SPayload(
            dim,
            sharedWaypoint
        );

        ClientPlayNetworking.send(payload);
        SharedWaypointsLogger.info("Sent shared waypoint C2S: " + sharedWaypoint.getName());
        mc.setScreen(null);
        ci.cancel();
    }
}