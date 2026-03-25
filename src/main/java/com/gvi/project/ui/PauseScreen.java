package com.gvi.project.ui;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import static com.gvi.project.ui.UITheme.*;
import static com.gvi.project.ui.UIUtils.*;

public class PauseScreen extends GameScreen {

    private static final String[] OPTIONS = {"Resume", "Save", "Load", "Exit"};
    private static final int OPTION_COUNT = OPTIONS.length;


    private int selectedOption = 0;

    public PauseScreen() {}

    public void reset() {
        selectedOption = 0;
    }

    public void navigateUp() {
        selectedOption = (selectedOption - 1 + OPTION_COUNT) % OPTION_COUNT;
    }

    public void navigateDown() {
        selectedOption = (selectedOption + 1) % OPTION_COUNT;
    }

    public int getSelectedOption() {
        return selectedOption;
    }

    public void draw(GraphicsContext gc) {
        gc.setFill(OVERLAY_DARK);
        gc.fillRect(0, 0, screenWidth, screenHeight);

        double boxW = 360;
        double boxH = 260;
        double boxX = screenWidth / 2.0 - boxW / 2.0;
        double boxY = screenHeight / 2.0 - boxH / 2.0;

        drawPixelBox(gc, boxX, boxY, boxW, boxH);

        String title = "PAUSED";
        gc.setFont(FONT_LG);
        double titleW = getTextWidth(title, FONT_LG);
        double titleX = screenWidth / 2.0 - titleW / 2.0;
        double titleY = boxY + 52;

        gc.setFill(Color.rgb(0, 0, 0, 0.5));
        gc.fillText(title, titleX + 2, titleY + 2);
        gc.setFill(TEXT_GOLD);
        gc.fillText(title, titleX, titleY);

        double optionStartY = boxY + 106;
        double optionSpacing = 34;

        for (int i = 0; i < OPTION_COUNT; i++) {
            double oy = optionStartY + i * optionSpacing;
            boolean isSelected = i == selectedOption;

            if (isSelected) {
                gc.setFont(FONT_MD);
                gc.setFill(TEXT_GOLD);
                gc.fillText(">", boxX + 38, oy);
            }

            gc.setFont(FONT_MD);
            gc.setFill(isSelected ? TEXT_GOLD : TEXT_WHITE);

            double optW = getTextWidth(OPTIONS[i], FONT_MD);
            gc.fillText(OPTIONS[i], screenWidth / 2.0 - optW / 2.0, oy);
        }

        gc.setFont(FONT_XS);
        gc.setFill(TEXT_GRAY);
        String hint = "WASD / Arrows - Navigate    ENTER - Select";
        double hw = getTextWidth(hint, FONT_XS);
        gc.fillText(hint, screenWidth / 2.0 - hw / 2.0, boxY + boxH - 18);
    }
}
