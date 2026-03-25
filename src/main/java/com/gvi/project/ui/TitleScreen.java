package com.gvi.project.ui;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import static com.gvi.project.ui.UITheme.*;
import static com.gvi.project.ui.UIUtils.*;

/**
 * Renders the title/start screen with game title, subtitle,
 * blinking start prompt, and control hints.
 */
public class TitleScreen extends GameScreen {

    private int blinkCounter = 0;

    public TitleScreen() {
        super();
    }

    public void draw(GraphicsContext gc) {
        blinkCounter++;

        // Background
        gc.setFill(TITLE_BG);
        gc.fillRect(0, 0, screenWidth, screenHeight);

        // pixel border
        drawBorder(gc);

        // Title
        String title = "SQL DUNGEON";
        gc.setFont(FONT_XL);
        double tw = getTextWidth(title, FONT_XL);
        double tx = screenWidth / 2.0 - tw / 2.0;
        double ty = screenHeight / 2.0 - 80;

        // Shadow
        gc.setFill(Color.rgb(0, 0, 0, 0.5));
        gc.fillText(title, tx + 3, ty + 3);
        gc.setFill(TEXT_GOLD);
        gc.fillText(title, tx, ty);

        // Subtitle
        String sub = "Gamification of Learning";
        gc.setFont(FONT_SM);
        gc.setFill(BOX_BORDER_INNER);
        double sw = getTextWidth(sub, FONT_SM);
        gc.fillText(sub, screenWidth / 2.0 - sw / 2.0, ty + 35);

        // "Press ENTER to start"
        if ((blinkCounter / 40) % 2 == 0) {
            String prompt = "Press ENTER to start";
            gc.setFont(FONT_MD);
            gc.setFill(TEXT_WHITE);
            double pw = getTextWidth(prompt, FONT_MD);
            gc.fillText(prompt, screenWidth / 2.0 - pw / 2.0, screenHeight / 2.0 + 60);
        }

        // Controls info
        gc.setFont(FONT_XS);
        gc.setFill(TEXT_GRAY);
        String c1 = "WASD - Move    F - Interact";
        double c1w = getTextWidth(c1, FONT_XS);
        gc.fillText(c1, screenWidth / 2.0 - c1w / 2.0, screenHeight - 60);
        String c2 = "1-4 - Answer    ESC - Close";
        double c2w = getTextWidth(c2, FONT_XS);
        gc.fillText(c2, screenWidth / 2.0 - c2w / 2.0, screenHeight - 38);
    }

    private void drawBorder(GraphicsContext gc) {
        // now delegated to shared utility
        UIUtils.drawScreenBorder(gc, screenWidth, screenHeight);
    }
}
