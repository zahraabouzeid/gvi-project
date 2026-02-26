package com.gvi.project.ui;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import static com.gvi.project.ui.UITheme.*;
import static com.gvi.project.ui.UIUtils.*;

/**
 * Screen for entering the player's character name at the start of the game.
 */
public class CharacterNameScreen {

    private final int screenWidth;
    private final int screenHeight;
    private int blinkCounter = 0;
    private final int maxNameLength = 16;

    public CharacterNameScreen(int screenWidth, int screenHeight) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
    }

    public void draw(GraphicsContext gc, String currentName) {
        blinkCounter++;

        // Background
        gc.setFill(TITLE_BG);
        gc.fillRect(0, 0, screenWidth, screenHeight);

        // pixel border
        drawBorder(gc);

        // Title
        String title = "CREATE YOUR CHARACTER";
        gc.setFont(FONT_XL);
        double tw = getTextWidth(title, FONT_XL);
        double tx = screenWidth / 2.0 - tw / 2.0;
        double ty = screenHeight / 2.0 - 100;

        // Shadow
        gc.setFill(Color.rgb(0, 0, 0, 0.5));
        gc.fillText(title, tx + 3, ty + 3);
        gc.setFill(TEXT_GOLD);
        gc.fillText(title, tx, ty);

        // Prompt
        String prompt = "Enter your character name:";
        gc.setFont(FONT_MD);
        gc.setFill(TEXT_WHITE);
        double pw = getTextWidth(prompt, FONT_MD);
        gc.fillText(prompt, screenWidth / 2.0 - pw / 2.0, ty + 50);

        // Input box
        int boxWidth = 300;
        int boxHeight = 50;
        int boxX = (screenWidth - boxWidth) / 2;
        int boxY = (int) (ty + 100);

        // Box border
        gc.setStroke(BOX_BORDER_OUTER);
        gc.setLineWidth(2);
        gc.strokeRect(boxX, boxY, boxWidth, boxHeight);

        // Box background
        gc.setFill(Color.rgb(30, 30, 30));
        gc.fillRect(boxX + 2, boxY + 2, boxWidth - 4, boxHeight - 4);

        // Text in box
        gc.setFont(FONT_LG);
        gc.setFill(TEXT_WHITE);
        String displayName = currentName.isEmpty() ? " " : currentName;
        gc.fillText(displayName, boxX + 15, boxY + 35);

        // Cursor (blinking)
        if ((blinkCounter / 30) % 2 == 0) {
            double cursorX = boxX + 15 + getTextWidth(currentName, FONT_LG);
            gc.setStroke(TEXT_GOLD);
            gc.setLineWidth(2);
            gc.strokeLine(cursorX, boxY + 10, cursorX, boxY + 40);
        }

        // Instructions
        gc.setFont(FONT_XS);
        gc.setFill(TEXT_GRAY);
        String info = "Max " + maxNameLength + " characters";
        double iw = getTextWidth(info, FONT_XS);
        gc.fillText(info, screenWidth / 2.0 - iw / 2.0, boxY + 70);

        // Press ENTER to continue
        gc.setFont(FONT_MD);
        if ((blinkCounter / 40) % 2 == 0) {
            gc.setFill(TEXT_WHITE);
            String enter = "Press ENTER to start";
            double ew = getTextWidth(enter, FONT_MD);
            gc.fillText(enter, screenWidth / 2.0 - ew / 2.0, screenHeight - 80);
        }

        // Additional info
        gc.setFont(FONT_XS);
        gc.setFill(TEXT_GRAY);
        String info2 = "ESC - Go back to title";
        double i2w = getTextWidth(info2, FONT_XS);
        gc.fillText(info2, screenWidth / 2.0 - i2w / 2.0, screenHeight - 50);
    }

    public int getMaxNameLength() {
        return maxNameLength;
    }

    private void drawBorder(GraphicsContext gc) {
        // reuse the common border logic from UIUtils
        UIUtils.drawScreenBorder(gc, screenWidth, screenHeight);
    }
}
