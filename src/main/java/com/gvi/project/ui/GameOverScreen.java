package com.gvi.project.ui;

import javafx.scene.canvas.GraphicsContext;

import static com.gvi.project.ui.UITheme.*;
import static com.gvi.project.ui.UIUtils.*;

public class GameOverScreen extends GameScreen {

    private int blinkCounter = 0;

    public GameOverScreen() {}

    public void reset() {
        blinkCounter = 0;
    }

    public void draw(GraphicsContext gc) {
        blinkCounter++;

        // Dark overlay
        gc.setFill(OVERLAY_DARK);
        gc.fillRect(0, 0, screenWidth, screenHeight);

        // "GAME OVER" text
        String text = "GAME OVER";
        gc.setFont(FONT_XL);
        gc.setFill(WRONG_TEXT);
        double textW = getTextWidth(text, FONT_XL);
        double x = screenWidth / 2.0 - textW / 2.0;
        double y = screenHeight / 2.0 - 20;
        gc.fillText(text, x, y);

        // Blinking restart prompt
        if ((blinkCounter / 30) % 2 == 0) {
            gc.setFont(FONT_SM);
            gc.setFill(TEXT_WHITE);
            String restart = "Press ENTER to restart";
            double rw = getTextWidth(restart, FONT_SM);
            gc.fillText(restart, screenWidth / 2.0 - rw / 2.0, screenHeight / 2.0 + 30);
        }
    }
}
