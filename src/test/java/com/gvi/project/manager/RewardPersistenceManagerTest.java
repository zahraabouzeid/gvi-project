package com.gvi.project.manager;

import com.gvi.project.models.questions.Reward;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RewardPersistenceManagerTest {

    @TempDir
    Path tempDir;

    @Test
    void shouldLoadKnownRewardsAndIgnoreUnknownEntries() throws IOException {
        Path saveDir = tempDir.resolve("saves");
        Files.createDirectories(saveDir);
        Files.writeString(saveDir.resolve("rewards.dat"), "GOLD\nUNKNOWN\n\nBRONZE\n");

        RewardPersistenceManager manager = new RewardPersistenceManager(saveDir);

        assertEquals(Set.of(Reward.GOLD, Reward.BRONZE), manager.getAchievedRewards());
        assertTrue(manager.hasReward(Reward.GOLD));
        assertFalse(manager.hasReward(Reward.SILVER));
    }

    @Test
    void checkAndMarkRewardShouldPersistOnlyNewRewards() throws IOException {
        Path saveDir = tempDir.resolve("progress");
        RewardPersistenceManager manager = new RewardPersistenceManager(saveDir);

        assertFalse(manager.checkAndMarkReward(Reward.NONE));
        assertTrue(manager.checkAndMarkReward(Reward.SILVER));
        assertFalse(manager.checkAndMarkReward(Reward.SILVER));

        String fileContent = Files.readString(saveDir.resolve("rewards.dat"));

        assertTrue(fileContent.contains("SILVER"));
        assertEquals(Set.of(Reward.SILVER), manager.getAchievedRewards());
    }

    @Test
    void resetAllRewardsShouldClearPersistedRewards() throws IOException {
        Path saveDir = tempDir.resolve("state");
        RewardPersistenceManager manager = new RewardPersistenceManager(saveDir);
        manager.checkAndMarkReward(Reward.BRONZE);
        manager.checkAndMarkReward(Reward.GOLD);

        manager.resetAllRewards();

        assertTrue(manager.getAchievedRewards().isEmpty());
        assertEquals("", Files.readString(saveDir.resolve("rewards.dat")));
    }
}