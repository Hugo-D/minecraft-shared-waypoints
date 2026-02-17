package com.ibuildstuff.hugo_d.minecraftsharedwaypoints.client.renderer;

import com.ibuildstuff.hugo_d.minecraftsharedwaypoints.client.storage.LocalWaypointsClientStorage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

import java.util.List;

public final class LocalWaypointsRenderer {

    private static final int TOP = 64;
    private static final int BOTTOM_OFFSET = 61;
    private static final int ENTRY_HEIGHT = 18;

    public static void renderList(GuiGraphics g,
                                  int panelX,
                                  int panelWidth,
                                  int screenHeight,
                                  int mouseX,
                                  int mouseY,
                                  List<LocalWaypointEntry> entries) {
        int bottom = screenHeight - BOTTOM_OFFSET;

        int scroll = LocalWaypointsClientStorage.getScrollOffset();

        // Panel background
        g.fill(panelX, TOP, panelX + panelWidth, bottom, 0xCC000000);

        int y = TOP - scroll;

        for (int i = 0; i < entries.size(); i++) {
            LocalWaypointEntry e = entries.get(i);

            if (y + ENTRY_HEIGHT < TOP) {
                y += ENTRY_HEIGHT;
                continue;
            }
            if (y > bottom) break;

            boolean hovered = mouseX >= panelX && mouseX < panelX + panelWidth &&
                mouseY >= y && mouseY < y + ENTRY_HEIGHT;

            boolean selected = (i == LocalWaypointsClientStorage.getSelectedIndex());

            int bgColor = selected ? 0xFF000000 : (hovered ? 0xAA222222 : 0x66000000);

            // Background
            g.fill(panelX + 1, y + 1, panelX + panelWidth - 1, y + ENTRY_HEIGHT - 1, bgColor);

            // Selection border
            if (selected) {
                int border = 0xFFAAAAAA;
                g.fill(panelX, y, panelX + panelWidth, y + 1, border);
                g.fill(panelX, y + ENTRY_HEIGHT - 1, panelX + panelWidth, y + ENTRY_HEIGHT, border);
                g.fill(panelX, y, panelX + 1, y + ENTRY_HEIGHT, border);
                g.fill(panelX + panelWidth - 1, y, panelX + panelWidth, y + ENTRY_HEIGHT, border);
            }

            drawEntry(g, e, panelX + 6, y + 4);

            y += ENTRY_HEIGHT;
        }

        drawScrollbar(g, panelX, panelWidth, screenHeight, entries.size());
    }

    private static void drawEntry(GuiGraphics g, LocalWaypointEntry e, int x, int y) {
        var font = Minecraft.getInstance().font;

        g.drawString(font, e.name(), x, y, 0xFFFFFF);
        g.drawString(font, "(" + e.x() + ", " + e.y() + ", " + e.z() + ")", x + 120, y, 0xAAAAAA);
    }

    private static void drawScrollbar(GuiGraphics g, int panelX, int panelWidth, int screenHeight, int totalEntries) {
        int bottom = screenHeight - BOTTOM_OFFSET;

        int contentHeight = totalEntries * ENTRY_HEIGHT;
        int visibleHeight = bottom - TOP;

        if (contentHeight <= visibleHeight) return;

        float ratio = (float) visibleHeight / contentHeight;
        int barHeight = (int) (visibleHeight * ratio);

        int scroll = LocalWaypointsClientStorage.getScrollOffset();
        float scrollRatio = (float) scroll / (contentHeight - visibleHeight);
        int barY = TOP + (int) (scrollRatio * (visibleHeight - barHeight));

        int barX = panelX + panelWidth;

        g.fill(barX, barY, barX + 4, barY + barHeight, 0xFF888888);
    }

    public static int getIndexAt(int panelX, int panelWidth, int screenHeight,
                                 double mouseX, double mouseY) {

        int bottom = screenHeight - BOTTOM_OFFSET;

        if (mouseX < panelX || mouseX >= panelX + panelWidth) return -1;
        if (mouseY < TOP || mouseY >= bottom) return -1;

        int scroll = LocalWaypointsClientStorage.getScrollOffset();
        int index = (int) ((mouseY - TOP + scroll) / ENTRY_HEIGHT);

        return index;
    }
}
