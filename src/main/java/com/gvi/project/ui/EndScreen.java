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
 * Finaler End-Screen nach dem Durchschreiten der Win-Tür.
 * Zeigt Score-Übersicht, High Score und die aktuelle Medaille.
 */
public class EndScreen extends GameScreen {

    private static final Logger log = LoggerFactory.getLogger(EndScreen.class);

    private final int screenWidth;
    private final int screenHeight;

    private Image medalBronze;
    private Image medalSilver;
    private Image medalGold;
    private Image medalGoldPerfect;

    public EndScreen(int screenWidth, int screenHeight) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        loadMedalSprites();
    }

    private void loadMedalSprites() {
        try {
            medalBronze      = ImageHelper.getImage("/sprites/medals/medal_bronze.png");
            medalSilver      = ImageHelper.getImage("/sprites/medals/medal_silver.png");
            medalGold        = ImageHelper.getImage("/sprites/medals/medal_gold.png");
            medalGoldPerfect = ImageHelper.getImage("/sprites/medals/medal_gold_perfect.png");
        } catch (IOException e) {
            log.warn("Medaillen-Sprites für EndScreen konnten nicht geladen werden", e);
        }
    }

    /**
     * @param playerName     Name des Spielers
     * @param score          Erreichter Score
     * @param maxScore       Maximal möglicher Score
     * @param percentage     Prozentuale Leistung
     * @param formattedTime  Spielzeit als String
     * @param highScore      Bester Score über alle Spiele
     * @param isNewHighScore true wenn dieser Durchgang einen neuen High Score aufgestellt hat
     * @param reward         Aktuell verdiente Medaille (kann NONE sein)
     */
    public void draw(GraphicsContext gc, String playerName, int score, int maxScore,
                     double percentage, String formattedTime,
                     int highScore, boolean isNewHighScore, Reward reward) {

        // Dunkles Overlay über das Spiel
        gc.setFill(Color.rgb(0, 0, 0, 0.85));
        gc.fillRect(0, 0, screenWidth, screenHeight);

        double boxW = 560;
        double boxH = 460;
        double cbx = screenWidth / 2.0 - boxW / 2.0;
        double cby = screenHeight / 2.0 - boxH / 2.0;
        drawPixelBox(gc, cbx, cby, boxW, boxH);

        double cx = screenWidth / 2.0;
        double y = cby + 48;

        // --- Titel ---
        gc.setFont(FONT_XL);
        gc.setFill(TEXT_GOLD);
        String text = "You Win!";
        gc.fillText(text, cx - getTextWidth(text, FONT_XL) / 2.0, y);
        y += 44;

        // Spielername
        gc.setFont(FONT_SM);
        gc.setFill(TEXT_GRAY);
        text = playerName;
        gc.fillText(text, cx - getTextWidth(text, FONT_SM) / 2.0, y);
        y += 36;

        // --- Trennlinie ---
        gc.setFill(Color.rgb(198, 160, 48, 0.4));
        gc.fillRect(cbx + 40, y, boxW - 80, 2);
        y += 18;

        // --- Score ---
        gc.setFont(FONT_MD);
        gc.setFill(TEXT_WHITE);
        text = "Score";
        gc.fillText(text, cbx + 60, y);
        String scoreVal = score + " / " + maxScore + "  (" + String.format("%.1f", percentage) + "%)";
        gc.setFill(TEXT_GOLD);
        gc.fillText(scoreVal, cbx + boxW - 60 - getTextWidth(scoreVal, FONT_MD), y);
        y += 32;

        // --- Zeit ---
        gc.setFont(FONT_MD);
        gc.setFill(TEXT_WHITE);
        text = "Time";
        gc.fillText(text, cbx + 60, y);
        gc.setFill(TEXT_GRAY);
        gc.fillText(formattedTime, cbx + boxW - 60 - getTextWidth(formattedTime, FONT_MD), y);
        y += 32;

        // --- High Score ---
        gc.setFont(FONT_MD);
        gc.setFill(TEXT_WHITE);
        text = "High Score";
        gc.fillText(text, cbx + 60, y);
        if (isNewHighScore) {
            gc.setFill(Color.rgb(255, 220, 0));
            String hsVal = highScore + "  ★ New!";
            gc.fillText(hsVal, cbx + boxW - 60 - getTextWidth(hsVal, FONT_MD), y);
        } else {
            gc.setFill(TEXT_GRAY);
            String hsVal = String.valueOf(highScore);
            gc.fillText(hsVal, cbx + boxW - 60 - getTextWidth(hsVal, FONT_MD), y);
        }
        y += 28;

        // --- Trennlinie ---
        gc.setFill(Color.rgb(198, 160, 48, 0.4));
        gc.fillRect(cbx + 40, y, boxW - 80, 2);
        y += 20;

        // --- Medaille ---
        if (reward != Reward.NONE) {
            Image medalSprite = getMedalSprite(reward);
            if (medalSprite != null) {
                double medalH = 100;
                double medalW = medalSprite.getWidth() * (medalH / medalSprite.getHeight());
                gc.drawImage(medalSprite, cx - medalW / 2.0, y, medalW, medalH);
                y += medalH + 10;
            }
            gc.setFont(FONT_MD);
            gc.setFill(getMedalColor(reward));
            text = reward.getDisplayName();
            gc.fillText(text, cx - getTextWidth(text, FONT_MD) / 2.0, y);
        } else {
            gc.setFont(FONT_SM);
            gc.setFill(TEXT_GRAY);
            text = "No medal earned";
            gc.fillText(text, cx - getTextWidth(text, FONT_SM) / 2.0, y + 30);
        }

        // --- Hinweis ---
        gc.setFont(FONT_XS);
        gc.setFill(TEXT_GRAY);
        text = "Press ENTER for Main Menu";
        gc.fillText(text, cx - getTextWidth(text, FONT_XS) / 2.0, cby + boxH - 20);
    }

    private Image getMedalSprite(Reward reward) {
        switch (reward) {
            case BRONZE:       return medalBronze;
            case SILVER:       return medalSilver;
            case GOLD:         return medalGold;
            case GOLD_PERFECT: return medalGoldPerfect;
            default:           return null;
        }
    }

    private Color getMedalColor(Reward reward) {
        switch (reward) {
            case GOLD_PERFECT: return Color.rgb(255, 220, 0);
            case GOLD:         return Color.rgb(255, 215, 0);
            case SILVER:       return Color.rgb(192, 192, 192);
            case BRONZE:       return Color.rgb(205, 127, 50);
            default:           return TEXT_WHITE;
        }
    }
}
