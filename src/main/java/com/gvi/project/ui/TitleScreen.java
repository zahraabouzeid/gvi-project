package com.gvi.project.ui;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import static com.gvi.project.ui.UITheme.*;
import static com.gvi.project.ui.UIUtils.*;

/**
 * Renders the title/start screen with game title, subtitle,
 * blinking start prompt, and control hints.
 */
public class TitleScreen {

    private final int screenWidth;
    private final int screenHeight;
    private int blinkCounter = 0;

    public TitleScreen(int screenWidth, int screenHeight) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
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
        int p = 3;
        gc.setFill(BOX_BORDER_OUTER);
        for (int i = p * 3; i < screenWidth - p * 3; i += p) {
            gc.fillRect(i, p * 2, p, p);
            gc.fillRect(i, screenHeight - p * 3, p, p);
        }
        for (int i = p * 3; i < screenHeight - p * 3; i += p) {
            gc.fillRect(p * 2, i, p, p);
            gc.fillRect(screenWidth - p * 3, i, p, p);
        }

        gc.setFill(BOX_CORNER);
        gc.fillRect(p * 2, p * 2, p * 2, p);
        gc.fillRect(p * 2, p * 2, p, p * 2);
        gc.fillRect(screenWidth - p * 4, p * 2, p * 2, p);
        gc.fillRect(screenWidth - p * 3, p * 2, p, p * 2);
        gc.fillRect(p * 2, screenHeight - p * 3, p * 2, p);
        gc.fillRect(p * 2, screenHeight - p * 4, p, p * 2);
        gc.fillRect(screenWidth - p * 4, screenHeight - p * 3, p * 2, p);
        gc.fillRect(screenWidth - p * 3, screenHeight - p * 4, p, p * 2);
    }
}
