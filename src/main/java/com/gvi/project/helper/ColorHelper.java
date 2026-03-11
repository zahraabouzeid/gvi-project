package com.gvi.project.helper;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.paint.Color;

import java.util.HashMap;
import java.util.Map;

public class ColorHelper {
	public static Color getMostCommonColor(Image image) {
		PixelReader reader = image.getPixelReader();
		int width = (int) image.getWidth();
		int height = (int) image.getHeight();

		Map<Integer, Integer> colorCount = new HashMap<>();

		int step = 2; // sample every 2nd pixel (speed boost)

		for (int y = 0; y < height; y += step) {
			for (int x = 0; x < width; x += step) {

				Color c = reader.getColor(x, y);

				if (c.getOpacity() == 0) continue;

				// Quantize to reduce similar colors
				int r = (int)(c.getRed() * 255) / 32;
				int g = (int)(c.getGreen() * 255) / 32;
				int b = (int)(c.getBlue() * 255) / 32;

				int key = (r << 16) | (g << 8) | b;

				colorCount.put(key, colorCount.getOrDefault(key, 0) + 1);
			}
		}

		int maxCount = 0;
		int dominantKey = 0;

		for (Map.Entry<Integer, Integer> entry : colorCount.entrySet()) {
			if (entry.getValue() > maxCount) {
				maxCount = entry.getValue();
				dominantKey = entry.getKey();
			}
		}

		int r = ((dominantKey >> 16) & 0xFF) * 32;
		int g = ((dominantKey >> 8) & 0xFF) * 32;
		int b = (dominantKey & 0xFF) * 32;

		return Color.rgb(
				Math.min(r, 255),
				Math.min(g, 255),
				Math.min(b, 255)
		);
	}

	public static Color getAverageColor(Image image) {
		PixelReader reader = image.getPixelReader();
		int width = (int) image.getWidth();
		int height = (int) image.getHeight();

		double r = 0, g = 0, b = 0;
		int count = 0;

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				Color color = reader.getColor(x, y);

				// Ignore fully transparent pixels if needed
				if (color.getOpacity() > 0) {
					r += color.getRed();
					g += color.getGreen();
					b += color.getBlue();
					count++;
				}
			}
		}

		if (count == 0) return Color.TRANSPARENT;

		return new Color(r / count, g / count, b / count, 1.0);
	}
}
