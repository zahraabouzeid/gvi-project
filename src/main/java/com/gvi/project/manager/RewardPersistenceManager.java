package com.gvi.project.manager;

import com.gvi.project.models.questions.Reward;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

/**
 * Manager für das permanente Speichern und Laden von erreichten Medaillen.
 * Stellt sicher, dass jede Medaille nur einmal angezeigt wird.
 */
public class RewardPersistenceManager {
    
    private static final Logger log = LoggerFactory.getLogger(RewardPersistenceManager.class);
    private static final String SAVE_DIR = "saves";
    private static final String REWARD_FILE = "rewards.dat";
    
    // Set der bereits erreichten und angezeigten Medaillen
    private final Set<Reward> achievedRewards;
    
    public RewardPersistenceManager() {
        this.achievedRewards = new HashSet<>();
        loadRewards();
    }
    
    /**
     * Prüft, ob eine Medaille bereits erreicht wurde und angezeigt werden sollte.
     * Wenn die Medaille neu ist, wird sie als erreicht markiert und gespeichert.
     * 
     * @param reward die zu prüfende Medaille
     * @return true, wenn die Medaille neu ist und angezeigt werden soll
     */
    public boolean checkAndMarkReward(Reward reward) {
        // NONE wird nie gespeichert oder angezeigt
        if (reward == Reward.NONE) {
            return false;
        }
        
        // Prüfen ob bereits erreicht
        if (achievedRewards.contains(reward)) {
            return false; // Bereits erreicht, nicht anzeigen
        }
        
        // Neue Medaille! Markieren und speichern
        achievedRewards.add(reward);
        saveRewards();
        return true;
    }
    
    /**
     * Prüft, ob eine Medaille bereits erreicht wurde (ohne sie zu markieren).
     * 
     * @param reward die zu prüfende Medaille
     * @return true, wenn die Medaille bereits erreicht wurde
     */
    public boolean hasReward(Reward reward) {
        return achievedRewards.contains(reward);
    }
    
    /**
     * Gibt alle erreichten Medaillen zurück.
     * 
     * @return Set aller erreichten Medaillen
     */
    public Set<Reward> getAchievedRewards() {
        return new HashSet<>(achievedRewards);
    }
    
    /**
     * Lädt die erreichten Medaillen aus der Datei.
     */
    private void loadRewards() {
        try {
            Path savePath = getSavePath();
            if (!Files.exists(savePath)) {
                log.info("Keine gespeicherten Rewards gefunden. Starte mit leerem Set.");
                return;
            }
            
            try (BufferedReader reader = new BufferedReader(new FileReader(savePath.toFile()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    line = line.trim();
                    if (!line.isEmpty()) {
                        try {
                            Reward reward = Reward.valueOf(line);
                            achievedRewards.add(reward);
                        } catch (IllegalArgumentException e) {
                            log.warn("Unbekannte Medaille in Datei: {}", line);
                        }
                    }
                }
                log.info("Rewards geladen: {}", achievedRewards);
            }
        } catch (IOException e) {
            log.error("Fehler beim Laden der Rewards", e);
        }
    }
    
    /**
     * Speichert die erreichten Medaillen in die Datei.
     */
    private void saveRewards() {
        try {
            Path savePath = getSavePath();
            
            // Verzeichnis erstellen falls nicht vorhanden
            Files.createDirectories(savePath.getParent());
            
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(savePath.toFile()))) {
                for (Reward reward : achievedRewards) {
                    writer.write(reward.name());
                    writer.newLine();
                }
            }
            log.info("Rewards gespeichert: {}", achievedRewards);
        } catch (IOException e) {
            log.error("Fehler beim Speichern der Rewards", e);
        }
    }
    
    /**
     * Gibt den Pfad zur Reward-Datei zurück.
     * 
     * @return Path zur Reward-Datei
     */
    private Path getSavePath() {
        return Paths.get(SAVE_DIR, REWARD_FILE);
    }
    
    public void resetSession() {
        achievedRewards.clear();
        log.info("Medaillen-Session zurückgesetzt");
    }

    public void resetAllRewards() {
        achievedRewards.clear();
        saveRewards();
        log.info("Alle Rewards zurückgesetzt");
    }
}
