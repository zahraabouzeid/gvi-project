package com.gvi.project.ui;

import com.gvi.project.GamePanel;
import com.gvi.project.helper.ColorHelper;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import static com.gvi.project.ui.UITheme.*;

public class Minimap {

    private final GamePanel gp;
    private static final int MINIMAP_SIZE = 120;

    public Minimap(GamePanel gp) {
        this.gp = gp;
    }

    public void draw(GraphicsContext gc) {
        int mapMax = Math.max(gp.currentMap.width, gp.currentMap.height);
        int pixelSize = Math.max(1, MINIMAP_SIZE / mapMax);

        int minimapW = gp.currentMap.width * pixelSize;
        int minimapH = gp.currentMap.height * pixelSize;
        int margin = 10;
        int startX = gp.generalSettings.screenWidth - MINIMAP_SIZE - margin + (MINIMAP_SIZE - minimapW) / 2;
        int startY = margin + (MINIMAP_SIZE - minimapH) / 2;

        double centerX = gp.generalSettings.screenWidth - MINIMAP_SIZE / 2.0 - margin;
        double centerY = margin + MINIMAP_SIZE / 2.0;
        double radius = MINIMAP_SIZE / 2.0;

        // Background circle
        drawCircleBackground(gc, centerX, centerY, radius);

        // Gold pixel border ring
        drawCircleBorder(gc, centerX, centerY, radius);

        // Tiles (clipped to circle)
        drawTiles(gc, startX, startY, centerX, centerY, radius, pixelSize);

        // Objects (clipped to circle)
        drawObjects(gc, startX, startY, centerX, centerY, radius, pixelSize);

        // Player marker (clipped to circle)
        drawPlayer(gc, startX, startY, centerX, centerY, radius, pixelSize);
    }

    private void drawCircleBackground(GraphicsContext gc, double cx, double cy, double radius) {
        gc.setFill(MINIMAP_BG);
        for (int px = (int) (cx - radius - 3); px <= (int) (cx + radius + 3); px++) {
            for (int py = (int) (cy - radius - 3); py <= (int) (cy + radius + 3); py++) {
                double dist = Math.sqrt((px - cx) * (px - cx) + (py - cy) * (py - cy));
                if (dist <= radius + 2) {
                    gc.fillRect(px, py, 1, 1);
                }
            }
        }
    }

    private void drawCircleBorder(GraphicsContext gc, double cx, double cy, double radius) {
        gc.setFill(MINIMAP_BORDER);
        for (int angle = 0; angle < 360; angle++) {
            double rad = Math.toRadians(angle);
            for (double r = radius; r <= radius + 2; r += 1) {
                int bx = (int) (cx + r * Math.cos(rad));
                int by = (int) (cy + r * Math.sin(rad));
                gc.fillRect(bx, by, 2, 2);
            }
        }
    }

    private void drawTiles(GraphicsContext gc, int startX, int startY,
                           double cx, double cy, double radius, int pixelSize) {
        for (int col = 0; col < gp.currentMap.width; col++) {
            for (int row = 0; row < gp.currentMap.height; row++) {
                double tileX = startX + col * pixelSize + pixelSize / 2.0;
                double tileY = startY + row * pixelSize + pixelSize / 2.0;
                if (distance(tileX, tileY, cx, cy) > radius) continue;

                String tileKey = gp.currentMap.getLayer("FLOOR").layout[col][row];
                gc.setFill(getTileColor(tileKey));
                gc.fillRect(startX + col * pixelSize, startY + row * pixelSize, pixelSize, pixelSize);
            }
        }
    }

    private void drawObjects(GraphicsContext gc, int startX, int startY,
                             double cx, double cy, double radius, int pixelSize) {
        gc.setFill(Color.YELLOW);
        for (int i = 0; i < gp.obj.size(); i++) {
            if (gp.obj.get(i) != null) {
                int objCol = gp.obj.get(i).worldX / gp.generalSettings.tileSize;
                int objRow = gp.obj.get(i).worldY / gp.generalSettings.tileSize;
                double ox = startX + objCol * pixelSize + pixelSize / 2.0;
                double oy = startY + objRow * pixelSize + pixelSize / 2.0;
                if (distance(ox, oy, cx, cy) <= radius) {
                    gc.fillRect(startX + objCol * pixelSize, startY + objRow * pixelSize, pixelSize, pixelSize);
                }
            }
        }
    }

    private void drawPlayer(GraphicsContext gc, int startX, int startY,
                            double cx, double cy, double radius, int pixelSize) {
        int playerCol = gp.player.worldX / gp.generalSettings.tileSize;
        int playerRow = gp.player.worldY / gp.generalSettings.tileSize;
        gc.setFill(Color.RED);
        gc.fillRect(startX + playerCol * pixelSize - 1, startY + playerRow * pixelSize - 1,
                    pixelSize + 2, pixelSize + 2);
    }

    private double distance(double x1, double y1, double x2, double y2) {
        return Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
    }

    private Color getTileColor(String tileKey) {
        if (tileKey.equals("empty")) {
            return Color.TRANSPARENT;
        }

        Image image = gp.spriteManager.getStoredSprite(tileKey).image;
        return ColorHelper.getMostCommonColor(image);
    }
}
