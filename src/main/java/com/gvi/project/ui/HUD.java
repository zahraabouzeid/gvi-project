package com.gvi.project.ui;

import com.gvi.project.GamePanel;
import com.gvi.project.GeneralSettings;
import com.gvi.project.models.objects.KeyType;
import com.gvi.project.models.objects.OBJ_Key;
import com.gvi.project.models.objects.SuperObject;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.*;

import static com.gvi.project.ui.UITheme.*;
import static com.gvi.project.ui.UIUtils.*;

public class HUD {

    private final GamePanel gp;

    private String floatingText = null;
    private boolean floatingPositive = true;
    private int floatingCounter = 0;
    private static final int FLOATING_DURATION = 60; // 1 second
    private final double hudX;
    private final double hudY;

    public HUD(GamePanel gp) {
        this.gp = gp;
        hudX = GeneralSettings.getTileSize() / 2.0;
        hudY = GeneralSettings.getTileSize() / 2.0;
        initHudImagesLoading();
    }

    public void initHudImagesLoading() {
        List<SuperObject> objects = List.of(
                new OBJ_Key(KeyType.COPPER),
                new OBJ_Key(KeyType.IRON),
                new OBJ_Key(KeyType.GOLD)
        );

        for (SuperObject object : objects) {
            object.canInteract = false;
            object.collision = false;

            gp.hudObj.add(object);
        }
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


        // Display player name
        gc.setFont(FONT_MD);
        gc.setFill(TEXT_GOLD);
        gc.fillText(gp.player.playerName, hudX, 20);

        drawHearts(gc);

        drawKeys(gc, hudX, hudY);

        // Score in top center with pixel-art rounded box
        String scoreStr = "Score: " + gp.player.score;
        gc.setFont(FONT_LG);
        double scoreTextWidth = getTextWidth(scoreStr, FONT_LG);
        double bgPadding = 16;
        double bgWidth = scoreTextWidth + bgPadding * 2;
        double bgHeight = 36;
        double boxX = GeneralSettings.getScreenWidth() / 2.0 - bgWidth / 2.0;
        double boxY = 18;
        
        // Draw pixel-art box with rounded corners
        drawPixelBox(gc, boxX, boxY, bgWidth, bgHeight);
        
        // Draw score text (centered in box)
        double scoreX = GeneralSettings.getScreenWidth() / 2.0 - scoreTextWidth / 2.0;
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
        gc.fillText(formattedTime, GeneralSettings.getScreenWidth() - timeW - 14, GeneralSettings.getScreenHeight() - 14);

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
        double px = GeneralSettings.getScreenWidth() / 2.0 - textW / 2.0;
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
        int startX = (int) (GeneralSettings.getTileSize() / 2.0);
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
        double x = GeneralSettings.getScreenWidth() / 2.0 - tw / 2.0;
        double y = 14;

        drawPixelBox(gc, x - 10, y - 12, tw + 20, 22);
        gc.setFill(TEXT_GOLD);
        gc.fillText(text, x, y + 2);
    }

    private void drawKeys(GraphicsContext gc, double hudX, double hudY) {
        String text = "x %s";
        double fontHeight = getTextHeight(text, FONT_MD);
        double imageSize = 40;
        double gap = 30;
        double index = 0;
        double imageY = hudY + 36;
        double textY = imageY + fontHeight;

        for (SuperObject keyObj : gp.hudObj) {
            if (keyObj == null) continue;

            if(keyObj.id.contains("key_")){
                double imageX = hudX + (imageSize * index ) + (gap * index );

                gc.drawImage(keyObj.sprite.image, imageX, imageY, imageSize, imageSize);
                gc.setFont(FONT_MD);
                gc.setFill(TEXT_WHITE);
                gc.fillText(text.formatted(gp.player.playerItems.getOrDefault(keyObj.id, 0)), imageX + imageSize, textY);

                index++;
            }
        }
    }
}
