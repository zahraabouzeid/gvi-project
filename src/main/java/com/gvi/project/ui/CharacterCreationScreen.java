package com.gvi.project.ui;

import com.gvi.project.models.entities.Player;
import com.gvi.project.models.sprite_sheets.Sprite;
import com.gvi.project.models.sprite_sheets.SpriteSheet;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

import static com.gvi.project.ui.UITheme.*;
import static com.gvi.project.ui.UIUtils.*;

/**
 * Character sprite selection screen.
 * Allows players to choose from available character sprite variations.
 */
public class CharacterCreationScreen extends GameScreen {

    private static final Logger log = LoggerFactory.getLogger(CharacterCreationScreen.class);

    private int blinkCounter = 0;
    private final Set<String> spriteLoadErrorsLogged = new HashSet<>();

    // Bereitgestellte sprites
    private final String[] availableSpriteNames = {
            "Hero",
            "Mushroom",
            "Goblin",
            "Skeleton"

    };

    private final String[] availableSpriteIds = {
            "Dungeon_HeroMan1",
            "Dungeon_MushroomMan16",
            "Dungeon_Goblin1",
            "Dungeon_Skeleton"

    };

    private int selectedSpriteIndex = 0;

    public CharacterCreationScreen() {}

    public void draw(GraphicsContext gc, Player player) {
        blinkCounter++;

        // Background
        gc.setFill(TITLE_BG);
        gc.fillRect(0, 0, screenWidth, screenHeight);

        // Draw border
        drawBorder(gc);

        // Title
        String title = "SELECT CHARACTER";
        gc.setFont(FONT_XL);
        double tw = getTextWidth(title, FONT_XL);
        double tx = screenWidth / 2.0 - tw / 2.0;
        double ty = screenHeight / 2.0 - 120;

        // Shadow
        gc.setFill(Color.rgb(0, 0, 0, 0.5));
        gc.fillText(title, tx + 3, ty + 3);
        gc.setFill(TEXT_GOLD);
        gc.fillText(title, tx, ty);

        // Draw sprite selection
        drawSpriteSelection(gc, ty + 80);

        // Instructions
        gc.setFont(FONT_XS);
        gc.setFill(TEXT_GRAY);
        String info = "Use LEFT/RIGHT arrow keys to select sprite";
        double iw = getTextWidth(info, FONT_XS);
        gc.fillText(info, screenWidth / 2.0 - iw / 2.0, screenHeight - 80);

        // Blinking prompt
        if ((blinkCounter / 40) % 2 == 0) {
            gc.setFont(FONT_MD);
            gc.setFill(TEXT_WHITE);
            String prompt = "Press ENTER to continue";
            double pw = getTextWidth(prompt, FONT_MD);
            gc.fillText(prompt, screenWidth / 2.0 - pw / 2.0, screenHeight - 50);
        }
    }

