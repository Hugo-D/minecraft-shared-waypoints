package com.ibuildstuff.hugo_d.minecraftsharedwaypoints;

import com.ibuildstuff.hugo_d.minecraftsharedwaypoints.data.SharedWaypointsEntry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;

import java.util.List;

public final class SharedWaypointsClientRenderer {

    private static final int TOP = 58;
    private static final int BOTTOM_OFFSET = 61;
    private static final int ENTRY_HEIGHT = 18;

    public static void renderTabs(GuiGraphics g, int width, Font font, boolean sharedTab) {
        int center = width / 2;
        int y = 50;

        drawTab(g, center - 100, y, 80, 14, "Local", !sharedTab, font);
        drawTab(g, center + 20, y, 80, 14, "Shared", sharedTab, font);
    }

    private static void drawTab(GuiGraphics g, int x, int y, int w, int h, String label, boolean active, Font font) {
        int bg = active ? 0xFF444444 : 0xFF222222;
        int border = 0xFF000000;
        int text = active ? 0xFFFFFF00 : 0xFFFFFFFF;

        g.fill(x, y, x + w, y + h, bg);
        g.fill(x, y, x + w, y + 1, border);
        g.fill(x, y + h - 1, x + w, y + h, border);
        g.fill(x, y, x + 1, y + h, border);
        g.fill(x + w - 1, y, x + w, y + h, border);

        g.drawCenteredString(font, label, x + w / 2, y + 3, text);
    }

    public static void renderList(GuiGraphics g, int height, int width, int mouseX, int mouseY) {
        List<SharedWaypointsEntry> entries = SharedWaypointsClientStorage.getEntries();
        int bottom = height - BOTTOM_OFFSET;

        // Background
        g.fill(0, TOP, width, bottom, 0xAA000000);

        int y = TOP;

        for (int i = 0; i < entries.size(); i++) {
            SharedWaypointsEntry e = entries.get(i);

            boolean hovered = mouseY >= y && mouseY < y + ENTRY_HEIGHT;
            boolean selected = (i == SharedWaypointsClientStorage.getSelectedIndex());

            int bg = selected ? 0x8833AAFF :
                hovered ? 0x55FFFFFF :
                    0x22000000;

            g.fill(0, y, width, y + ENTRY_HEIGHT, bg);

            drawEntry(g, e, 10, y + 4);

            y += ENTRY_HEIGHT;
            if (y > bottom) break;
        }
    }

    private static void drawEntry(GuiGraphics g, SharedWaypointsEntry e, int x, int y) {
        var font = Minecraft.getInstance().font;

        g.drawString(font, e.name(), x, y, 0xFFFFFF);
        g.drawString(font, "(" + e.x() + ", " + e.y() + ", " + e.z() + ")", x + 120, y, 0xAAAAAA);
        g.drawString(font, e.dimension(), x + 240, y, 0x8888FF);
    }

    public static int getIndexAt(double mouseX, double mouseY, int height) {
        int bottom = height - BOTTOM_OFFSET;

        if (mouseY < TOP || mouseY >= bottom) return -1;

        return (int) ((mouseY - TOP) / ENTRY_HEIGHT);
    }
}
