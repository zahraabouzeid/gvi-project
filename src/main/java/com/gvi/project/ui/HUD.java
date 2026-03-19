package com.gvi.project.ui;

import com.gvi.project.GamePanel;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import static com.gvi.project.ui.UITheme.*;
import static com.gvi.project.ui.UIUtils.*;

public class HUD {

    private final GamePanel gp;
    private final Image keyImage;

    private String floatingText = null;
    private boolean floatingPositive = true;
    private int floatingCounter = 0;
    private static final int FLOATING_DURATION = 60; // 1 second

    public HUD(GamePanel gp, Image keyImage) {
        this.gp = gp;
        this.keyImage = keyImage;
    }

    public void showFloatingScore(int points) {
        floatingText = (points > 0 ? "+" : "") + points;
        floatingPositive = points > 0;
        floatingCounter = FLOATING_DURATION;
    }

    public void reset() {
        floatingText = null;
        floatingCounter = 0;
    }

    public void draw(GraphicsContext gc, String formattedTime) {
        double hudX = gp.generalSettings.tileSize / 2.0;
        double hudY = gp.generalSettings.tileSize / 2.0;

        // Display player name
        gc.setFont(FONT_MD);
        gc.setFill(TEXT_GOLD);
        gc.fillText(gp.player.playerName, hudX, 20);

        drawHearts(gc);

        int keySize = 36;
        double keyY = hudY + 55;
        gc.drawImage(keyImage, hudX, keyY, keySize, keySize);
        gc.setFont(FONT_MD);
        gc.setFill(TEXT_WHITE);
        gc.fillText("x" + gp.player.playerKeys, hudX + keySize + 6, keyY + 26);

        // Score in top center with pixel-art rounded box
        String scoreStr = "Score: " + gp.player.score;
        gc.setFont(FONT_LG);
        double scoreTextWidth = getTextWidth(scoreStr, FONT_LG);
        double bgPadding = 16;
        double bgWidth = scoreTextWidth + bgPadding * 2;
        double bgHeight = 36;
        double boxX = gp.generalSettings.screenWidth / 2.0 - bgWidth / 2.0;
        double boxY = 18;
        
        // Draw pixel-art box with rounded corners
        drawPixelBox(gc, boxX, boxY, bgWidth, bgHeight);
        
        // Draw score text (centered in box)
        double scoreX = gp.generalSettings.screenWidth / 2.0 - scoreTextWidth / 2.0;
        double scoreY = boxY + bgHeight / 2.0 + 6;
        gc.setFill(TEXT_WHITE);
        gc.fillText(scoreStr, scoreX, scoreY);

        if (floatingCounter > 0 && floatingText != null) {
            double alpha = floatingCounter / (double) FLOATING_DURATION;
            double floatOffset = (FLOATING_DURATION - floatingCounter) * 0.8;
            gc.setFont(FONT_MD);
            gc.setFill(floatingPositive
                    ? Color.rgb(80, 240, 80, alpha)
                    : Color.rgb(240, 80, 80, alpha));
            gc.fillText(floatingText, boxX + bgWidth + 10, scoreY - floatOffset);
            floatingCounter--;
        }

        gc.setFont(FONT_LG);
        gc.setFill(TEXT_WHITE);
        double timeW = getTextWidth(formattedTime, FONT_LG);
        gc.fillText(formattedTime, gp.generalSettings.screenWidth - timeW - 14, gp.generalSettings.screenHeight - 14);

        if (gp.player.speed > 4) {
            drawSpeedBoost(gc);
        }
    }

    public void drawInteractHint(GraphicsContext gc) {
        int idx = gp.player.nearbyObjectIndex;
        if (idx < 0 || idx >= gp.obj.size() || gp.obj.get(idx) == null) return;

        String hint = gp.obj.get(idx).interactHint;
        gc.setFont(FONT_XS);

        double textW = getTextWidth(hint, FONT_XS);
        double px = gp.generalSettings.screenWidth / 2.0 - textW / 2.0;
        double py = gp.player.screenY - 20;

        // Shadow
        gc.setFill(Color.rgb(0, 0, 0, 0.6));
        gc.fillText(hint, px + 2, py + 2);
        // Text
        gc.setFill(TEXT_GOLD);
        gc.fillText(hint, px, py);
    }

    private void drawHearts(GraphicsContext gc) {
        int hearts = (int) Math.ceil(gp.player.maxHealthHalf / 2.0);
        int startX = (int) (gp.generalSettings.tileSize / 2.0);
        int startY = 28;
        int spacing = 38;

        for (int i = 0; i < hearts; i++) {
            int leftIndex = i * 2;
            boolean leftFull = gp.player.healthHalf > leftIndex;
            boolean rightFull = gp.player.healthHalf > leftIndex + 1;
            drawPixelHeart(gc, startX + i * spacing, startY, leftFull, rightFull);
        }
    }

    private void drawPixelHeart(GraphicsContext gc, int x, int y, boolean leftFull, boolean rightFull) {
        int scale = 4;
        int[][] mask = {
            {0, 1, 1, 0, 0, 1, 1, 0},
            {1, 1, 1, 1, 1, 1, 1, 1},
            {1, 1, 1, 1, 1, 1, 1, 1},
            {1, 1, 1, 1, 1, 1, 1, 1},
            {0, 1, 1, 1, 1, 1, 1, 0},
            {0, 0, 1, 1, 1, 1, 0, 0},
            {0, 0, 0, 1, 1, 0, 0, 0}
        };

        Color empty = Color.rgb(60, 20, 20);
        Color full = Color.rgb(220, 40, 40);
        Color border = Color.rgb(0, 0, 0);

        for (int row = 0; row < mask.length; row++) {
            for (int col = 0; col < mask[row].length; col++) {
                if (mask[row][col] == 0) continue;

                boolean isEdge = false;
                if (row == 0 || col == 0 || row == mask.length - 1 || col == mask[0].length - 1) {
                    isEdge = true;
                } else if (mask[row - 1][col] == 0 || mask[row + 1][col] == 0 ||
                           mask[row][col - 1] == 0 || mask[row][col + 1] == 0) {
                    isEdge = true;
                }

                if (isEdge) {
                    gc.setFill(border);
                } else {
                    boolean isLeft = col < 4;
                    gc.setFill((isLeft ? leftFull : rightFull) ? full : empty);
                }
                gc.fillRect(x + col * scale, y + row * scale, scale, scale);
            }
        }
    }

    private void drawSpeedBoost(GraphicsContext gc) {
        String text = "SPEED BOOST";
        gc.setFont(FONT_XS);
        double tw = getTextWidth(text, FONT_XS);
        double x = gp.generalSettings.screenWidth / 2.0 - tw / 2.0;
        double y = 14;

        drawPixelBox(gc, x - 10, y - 12, tw + 20, 22);
        gc.setFill(TEXT_GOLD);
        gc.fillText(text, x, y + 2);
    }
}
