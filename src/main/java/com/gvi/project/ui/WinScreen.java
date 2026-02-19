package com.gvi.project.ui;

import javafx.scene.canvas.GraphicsContext;

import static com.gvi.project.ui.UITheme.*;
import static com.gvi.project.ui.UIUtils.*;

public class WinScreen {

    private final int screenWidth;
    private final int screenHeight;

    public WinScreen(int screenWidth, int screenHeight) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
    }

    public void draw(GraphicsContext gc, String formattedTime) {
        gc.setFill(OVERLAY_MEDIUM);
        gc.fillRect(0, 0, screenWidth, screenHeight);

        double centerBoxW = 500;
        double centerBoxH = 180;
        double cbx = screenWidth / 2.0 - centerBoxW / 2.0;
        double cby = screenHeight / 2.0 - centerBoxH / 2.0;
        drawPixelBox(gc, cbx, cby, centerBoxW, centerBoxH);

        gc.setFont(FONT_MD);
        gc.setFill(TEXT_WHITE);
        String text = "You found the Treasure!";
        double textW = getTextWidth(text, FONT_MD);
        gc.fillText(text, screenWidth / 2.0 - textW / 2.0, cby + 50);

        gc.setFont(FONT_LG);
        gc.setFill(TEXT_GOLD);
        text = "Congratulations!";
        textW = getTextWidth(text, FONT_LG);
        gc.fillText(text, screenWidth / 2.0 - textW / 2.0, cby + 100);

        // Time
        gc.setFont(FONT_SM);
        gc.setFill(TEXT_GRAY);
        text = "Time: " + formattedTime;
        textW = getTextWidth(text, FONT_SM);
        gc.fillText(text, screenWidth / 2.0 - textW / 2.0, cby + 140);
    }
}
