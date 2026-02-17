package com.ibuildstuff.hugo_d.minecraftsharedwaypoints.client.mixin;

import com.ibuildstuff.hugo_d.minecraftsharedwaypoints.client.mapping.WaypointMapper;
import com.ibuildstuff.hugo_d.minecraftsharedwaypoints.client.mapping.WaypointMapperImpl;
import com.ibuildstuff.hugo_d.minecraftsharedwaypoints.client.renderer.LocalWaypointsRenderer;
import com.ibuildstuff.hugo_d.minecraftsharedwaypoints.client.renderer.SharedWaypointsRenderer;
import com.ibuildstuff.hugo_d.minecraftsharedwaypoints.client.storage.LocalWaypointsClientStorage;
import com.ibuildstuff.hugo_d.minecraftsharedwaypoints.client.storage.SharedWaypointsClientStorage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
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
import xaero.lib.client.gui.widget.dropdown.DropDownWidget;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.StreamSupport;

@Mixin(GuiWaypoints.class)
public class GuiWaypointsClientMixin {

    private final WaypointMapper waypointMapper = new WaypointMapperImpl();

    // === Xaero fields we need for opening GuiAddWaypoint ===
    @Shadow
    private MinimapWorld displayedWorld;
    @Shadow
    private DropDownWidget worldsDD;
    @Shadow
    private MinimapSession session;
    @Final
    @Shadow
    private HudMod modMain;

    @Shadow
    private Button teleportButton;
    @Shadow
    private Button shareButton;
    @Shadow
    private Button editButton;
    @Shadow
    private Button deleteButton;
    @Shadow
    private Button disableEnableButton;
    @Shadow
    private Button clearButton;

    // ========================================================================
    //  INIT —
    //  - Feed existing removedWaypoints to local mod
    //  - Disable teleport button
    // ========================================================================
    @Inject(method = "init", at = @At("TAIL"))
    private void sharedwaypoints$init(CallbackInfo ci) {
        // Feed existing removedWaypoints to local mod
        Collection<LocalWaypointEntry> localWaypoints = StreamSupport.stream(this.displayedWorld.getIterableWaypointSets().spliterator(), false)
            .flatMap(waypointSet ->
                StreamSupport.stream(waypointSet.getWaypoints().spliterator(), false)
                    .map(waypoint -> waypointMapper.xaeroWaypointToLocalWaypoint(waypoint, this.displayedWorld.getDimId().location().toString()))
            )
            .toList();
        LocalWaypointsClientStorage.update(localWaypoints);

        // Disable teleport button
        if (this.teleportButton != null) {
            this.teleportButton.active = false;
        }
    }

    // ========================================================================
    //  RENDER — draw both panels entirely ourselves
    // ========================================================================
    @Inject(method = "renderBackground", at = @At("HEAD"), cancellable = true)
    private void sharedwaypoints$renderPanels(GuiGraphics g, int mouseX, int mouseY, float partial, CallbackInfo ci) {
        GuiWaypoints self = (GuiWaypoints) (Object) this;
        Minecraft mc = Minecraft.getInstance();

        mc.gameRenderer.processBlurEffect(0.7f);
        mc.getMainRenderTarget().bindWrite(true);

        int mid = self.width / 2;

        // === Labels ===
        g.drawCenteredString(mc.font, "Local", mid / 2, 54, 0xFFFFFF);
        g.drawCenteredString(mc.font, "Shared", mid + mid / 2, 54, 0xFFFFFF);

        // === Local panel ===
        int leftX = 4;
        int leftWidth = self.width / 2 - 8;

        LocalWaypointsRenderer.renderList(
            g,
            leftX,
            leftWidth,
            self.height,
            mouseX,
            mouseY,
            LocalWaypointsClientStorage.getEntries(this.displayedWorld.getDimId().location().toString())
        );

        // === Shared panel ===
        int rightX = mid + 4;
        int rightWidth = self.width / 2 - 8;

        SharedWaypointsRenderer.renderList(
            g,
            rightX,
            rightWidth,
            self.height,
            mouseX,
            mouseY,
            SharedWaypointsClientStorage.getWaypoints(this.displayedWorld.getDimId())
        );

        // Le bouton "Share" est actif uniquement s'il s'agit d'un waypoint local
        shareButton.active = LocalWaypointsClientStorage.getSelectedIndex() != -1;

        // Gestion de l'activation des autres boutons
        boolean buttonsActive = (LocalWaypointsClientStorage.getSelectedIndex() != -1 || SharedWaypointsClientStorage.getSelectedIndex() != -1);
        editButton.active = buttonsActive;
        deleteButton.active = buttonsActive;
        disableEnableButton.active = buttonsActive;
        clearButton.active = buttonsActive;

        ci.cancel();
    }

    @Inject(method = "onSelected", at = @At("RETURN"))
    private void sharedwaypoints$onSelected(DropDownWidget menu, int selectedIndex, CallbackInfoReturnable<Boolean> cir) {
        if (menu == worldsDD) {
            Collection<LocalWaypointEntry> localWaypoints = StreamSupport.stream(this.displayedWorld.getIterableWaypointSets().spliterator(), false)
                .flatMap(waypointSet ->
                    StreamSupport.stream(waypointSet.getWaypoints().spliterator(), false)
                        .map(waypoint -> waypointMapper.xaeroWaypointToLocalWaypoint(waypoint, this.displayedWorld.getDimId().location().toString()))
                )
                .toList();
            LocalWaypointsClientStorage.update(localWaypoints);
        }
    }

