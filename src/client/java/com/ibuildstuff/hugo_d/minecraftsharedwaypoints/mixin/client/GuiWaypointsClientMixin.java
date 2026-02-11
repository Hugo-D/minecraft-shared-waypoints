package com.ibuildstuff.hugo_d.minecraftsharedwaypoints.mixin.client;

import com.ibuildstuff.hugo_d.minecraftsharedwaypoints.SharedWaypointsClientRenderer;
import com.ibuildstuff.hugo_d.minecraftsharedwaypoints.SharedWaypointsClientStorage;
import com.ibuildstuff.hugo_d.minecraftsharedwaypoints.data.SharedWaypointsEntry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xaero.common.HudMod;
import xaero.common.gui.GuiAddWaypoint;
import xaero.common.gui.GuiWaypoints;
import xaero.common.minimap.waypoints.Waypoint;
import xaero.hud.minimap.module.MinimapSession;
import xaero.hud.minimap.waypoint.WaypointColor;
import xaero.hud.minimap.world.MinimapWorld;

import java.util.ArrayList;

@Mixin(GuiWaypoints.class)
public class GuiWaypointsClientMixin {

    // === Shadows (legal access to private fields) ===
    @Shadow
    private MinimapWorld displayedWorld;
    @Shadow
    private MinimapSession session;
    @Final
    @Shadow
    private HudMod modMain;

    // === Tab state ===
    @Unique
    private boolean sharedwaypoints$sharedTabActive = false;

    // === TAB RENDERING ===
    @Inject(method = "renderPreDropdown", at = @At("TAIL"))
    private void sharedwaypoints$renderTabs(GuiGraphics g, int mouseX, int mouseY, float partial, CallbackInfo ci) {
        GuiWaypoints self = (GuiWaypoints) (Object) this;
        SharedWaypointsClientRenderer.renderTabs(g, self.width, Minecraft.getInstance().font, sharedwaypoints$sharedTabActive);
    }


    // === TAB CLICK HANDLING ===
    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    private void sharedwaypoints$clickTabs(double mouseX, double mouseY, int button,
                                           CallbackInfoReturnable<Boolean> cir) {
        GuiWaypoints self = (GuiWaypoints) (Object) this;
        int center = self.width / 2;
        int y = 50;
        int h = 14;

        // Legacy tab
        if (mouseY >= y && mouseY <= y + h &&
            mouseX >= center - 100 && mouseX <= center - 20) {
            sharedwaypoints$sharedTabActive = false;
            cir.setReturnValue(true);
            return;
        }

        // Shared tab
        if (mouseY >= y && mouseY <= y + h &&
            mouseX >= center + 20 && mouseX <= center + 100) {
            sharedwaypoints$sharedTabActive = true;
            cir.setReturnValue(true);
        }
    }

    // === SHARED LIST RENDER OVERRIDE ===
    @Inject(method = "renderBackground", at = @At("HEAD"), cancellable = true)
    private void sharedwaypoints$renderSharedList(GuiGraphics g, int mouseX, int mouseY, float partial,
                                                  CallbackInfo ci) {
        if (!sharedwaypoints$sharedTabActive) return;

        GuiWaypoints self = (GuiWaypoints) (Object) this;
        SharedWaypointsClientRenderer.renderList(g, self.height, self.width, mouseX, mouseY);
        ci.cancel();
    }

    // === SHARED LIST CLICK HANDLING ===
    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    private void sharedwaypoints$clickSharedList(double mouseX, double mouseY, int button,
                                                 CallbackInfoReturnable<Boolean> cir) {
        if (!sharedwaypoints$sharedTabActive) return;

        GuiWaypoints self = (GuiWaypoints) (Object) this;

        int index = SharedWaypointsClientRenderer.getIndexAt(mouseX, mouseY, self.height);
        if (index == -1) return;

        // Left click: select
        if (button == 0) {
            SharedWaypointsClientStorage.setSelectedIndex(index);
            cir.setReturnValue(true);
            return;
        }

        // Right click: open Xaero's edit GUI
        if (button == 1) {
            openXaeroEditGui(self, index);
            cir.setReturnValue(true);
        }
    }

    @Unique
    private void openXaeroEditGui(GuiWaypoints self, int index) {
        SharedWaypointsEntry e = SharedWaypointsClientStorage.getEntries().get(index);

        Waypoint wp = new Waypoint(
            e.x(), e.y(), e.z(),
            e.name(),
            "",
            WaypointColor.WHITE
        );

        ArrayList<Waypoint> list = new ArrayList<>();
        list.add(wp);

        Minecraft.getInstance().setScreen(new GuiAddWaypoint(
            modMain,
            session,
            self,
            self.escape,
            list,
            displayedWorld.getContainer().getRoot().getPath(),
            displayedWorld,
            displayedWorld.getCurrentWaypointSetId(),
            false
        ));
    }
}
