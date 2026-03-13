package com.gvi.project.ui;

import com.gvi.project.helper.SaveManager;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import static com.gvi.project.ui.UITheme.*;
import static com.gvi.project.ui.UIUtils.*;

public class SaveSlotScreen {

    private static final int SLOT_COUNT = 3;

    private final int screenWidth;
    private final int screenHeight;
    private int    selectedSlot = 0;
    private String mode         = "save";

    public SaveSlotScreen(int screenWidth, int screenHeight) {
        this.screenWidth  = screenWidth;
        this.screenHeight = screenHeight;
    }

    public void open(String mode) {
        this.mode         = mode;
        this.selectedSlot = 0;
    }

    public void navigateUp() {
        selectedSlot = (selectedSlot - 1 + SLOT_COUNT) % SLOT_COUNT;
    }

    public void navigateDown() {
        selectedSlot = (selectedSlot + 1) % SLOT_COUNT;
    }

    public int getSelectedSlot() {
        return selectedSlot + 1;
    }

    public void draw(GraphicsContext gc, SaveManager.SlotInfo[] slots) {
        gc.setFill(OVERLAY_DARK);
        gc.fillRect(0, 0, screenWidth, screenHeight);

        double boxW = 460;
        double boxH = 330;
        double boxX = screenWidth  / 2.0 - boxW / 2.0;
        double boxY = screenHeight / 2.0 - boxH / 2.0;

        drawPixelBox(gc, boxX, boxY, boxW, boxH);

        String title = mode.equals("save") ? "SAVE GAME" : "LOAD GAME";
        gc.setFont(FONT_LG);
        double tw = getTextWidth(title, FONT_LG);
        gc.setFill(Color.rgb(0, 0, 0, 0.5));
        gc.fillText(title, screenWidth / 2.0 - tw / 2.0 + 2, boxY + 52 + 2);
        gc.setFill(TEXT_GOLD);
        gc.fillText(title, screenWidth / 2.0 - tw / 2.0, boxY + 52);

        double slotStartY = boxY + 96;
        double slotSpacing = 68;
        double slotBoxX   = boxX + 20;
        double slotBoxW   = boxW - 40;
        double slotH      = 60;

        for (int i = 0; i < SLOT_COUNT; i++) {
            double sy       = slotStartY + i * slotSpacing;
            boolean sel     = i == selectedSlot;
            SaveManager.SlotInfo info = slots != null && i < slots.length ? slots[i] : null;

            gc.setFill(sel ? Color.rgb(55, 82, 60) : Color.rgb(28, 44, 32));
            gc.fillRect(slotBoxX, sy, slotBoxW, slotH);

            if (sel) {
                gc.setStroke(TEXT_GOLD);
                gc.setLineWidth(2);
                gc.strokeRect(slotBoxX + 1, sy + 1, slotBoxW - 2, slotH - 2);
            }

            gc.setFont(FONT_SM);
            gc.setFill(sel ? TEXT_GOLD : TEXT_GRAY);
            gc.fillText("Slot " + (i + 1), slotBoxX + 12, sy + 18);

            if (info != null && info.exists()) {
                gc.setFont(FONT_XS);
                gc.setFill(sel ? TEXT_WHITE : TEXT_GRAY);
                gc.fillText(info.playerName() + "  |  Score: " + info.score() + "  |  " + info.mapName(),
                        slotBoxX + 12, sy + 36);
                gc.fillText(info.savedAt(), slotBoxX + 12, sy + 52);
            } else {
                gc.setFont(FONT_XS);
                gc.setFill(Color.rgb(90, 110, 90));
                gc.fillText("Empty", slotBoxX + 12, sy + 40);
            }
        }

        gc.setFont(FONT_XS);
        gc.setFill(TEXT_GRAY);
        String hint = "WASD / Arrows - Navigate    ENTER - Confirm    ESC - Cancel";
        double hw = getTextWidth(hint, FONT_XS);
        gc.fillText(hint, screenWidth / 2.0 - hw / 2.0, boxY + boxH - 18);
    }
}
