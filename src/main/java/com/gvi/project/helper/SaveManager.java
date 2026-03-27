package com.gvi.project.helper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.gvi.project.GamePanel;
import com.gvi.project.models.game_maps.GameMaps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

public class SaveManager {

    private static final Logger log = LoggerFactory.getLogger(SaveManager.class);

    private static final Path DEFAULT_SAVE_DIR = Path.of(System.getProperty("user.home"), ".sql-dungeon", "saves");
    private static final DateTimeFormatter TIMESTAMP_FMT = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
    private final ObjectMapper mapper;
    private final Path saveDir;

    public SaveManager() {
        this(DEFAULT_SAVE_DIR);
    }

    SaveManager(Path saveDir) {
        this.saveDir = saveDir;
        mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    private static String resolveCurrentMapName(GamePanel gp) {
        if (gp.currentMap == null) return GameMaps.MAP_01.name();
        try {
            GameMaps.valueOf(gp.currentMap.name.toUpperCase());
            return gp.currentMap.name.toUpperCase();
        } catch (IllegalArgumentException e) {
            return GameMaps.MAP_01.name();
        }
    }

    private static GameMaps parseMap(String name) {
        if (name == null) return GameMaps.MAP_01;
        try {
            return GameMaps.valueOf(name);
        } catch (IllegalArgumentException e) {
            return GameMaps.MAP_01;
        }
    }

    private Path slotFile(int slot) {
        return saveDir.resolve("save_" + slot + ".json");
    }

    public boolean hasSave(int slot) {
        return Files.exists(slotFile(slot));
    }

    public boolean deleteSave(int slot) {
        try {
            return Files.deleteIfExists(slotFile(slot));
        } catch (IOException e) {
            return false;
        }
    }

    public SlotInfo getSlotInfo(int slot) {
        if (!hasSave(slot)) return new SlotInfo(false, null, 0, null, null);
        try {
            SaveData d = mapper.readValue(slotFile(slot).toFile(), SaveData.class);
            return new SlotInfo(true, d.playerName, d.score, d.currentMap, d.savedAt);
        } catch (IOException e) {
            return new SlotInfo(false, null, 0, null, null);
        }
    }

    public boolean save(GamePanel gp, int slot) {
        SaveData data = new SaveData();
        data.playerName    = gp.player.playerName;
        data.worldX        = gp.player.worldX;
        data.worldY        = gp.player.worldY;
        data.gridX         = gp.player.gridX;
        data.gridY         = gp.player.gridY;
        data.direction     = gp.player.direction;
        data.score         = gp.player.score;
        data.healthHalf    = gp.player.healthHalf;
        data.maxHealthHalf = gp.player.maxHealthHalf;
        data.playerItems   = new HashMap<>(gp.player.playerItems);
        data.currentMap    = resolveCurrentMapName(gp);
        data.savedAt       = LocalDateTime.now().format(TIMESTAMP_FMT);
        data.playtime      = gp.ui != null ? gp.ui.playtime : 0;

        gp.progressManager.snapshotCurrentMap(gp);
        data.allMapObjects = new HashMap<>(gp.progressManager.getSnapshots());

        try {
            Files.createDirectories(saveDir);
            mapper.writeValue(slotFile(slot).toFile(), data);
            return true;
        } catch (IOException e) {
            log.warn("Save slot {} failed", slot, e);
            return false;
        }
    }

    public boolean load(GamePanel gp, int slot) {
        if (!hasSave(slot)) return false;
        SaveData data;
        try {
            data = mapper.readValue(slotFile(slot).toFile(), SaveData.class);
        } catch (IOException e) {
            log.warn("Load slot {} failed", slot, e);
            return false;
        }

        gp.player.playerName    = data.playerName != null ? data.playerName : "Player";
        gp.player.worldX        = data.worldX;
        gp.player.worldY        = data.worldY;
        gp.player.gridX         = data.gridX;
        gp.player.gridY         = data.gridY;
        gp.player.targetGridX   = data.gridX;
        gp.player.targetGridY   = data.gridY;
        gp.player.direction     = data.direction != null ? data.direction : "down";
        gp.player.score         = data.score;
        gp.player.healthHalf    = data.healthHalf;
        gp.player.maxHealthHalf = data.maxHealthHalf;
        gp.player.playerItems   = data.playerItems != null ? new HashMap<>(data.playerItems) : new HashMap<>();
        gp.player.isMoving      = false;
        gp.player.isDead        = false;
        if (gp.ui != null) gp.ui.playtime = data.playtime;

        gp.progressManager.reset();
        gp.currentMap = null;
        gp.progressManager.restoreFrom(data.allMapObjects);
        gp.loadMap(parseMap(data.currentMap));

        return true;
    }

    public record SlotInfo(boolean exists, String playerName, int score, String mapName, String savedAt) {}
}
