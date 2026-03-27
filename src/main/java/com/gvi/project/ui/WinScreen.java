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
 * Mid-Game Medaillen-Popup: wird angezeigt wenn ein neuer Schwellenwert
 * erreicht wird. Das Spiel läuft nach dem Schließen nahtlos weiter.
 */
public class WinScreen extends GameScreen {

    private static final Logger log = LoggerFactory.getLogger(WinScreen.class);

    private final int screenWidth;
    private final int screenHeight;

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
            medalBronze     = ImageHelper.getImage("/sprites/medals/medal_bronze.png");
            medalSilver     = ImageHelper.getImage("/sprites/medals/medal_silver.png");
            medalGold       = ImageHelper.getImage("/sprites/medals/medal_gold.png");
            medalGoldPerfect = ImageHelper.getImage("/sprites/medals/medal_gold_perfect.png");
        } catch (IOException e) {
            log.warn("Medaillen-Sprites konnten nicht geladen werden", e);
        }
    }

    public void draw(GraphicsContext gc, Reward reward) {
        gc.setFill(OVERLAY_MEDIUM);
        gc.fillRect(0, 0, screenWidth, screenHeight);

        double boxW = 480;
        double boxH = 300;
        double cbx = screenWidth / 2.0 - boxW / 2.0;
        double cby = screenHeight / 2.0 - boxH / 2.0;
        drawPixelBox(gc, cbx, cby, boxW, boxH);

        gc.setFont(FONT_LG);
        gc.setFill(TEXT_GOLD);
        String text = "New Medal!";
        double textW = getTextWidth(text, FONT_LG);
        gc.fillText(text, screenWidth / 2.0 - textW / 2.0, cby + 50);

        if (reward != Reward.NONE) {
            Image medalSprite = getMedalSprite(reward);
            if (medalSprite != null) {
                double medalH = 110;
                double medalW = medalSprite.getWidth() * (medalH / medalSprite.getHeight());
                gc.drawImage(medalSprite, screenWidth / 2.0 - medalW / 2.0, cby + 65, medalW, medalH);
            }

            gc.setFont(FONT_MD);
            gc.setFill(getMedalColor(reward));
            text = reward.getDisplayName();
            textW = getTextWidth(text, FONT_MD);
            gc.fillText(text, screenWidth / 2.0 - textW / 2.0, cby + 200);
        }

        gc.setFont(FONT_XS);
        gc.setFill(TEXT_GRAY);
        text = "Press ENTER to continue";
        textW = getTextWidth(text, FONT_XS);
        gc.fillText(text, screenWidth / 2.0 - textW / 2.0, cby + boxH - 20);
    }

    private Image getMedalSprite(Reward reward) {
        switch (reward) {
            case BRONZE:      return medalBronze;
            case SILVER:      return medalSilver;
            case GOLD:        return medalGold;
            case GOLD_PERFECT: return medalGoldPerfect;
            default:          return null;
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
