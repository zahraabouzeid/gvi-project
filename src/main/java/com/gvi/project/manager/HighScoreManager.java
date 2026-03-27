package com.gvi.project.manager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Verwaltet den persistenten High Score über alle Spielläufe hinweg.
 * Speichert den besten Score in saves/highscore.dat.
 */
public class HighScoreManager {

    private static final Logger log = LoggerFactory.getLogger(HighScoreManager.class);
    private static final Path SAVE_PATH = Paths.get("saves", "highscore.dat");

    private int highScore;

    public HighScoreManager() {
        highScore = load();
    }

    public int getHighScore() {
        return highScore;
    }

    /**
     * Aktualisiert den High Score, wenn der neue Score höher ist.
     *
     * @param score der neue Score
     * @return true, wenn ein neuer High Score erreicht wurde
     */
    public boolean updateHighScore(int score) {
        if (score > highScore) {
            highScore = score;
            save();
            log.info("Neuer High Score: {}", highScore);
            return true;
        }
        return false;
    }

    private int load() {
        try {
            if (!Files.exists(SAVE_PATH)) return 0;
            String content = Files.readString(SAVE_PATH).trim();
            return Integer.parseInt(content);
        } catch (Exception e) {
            log.warn("High Score konnte nicht geladen werden", e);
            return 0;
        }
    }

    private void save() {
        try {
            Files.createDirectories(SAVE_PATH.getParent());
            Files.writeString(SAVE_PATH, String.valueOf(highScore));
        } catch (IOException e) {
            log.error("High Score konnte nicht gespeichert werden", e);
        }
    }
}
