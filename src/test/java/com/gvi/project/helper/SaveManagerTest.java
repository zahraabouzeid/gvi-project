package com.gvi.project.helper;

import com.gvi.project.GamePanel;
import com.gvi.project.manager.GameProgressManager;
import com.gvi.project.models.entities.Player;
import com.gvi.project.models.game_maps.GameMap;
import com.gvi.project.models.game_maps.GameMaps;
import com.gvi.project.models.objects.SuperObject;
import org.junit.jupiter.api.Test;
import sun.misc.Unsafe;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SaveManagerTest {

    @Test
    void hasSaveDeleteAndSlotInfoShouldWork() throws IOException {
        Path tempDir = Files.createTempDirectory("save-manager-test-");
        SaveManager manager = new SaveManager(tempDir);

        int slot = 77;
        assertFalse(manager.hasSave(slot));

        Files.writeString(tempDir.resolve("save_77.json"), """
                {
                  "playerName": "Tester",
                  "score": 42,
                  "currentMap": "MAP_02",
                  "savedAt": "01.01.2026 10:30"
                }
                """);

        assertTrue(manager.hasSave(slot));
        SaveManager.SlotInfo info = manager.getSlotInfo(slot);
        assertTrue(info.exists());
        assertEquals("Tester", info.playerName());
        assertEquals(42, info.score());
        assertEquals("MAP_02", info.mapName());

        assertTrue(manager.deleteSave(slot));
        assertFalse(manager.hasSave(slot));
    }

    @Test
    void saveAndLoadShouldRoundTripPlayerAndObjects() throws Exception {
        Path tempDir = Files.createTempDirectory("save-manager-roundtrip-");
        SaveManager manager = new SaveManager(tempDir);

        TestGamePanel gp = allocate(TestGamePanel.class);
        Player player = allocate(Player.class);

        setField(gp, "player", player);
        setField(gp, "obj", new ArrayList<SuperObject>());
        setField(gp, "progressManager", new GameProgressManager());

        GameMap map = new GameMap(2, "MAP_02", 10, 10);
        setField(gp, "currentMap", map);

        player.playerName = "Alice";
        player.worldX = 96;
        player.worldY = 144;
        player.gridX = 2;
        player.gridY = 3;
        player.direction = "left";
        player.score = 15;
        player.healthHalf = 7;
        player.maxHealthHalf = 10;
        player.playerItems = new HashMap<>();
        player.playerItems.put("key_iron", 1);
        player.playerItems.put("key_gold", 2);
        player.playerItems.put("key_copper", 3);

        DummyObject keepObject = new DummyObject();
        keepObject.worldX = 96;
        keepObject.worldY = 144;

        DummyObject removeObject = new DummyObject();
        removeObject.worldX = 500;
        removeObject.worldY = 500;

        gp.obj.add(keepObject);
        gp.obj.add(removeObject);

        int slot = 5;
        assertTrue(manager.save(gp, slot));

        player.playerName = "Changed";
        player.worldX = 0;
        player.worldY = 0;
        player.gridX = 0;
        player.gridY = 0;
        player.targetGridX = -1;
        player.targetGridY = -1;
        player.direction = "up";
        player.score = 0;
        player.healthHalf = 1;
        player.maxHealthHalf = 1;
        player.playerItems = new HashMap<>();
        player.isMoving = true;
        player.isDead = true;

        DummyObject mapLoadedKeep = new DummyObject();
        mapLoadedKeep.worldX = 96;
        mapLoadedKeep.worldY = 144;

        DummyObject mapLoadedExtra = new DummyObject();
        mapLoadedExtra.worldX = 700;
        mapLoadedExtra.worldY = 700;

        gp.mapObjectsToLoad = List.of(mapLoadedKeep, mapLoadedExtra);

        assertTrue(manager.load(gp, slot));

        assertEquals("Alice", player.playerName);
        assertEquals(96, player.worldX);
        assertEquals(144, player.worldY);
        assertEquals(2, player.gridX);
        assertEquals(3, player.gridY);
        assertEquals(2, player.targetGridX);
        assertEquals(3, player.targetGridY);
        assertEquals("left", player.direction);
        assertEquals(15, player.score);
        assertEquals(7, player.healthHalf);
        assertEquals(10, player.maxHealthHalf);
        assertEquals(1, player.playerItems.get("key_iron"));
        assertEquals(2, player.playerItems.get("key_gold"));
        assertEquals(3, player.playerItems.get("key_copper"));
        assertFalse(player.isMoving);
        assertFalse(player.isDead);

        assertEquals(GameMaps.MAP_02, gp.loadedMap);
        assertNotNull(gp.currentMap);
        assertEquals(1, gp.obj.size());
        assertEquals(96, gp.obj.getFirst().worldX);
        assertEquals(144, gp.obj.getFirst().worldY);
    }

    @Test
    void loadShouldReturnFalseWhenSlotMissing() throws Exception {
        Path tempDir = Files.createTempDirectory("save-manager-missing-");
        SaveManager manager = new SaveManager(tempDir);

        TestGamePanel gp = allocate(TestGamePanel.class);
        Player player = allocate(Player.class);
        setField(gp, "player", player);
        setField(gp, "obj", new ArrayList<SuperObject>());
        setField(gp, "progressManager", new GameProgressManager());

        assertFalse(manager.load(gp, 999));
    }

    private static final Unsafe UNSAFE = getUnsafe();

    @SuppressWarnings("unchecked")
    private static <T> T allocate(Class<T> type) throws InstantiationException {
        return (T) UNSAFE.allocateInstance(type);
    }

    private static void setField(Object target, String name, Object value) throws Exception {
        Field field = findField(target.getClass(), name);
        field.setAccessible(true);
        field.set(target, value);
    }

    private static Field findField(Class<?> type, String name) throws NoSuchFieldException {
        Class<?> current = type;
        while (current != null) {
            try {
                return current.getDeclaredField(name);
            } catch (NoSuchFieldException ignored) {
                current = current.getSuperclass();
            }
        }
        throw new NoSuchFieldException(name);
    }

    private static Unsafe getUnsafe() {
        try {
            Field field = Unsafe.class.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            return (Unsafe) field.get(null);
        } catch (Exception e) {
            throw new RuntimeException("Unable to access Unsafe", e);
        }
    }

    static class DummyObject extends SuperObject {
    }

    static class TestGamePanel extends GamePanel {
        GameMaps loadedMap;
        List<SuperObject> mapObjectsToLoad;

        TestGamePanel() {
            throw new UnsupportedOperationException("Use Unsafe allocation in tests");
        }

        @Override
        public void loadMap(GameMaps map) {
            loadedMap = map;
            this.currentMap = new GameMap(map.ordinal(), map.name(), 1, 1);
            if (obj != null) {
                obj.clear();
                if (mapObjectsToLoad != null) {
                    obj.addAll(mapObjectsToLoad);
                }
            }
            progressManager.applySnapshotIfPresent(this);
        }

        @Override
        public void clearObjects() {
            if (obj != null) {
                obj.clear();
            }
        }
    }
}