    private void drawSpriteSelection(GraphicsContext gc, double y) {
        // Draw available sprites
        double spriteSize = 16 * 3 * 1.5;
        double boxWidth = spriteSize + 2 * 18;
        double boxHeight = spriteSize * 2 + 2 * 20;
        double spacing = 20;
        double startX = screenWidth / 2.0 - (boxWidth * availableSpriteNames.length + spacing * (availableSpriteNames.length - 1)) / 2.0;
        double lineWidthSpriteBox = 2 ;
        double lineWidthBoxSelected = 5 ;

        for (int i = 0; i < availableSpriteNames.length; i++) {
            double boxX = startX + i * (boxWidth + spacing);
            double boxY = y;
            boolean isSelected = (i == selectedSpriteIndex);

            // Draw box
            if (isSelected) {
                gc.setFill(Color.web("#FFD700"));
                gc.fillRect(boxX - lineWidthBoxSelected, boxY - lineWidthBoxSelected, boxWidth + lineWidthBoxSelected * 2, boxHeight + lineWidthBoxSelected * 2);
                gc.setFill(Color.web("#1A1A1A"));
            } else {
                gc.setFill(Color.web("#404040"));
            }
            gc.fillRect(boxX, boxY, boxWidth, boxHeight);

            // Draw Player sprite
            try {
                String spriteSetName = availableSpriteIds[i];
                SpriteSheet sheet = new SpriteSheet("/sprites/tilemaps/damp-dungeons/Characters/" + spriteSetName);
                Sprite sprite = sheet.getSprite("walk", "down_1");

                double spriteX = boxX + (boxWidth - spriteSize) / 2.0;
                double spriteY = boxY + 15;

                // Draw the actual sprite image
                if (sprite != null && sprite.image != null) {
                    gc.drawImage(sprite.image, spriteX, spriteY, spriteSize * sprite.imageWidth, spriteSize * sprite.imageHeight);
                } else {
                    // Fallback: gray box if sprite fails to load
                    gc.setFill(Color.web("#555555"));
                    gc.fillRect(spriteX, spriteY, spriteSize, spriteSize);
                }

                // Draw sprite border
                gc.setStroke(isSelected ? TEXT_GOLD : Color.web("#888888"));
                gc.setLineWidth(2);
                gc.strokeRect(spriteX - lineWidthSpriteBox, spriteY - lineWidthSpriteBox, spriteSize * sprite.imageWidth + lineWidthSpriteBox * 2, spriteSize * sprite.imageHeight + lineWidthSpriteBox * 2);
            } catch (Exception e) {
                String spriteId = availableSpriteIds[i];
                if (spriteLoadErrorsLogged.add(spriteId)) {
                    log.warn("Failed to load character selection sprite '{}'", spriteId, e);
                }
                
                // Fallback if sprite loading fails
                gc.setFill(Color.web("#555555"));
                double spriteX = boxX + (boxWidth - spriteSize) / 2.0;
                double spriteY = y + 10;
                gc.fillRect(spriteX, spriteY, spriteSize, spriteSize);

                gc.setStroke(isSelected ? TEXT_GOLD : Color.web("#888888"));
                gc.setLineWidth(2);
                gc.strokeRect(spriteX - 1, spriteY - 1, spriteSize + 2, spriteSize + 2);
                
                // Draw error indicator
                gc.setFont(FONT_XS);
                gc.setFill(Color.web("#FF6666"));
                gc.fillText("ERROR", spriteX + 15, spriteY + 30);
            }

            // Draw label
            gc.setFont(FONT_SM);
            gc.setFill(isSelected ? TEXT_WHITE : Color.BLACK);
            String name = availableSpriteNames[i];
            double tw = getTextWidth(name, FONT_SM);
            gc.fillText(name, boxX + boxWidth / 2.0 - tw / 2.0, boxY + boxHeight - 5);

            // Draw selection indicator
            if (isSelected) {
                gc.setFont(FONT_XS);
                gc.setFill(Color.web("#FFD700"));
                String selected = "Selected";
                double sw = getTextWidth(selected, FONT_XS);
                gc.fillText(selected, boxX + boxWidth / 2.0 - sw / 2.0, boxY + boxHeight + 30);
            }
        }
    }

    public void navigateLeft() {
        selectedSpriteIndex = (selectedSpriteIndex - 1 + availableSpriteNames.length) % availableSpriteNames.length;
    }

    public void navigateRight() {
        selectedSpriteIndex = (selectedSpriteIndex + 1) % availableSpriteNames.length;
    }

    public void navigateUp() {
        // Not used in sprite selection, but keeping for compatibility
    }

    public void navigateDown() {
        // Not used in sprite selection, but keeping for compatibility
    }

    public void applyChanges(Player player) {
        player.selectedSpriteSet = availableSpriteIds[selectedSpriteIndex];
    }

    public void reset() {
        blinkCounter = 0;
        selectedSpriteIndex = 0;
    }

    private void drawBorder(GraphicsContext gc) {
        UIUtils.drawScreenBorder(gc, screenWidth, screenHeight);
    }
}
