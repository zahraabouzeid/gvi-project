package com.gvi.project.ui;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.List;

import static com.gvi.project.ui.UITheme.*;

public final class UIUtils {

    private UIUtils() {}

    public static void drawPixelBox(GraphicsContext gc, double x, double y, double w, double h) {
        int p = 3; // pixel step size

        // Stepped outer border (gold)
        gc.setFill(BOX_BORDER_OUTER);
        gc.fillRect(x + p, y - p, w - p * 2, p);       // top
        gc.fillRect(x + p, y + h, w - p * 2, p);       // bottom
        gc.fillRect(x - p, y + p, p, h - p * 2);       // left
        gc.fillRect(x + w, y + p, p, h - p * 2);       // right
        // Corner steps
        gc.fillRect(x, y, p, p);
        gc.fillRect(x + w - p, y, p, p);
        gc.fillRect(x, y + h - p, p, p);
        gc.fillRect(x + w - p, y + h - p, p, p);

        // Inner border
        gc.setFill(BOX_BORDER_INNER);
        gc.fillRect(x + p, y, w - p * 2, p);
        gc.fillRect(x + p, y + h - p, w - p * 2, p);
        gc.fillRect(x, y + p, p, h - p * 2);
        gc.fillRect(x + w - p, y + p, p, h - p * 2);

        // Background fill
        gc.setFill(BOX_BG);
        gc.fillRect(x + p, y + p, w - p * 2, h - p * 2);

        // Bright corner accent dots
        gc.setFill(BOX_CORNER);
        int cs = 3;
        gc.fillRect(x, y, cs, cs);
        gc.fillRect(x + w - cs, y, cs, cs);
        gc.fillRect(x, y + h - cs, cs, cs);
        gc.fillRect(x + w - cs, y + h - cs, cs, cs);
        // Extra outer staircase dots
        gc.fillRect(x + p, y - p, cs, cs);
        gc.fillRect(x + w - p - cs, y - p, cs, cs);
        gc.fillRect(x - p, y + p, cs, cs);
        gc.fillRect(x + w, y + p, cs, cs);
        gc.fillRect(x + p, y + h, cs, cs);
        gc.fillRect(x + w - p - cs, y + h, cs, cs);
        gc.fillRect(x - p, y + h - p - cs, cs, cs);
        gc.fillRect(x + w, y + h - p - cs, cs, cs);
    }


    public static double getTextWidth(String text, Font font) {
        Text temp = new Text(text);
        temp.setFont(font);
        return temp.getLayoutBounds().getWidth();
    }

    public static double getTextHeight(String text, Font font) {
        Text temp = new Text(text);
        temp.setFont(font);
        return temp.getLayoutBounds().getHeight();
    }

    public static List<String> wrapText(String text, Font font, double maxWidth) {
        List<String> lines = new ArrayList<>();
        
        // Handle explicit line breaks (\n) from database
        String normalized = text.replace("\r\n", "\n").replace('\r', '\n');
        String[] rawLines = normalized.split("\n", -1);
        
        for (String rawLine : rawLines) {
            if (rawLine.isEmpty()) {
                lines.add("");
                continue;
            }
            
            // Wrap each line by words if it exceeds maxWidth
            String[] words = rawLine.split(" ");
            StringBuilder current = new StringBuilder();

            for (String word : words) {
                String test = current.isEmpty() ? word : current + " " + word;
                if (getTextWidth(test, font) > maxWidth && !current.isEmpty()) {
                    lines.add(current.toString());
                    current = new StringBuilder(word);
                } else {
                    current = new StringBuilder(test);
                }
            }
            if (!current.isEmpty()) {
                lines.add(current.toString());
            }
        }
        
        return lines;
    }

    // Draws the pixelated border used by the title and character-name screens.
    public static void drawScreenBorder(GraphicsContext gc, int screenWidth, int screenHeight) {
        int p = 3;
        gc.setFill(UITheme.BOX_BORDER_OUTER);
        for (int i = p * 3; i < screenWidth - p * 3; i += p) {
            gc.fillRect(i, p * 2, p, p);
            gc.fillRect(i, screenHeight - p * 3, p, p);
        }
        for (int i = p * 3; i < screenHeight - p * 3; i += p) {
            gc.fillRect(p * 2, i, p, p);
            gc.fillRect(screenWidth - p * 3, i, p, p);
        }

        gc.setFill(UITheme.BOX_CORNER);
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
