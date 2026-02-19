package com.gvi.project.ui;

import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.io.InputStream;

public final class UITheme {

    public static final Color BOX_BG = Color.rgb(42, 64, 48);
    public static final Color BOX_BORDER_OUTER = Color.rgb(198, 160, 48);
    public static final Color BOX_BORDER_INNER = Color.rgb(58, 80, 60);
    public static final Color BOX_CORNER = Color.rgb(240, 200, 80);

    public static final Color TEXT_WHITE = Color.rgb(235, 230, 210);
    public static final Color TEXT_GOLD = Color.rgb(198, 160, 48);
    public static final Color TEXT_GRAY = Color.rgb(170, 185, 170);

    public static final Color ANSWER_BG = Color.rgb(50, 72, 54);
    public static final Color CORRECT_BG = Color.rgb(30, 100, 40);
    public static final Color WRONG_BG = Color.rgb(110, 30, 30);
    public static final Color CORRECT_TEXT = Color.rgb(80, 240, 80);
    public static final Color WRONG_TEXT = Color.rgb(240, 80, 80);

    public static final Color TITLE_BG = Color.rgb(30, 46, 34);
    public static final Color OVERLAY_DARK = Color.rgb(0, 0, 0, 0.7);
    public static final Color OVERLAY_MEDIUM = Color.rgb(0, 0, 0, 0.6);
    public static final Color MINIMAP_BORDER = Color.rgb(198, 160, 48);
    public static final Color MINIMAP_BG = Color.rgb(0, 0, 0, 0.7);

    // Fonts 
    public static Font FONT_XS;   // 14px
    public static Font FONT_SM;   // 16px
    public static Font FONT_MD;   // 18px
    public static Font FONT_LG;   // 24px
    public static Font FONT_XL;   // 36px

    static {
        loadFonts();
    }

    private static void loadFonts() {
        try {
            InputStream is = UITheme.class.getResourceAsStream("/fonts/Lower Pixel Regular 400.otf");
            if (is != null) {
                Font base = Font.loadFont(is, 14);
                if (base != null) {
                    String family = base.getFamily();
                    FONT_XS = Font.font(family, 14);
                    FONT_SM = Font.font(family, 16);
                    FONT_MD = Font.font(family, 18);
                    FONT_LG = Font.font(family, 24);
                    FONT_XL = Font.font(family, 36);
                    return;
                }
            }
        } catch (Exception e) {
            System.err.println("Could not load pixel font: " + e.getMessage());
        }
        // Fallback 
        FONT_XS = Font.font("Arial", 14);
        FONT_SM = Font.font("Arial", 16);
        FONT_MD = Font.font("Arial", 18);
        FONT_LG = Font.font("Arial", 24);
        FONT_XL = Font.font("Arial", 36);
    }

    private UITheme() {} 
}