    // ========================================================================
    //  CLICK HANDLING — both panels + edit override
    // ========================================================================
    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    private void sharedwaypoints$mouseClicked(double mouseX, double mouseY, int button,
                                              CallbackInfoReturnable<Boolean> cir) {
        GuiWaypoints self = (GuiWaypoints) (Object) this;

        int mid = self.width / 2;

        int leftX = 4;
        int leftWidth = self.width / 2 - 8;

        int rightX = mid + 4;
        int rightWidth = self.width / 2 - 8;

        // --- LOCAL PANEL CLICK (entries) ---
        int localIndex = LocalWaypointsRenderer.getIndexAt(
            leftX,
            leftWidth,
            self.height,
            mouseX,
            mouseY
        );

        if (localIndex != -1 && (button == 0 || button == 1)) {
            LocalWaypointsClientStorage.setSelectedIndex(localIndex);
            SharedWaypointsClientStorage.setSelectedIndex(-1);
            cir.setReturnValue(true);
            return;
        }

        // --- SHARED PANEL CLICK (entries) ---
        int sharedIndex = SharedWaypointsRenderer.getIndexAt(
            rightX,
            rightWidth,
            self.height,
            mouseX,
            mouseY
        );

        if (sharedIndex != -1 && (button == 0 || button == 1)) {
            SharedWaypointsClientStorage.setSelectedIndex(sharedIndex);
            LocalWaypointsClientStorage.setSelectedIndex(-1);
            cir.setReturnValue(true);
            return;
        }

        // --- LOCAL SCROLLBAR CLICK ---
        if (isInLocalScrollbar(mouseX, mouseY, leftX, leftWidth, self.height)) {
            handleLocalScrollbarClick(mouseY, self.height);
            cir.setReturnValue(true);
            return;
        }

        // --- SHARED SCROLLBAR CLICK ---
        if (isInSharedScrollbar(mouseX, mouseY, rightX, rightWidth, self.height)) {
            handleSharedScrollbarClick(mouseY, self.height);
            cir.setReturnValue(true);
            return;
        }

        // --- EDIT BUTTON WITH SHARED SELECTION (unchanged) ---
        if (button == 0 &&
            SharedWaypointsClientStorage.getSelectedIndex() != -1 &&
            editButton != null &&
            editButton.isMouseOver(mouseX, mouseY)) {

            SharedWaypointEntry e = SharedWaypointsClientStorage
                .getWaypoints(this.displayedWorld.getDimId().location().toString())
                .get(SharedWaypointsClientStorage.getSelectedIndex());

            Waypoint wp = new Waypoint(
                e.x(), e.y(), e.z(),
                e.name(),
                "",
                WaypointColor.WHITE
            );

            var list = new ArrayList<Waypoint>();
            list.add(wp);

            Minecraft.getInstance().setScreen(new GuiAddWaypoint(
                this.modMain,
                this.session,
                self,
                self.escape,
                list,
                this.displayedWorld.getContainer().getRoot().getPath(),
                this.displayedWorld,
                this.displayedWorld.getCurrentWaypointSetId(),
                false
            ));

            cir.setReturnValue(true);
        }

        // --- SHARE BUTTON CLICK: route exclusively through our system ---
        if (button == 0 &&
            shareButton != null &&
            shareButton.active &&
            shareButton.isMouseOver(mouseX, mouseY)) {

            // Share local selection if any
            int localIdx = LocalWaypointsClientStorage.getSelectedIndex();

            if (localIdx != -1) {
                LocalWaypointEntry e = LocalWaypointsClientStorage.getEntries(this.displayedWorld.getDimId().location().toString()).get(localIdx);
                SharedWaypointsClientNetworking.sendShare(this.displayedWorld.getDimId(), e);
                cir.setReturnValue(true);
                return;
            }

            // prevent Xaero's original share behavior
            cir.setReturnValue(true);
        }
    }

    @Unique
    private boolean isInLocalScrollbar(double mouseX, double mouseY, int panelX, int panelWidth, int screenHeight) {
        int barX = panelX + panelWidth;
        int top = 64;
        int bottom = screenHeight - 61;
        return mouseX >= barX && mouseX <= barX + 4 && mouseY >= top && mouseY <= bottom;
    }

    @Unique
    private boolean isInSharedScrollbar(double mouseX, double mouseY, int panelX, int panelWidth, int screenHeight) {
        int barX = panelX + panelWidth;
        int top = 64;
        int bottom = screenHeight - 61;
        return mouseX >= barX && mouseX <= barX + 4 && mouseY >= top && mouseY <= bottom;
    }

    @Unique
    private void handleLocalScrollbarClick(double mouseY, int screenHeight) {
        int top = 64;
        int bottom = screenHeight - 61;
        int visibleHeight = bottom - top;

        // simple page up/down
        if (mouseY < top + (double) visibleHeight / 2) {
            LocalWaypointsClientStorage.addScroll(-visibleHeight);
        } else {
            LocalWaypointsClientStorage.addScroll(visibleHeight);
        }
    }

    @Unique
    private void handleSharedScrollbarClick(double mouseY, int screenHeight) {
        int top = 64;
        int bottom = screenHeight - 61;
        int visibleHeight = bottom - top;

        if (mouseY < top + (double) visibleHeight / 2) {
            SharedWaypointsClientStorage.addScroll(-visibleHeight);
        } else {
            SharedWaypointsClientStorage.addScroll(visibleHeight);
        }
    }

}
