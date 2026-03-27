package com.gvi.project.ui;

import com.gvi.project.helper.ImageHelper;
import com.gvi.project.models.questions.Reward;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static com.gvi.project.ui.UITheme.*;
import static com.gvi.project.ui.UIUtils.*;

/**
 * WinScreen zeigt die erreichte Medaille an, wenn ein neuer Schwellenwert erreicht wurde.
 * Jede Medaille wird nur einmal angezeigt (einmaliger Trigger).
 * Das Spiel läuft nach dem Schließen nahtlos weiter ohne Reset.
 */
public class WinScreen extends GameScreen {

    private static final Logger log = LoggerFactory.getLogger(WinScreen.class);

    private final int screenWidth;
    private final int screenHeight;
    
    // Medal sprites
    private Image medalBronze;
    private Image medalSilver;
    private Image medalGold;
    private Image medalGoldPerfect;

    public WinScreen(int screenWidth, int screenHeight) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        loadMedalSprites();
    }
    
    private void loadMedalSprites() {
        try {
            // Lade die Medaillen-Sprites aus dem Resources-Ordner
            medalBronze = ImageHelper.getImage("/sprites/medals/medal_bronze.png");
            medalSilver = ImageHelper.getImage("/sprites/medals/medal_silver.png");
            medalGold = ImageHelper.getImage("/sprites/medals/medal_gold.png");
            medalGoldPerfect = ImageHelper.getImage("/sprites/medals/medal_gold_perfect.png");
                
        } catch (IOException e) {
            System.err.println("Failed to load medal sprites: " + e.getMessage());
        }
    }

    public void draw(GraphicsContext gc, String formattedTime, Reward reward, double percentage, int score, int maxScore) {
        gc.setFill(OVERLAY_MEDIUM);
        gc.fillRect(0, 0, screenWidth, screenHeight);

        double centerBoxW = 600;
        double centerBoxH = 380;
        double cbx = screenWidth / 2.0 - centerBoxW / 2.0;
        double cby = screenHeight / 2.0 - centerBoxH / 2.0;
        drawPixelBox(gc, cbx, cby, centerBoxW, centerBoxH);

        gc.setFont(FONT_MD);
        gc.setFill(TEXT_WHITE);
        String text = "You found the Treasure!";
        double textW = getTextWidth(text, FONT_MD);
        gc.fillText(text, screenWidth / 2.0 - textW / 2.0, cby + 40);

        gc.setFont(FONT_LG);
        gc.setFill(TEXT_GOLD);
        text = "Congratulations!";
        textW = getTextWidth(text, FONT_LG);
        gc.fillText(text, screenWidth / 2.0 - textW / 2.0, cby + 85);

        // Time
        gc.setFont(FONT_SM);
        gc.setFill(TEXT_GRAY);
        text = "Time: " + formattedTime;
        textW = getTextWidth(text, FONT_SM);
        gc.fillText(text, screenWidth / 2.0 - textW / 2.0, cby + 120);

        // Score
        gc.setFont(FONT_SM);
        gc.setFill(TEXT_WHITE);
        text = "Score: " + score + " / " + maxScore + " (" + String.format("%.1f", percentage) + "%)";
        textW = getTextWidth(text, FONT_SM);
        gc.fillText(text, screenWidth / 2.0 - textW / 2.0, cby + 150);

        // Reward Section - Zeigt die erreichte Medaille an
        if (reward != Reward.NONE) {
            // Hole das passende Medaillen-Sprite
            Image medalSprite = getMedalSprite(reward);
            
            if (medalSprite != null) {
                // Zeichne Medaille zentriert mit korrektem Seitenverhältnis
                double medalHeight = 128;
                double medalWidth = medalSprite.getWidth() * (medalHeight / medalSprite.getHeight());
                // Zentriere auf dem Bildschirm
                double medalX = (screenWidth - medalWidth) / 2.0;
                double medalY = cby + 165;
                
                    gc.setFill(Color.MAGENTA);
                    gc.fillRect(medalX, medalY, medalWidth, medalHeight);
    
                // Draw pixelated background for medal
                gc.drawImage(medalSprite, medalX, medalY, medalWidth, medalHeight);
            }

            // Medaillen-Text mit passender Farbe
            gc.setFont(FONT_MD);
            Color rewardColor;
            switch (reward) {
                case GOLD_PERFECT:
                    rewardColor = Color.rgb(255, 220, 0); // Helles Gold für Special
                    break;
                case GOLD:
                    rewardColor = Color.rgb(255, 215, 0); // Gold
                    break;
                case SILVER:
                    rewardColor = Color.rgb(192, 192, 192); // Silber
                    break;
                case BRONZE:
                    rewardColor = Color.rgb(205, 127, 50); // Bronze
                    break;
                default:
                    rewardColor = TEXT_WHITE;
            }
            gc.setFill(rewardColor);
            text = "Belohnung: " + reward.getDisplayName();
            textW = getTextWidth(text, FONT_MD);
            gc.fillText(text, screenWidth / 2.0 - textW / 2.0, cby + 315);
        } else {
            // Keine Medaille erreicht
            gc.setFont(FONT_SM);
            gc.setFill(TEXT_GRAY);
            text = "Leider keine Medaille - versuche es erneut!";
            textW = getTextWidth(text, FONT_SM);
            gc.fillText(text, screenWidth / 2.0 - textW / 2.0, cby + 220);
        }

        // Hinweis zum Weiterspielen (nahtloser Spielfluss)
        gc.setFont(FONT_XS);
        gc.setFill(TEXT_GRAY);
        text = "Drücke ENTER um weiterzuspielen";
        textW = getTextWidth(text, FONT_XS);
        gc.fillText(text, screenWidth / 2.0 - textW / 2.0, cby + 340);
    }

    /**
     * Gibt das passende Medaillen-Sprite basierend auf der Belohnungsstufe zurück.
     * 
     * @param reward die Belohnungsstufe
     * @return das Medaillen-Sprite oder null wenn keine Medaille
     */
    private Image getMedalSprite(Reward reward) {
        switch (reward) {
            case BRONZE:
                return medalBronze;
            case SILVER:
                return medalSilver;
            case GOLD:
                return medalGold;
            case GOLD_PERFECT:
                return medalGoldPerfect;
            default:
                return null;
        }
    }
}
