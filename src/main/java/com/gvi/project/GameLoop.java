package com.gvi.project;

import com.gvi.project.models.game_maps.GameMaps;
import com.gvi.project.models.objects.OBJ_QuizStation;
import com.gvi.project.ui.LoadingScreen;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.scene.paint.Color;

public class GameLoop extends AnimationTimer {

	private final GamePanel gp;

	double delta = 0;
	long lastTime = System.nanoTime();
	int drawCount = 0;
	long timer = 0;
	private int pauseNavCooldown = 0;
	private int slotNavCooldown  = 0;
	private int creationNavCooldown = 0;
	private int loadingCounter   = 0;
	private GameState loadSlotOrigin = GameState.PAUSE;

	public GameLoop(GamePanel gp) {
		this.gp = gp;
	}

	@Override
	public void handle(long now) {

		double fixedDelta = GeneralSettings.getDrawInterval() / 1_000_000_000.0;

		delta += (now - lastTime) / GeneralSettings.getDrawInterval();
		timer += (now - lastTime);
		lastTime = now;

		while (delta >= 1) {
			update(fixedDelta);
			delta--;
			drawCount++;
		}

		renderScreen();

		if (timer >= 1_000_000_000) {
			drawCount = 0;
			timer = 0;
		}
	}

	private void update(double fixedDelta) {
		GeneralSettings.setDevMode(gp.keyHandler.f2Pressed);

		if (gp.gameState == GameState.TITLE) {
			if (gp.keyHandler.enterPressed) {
				gp.keyHandler.enterPressed = false;
				gp.gameState = GameState.CHARACTER_NAME;
			}
			return;
		}
		if (gp.gameState == GameState.CHARACTER_NAME) {
			handleCharacterNameInput();
			return;
		}
		if (gp.gameState == GameState.CHARACTER_CREATION) {
			handleCharacterCreationInput();
			return;
		}
		if (gp.gameState == GameState.LOADING) {
			loadingCounter++;
			if (loadingCounter >= LoadingScreen.DURATION) {
				gp.gameState = GameState.PLAY;
			}
			return;
		}
		// Winscreen-Handling: Nahtloser Spielfluss ohne Reset
		// Wenn der Spieler ENTER drückt, wird nur der Screen geschlossen, Spiel läuft weiter
		if (gp.ui.gameFinished) {
			if (gp.keyHandler.enterPressed) {
				gp.keyHandler.enterPressed = false;
				gp.ui.closeWinScreen(); // Schließt nur den Screen, kein Reset
			}
			return;
		}
		if (gp.player.isDead) {
			if (gp.keyHandler.enterPressed) {
				gp.keyHandler.enterPressed = false;
				gp.loadMap(GameMaps.MAP_00);
				gp.player.setDefaultValues();
				gp.ui.resetGame();
				gp.interactingObjectIndex = -1;
				gp.gameState = GameState.PLAY;
			}
			return;
		}
		if (gp.gameState == GameState.PLAY) {
			GeneralSettings.setDevMode(gp.keyHandler.f2Pressed);

			// Cheat-Keys zum Testen des Belohnungssystems
			// Simuliert das Erreichen verschiedener Medaillen-Schwellenwerte
			if (gp.keyHandler.f7Pressed) {
				gp.keyHandler.f7Pressed = false;
				// Bronze: 60% (600/1000)
				gp.player.score = 600;
				gp.ui.setMaxPossiblePoints(1000);
				gp.ui.calculateReward();
				// Winscreen wird nur angezeigt, wenn die Medaille neu ist
				if (gp.ui.shouldShowWinScreen()) {
					gp.ui.gameFinished = true;
					gp.stopMusic();
					gp.playSE(4);
				}
				return;
			}
			if (gp.keyHandler.f8Pressed) {
				gp.keyHandler.f8Pressed = false;
				// Silber: 80% (800/1000)
				gp.player.score = 800;
				gp.ui.setMaxPossiblePoints(1000);
				gp.ui.calculateReward();
				// Winscreen wird nur angezeigt, wenn die Medaille neu ist
				if (gp.ui.shouldShowWinScreen()) {
					gp.ui.gameFinished = true;
					gp.stopMusic();
					gp.playSE(4);
				}
				return;
			}
			if (gp.keyHandler.f9Pressed) {
				gp.keyHandler.f9Pressed = false;
				// Gold: 95% (950/1000)
				gp.player.score = 950;
				gp.ui.setMaxPossiblePoints(1000);
				gp.ui.calculateReward();
				// Winscreen wird nur angezeigt, wenn die Medaille neu ist
				if (gp.ui.shouldShowWinScreen()) {
					gp.ui.gameFinished = true;
					gp.stopMusic();
					gp.playSE(4);
				}
				return;
			}
			if (gp.keyHandler.f10Pressed) {
				gp.keyHandler.f10Pressed = false;
				// Special Medaille: 99% (990/1000)
				gp.player.score = 990;
				gp.ui.setMaxPossiblePoints(1000);
				gp.ui.calculateReward();
				// Winscreen wird nur angezeigt, wenn die Medaille neu ist
				if (gp.ui.shouldShowWinScreen()) {
					gp.ui.gameFinished = true;
					gp.stopMusic();
					gp.playSE(4);
				}
				return;
			}
			if (gp.keyHandler.f11Pressed) {
				gp.keyHandler.f11Pressed = false;
				// Reset alle Medaillen (nur im Dev-Mode)
				gp.ui.resetAllRewards();
				gp.ui.openMessage("Alle Medaillen zurückgesetzt!");
				return;
			}

			if (gp.keyHandler.escPressed) {
				gp.keyHandler.escPressed = false;
				gp.ui.resetPauseScreen();
				gp.gameState = GameState.PAUSE;
				return;
			}

			if (GeneralSettings.isDevMode()) handleDebugInput();

			gp.animationSystem.tick(fixedDelta);
			gp.player.update();
		} else if (gp.gameState == GameState.QUIZ) {
			if (gp.ui.isAnswerFeedback()) {
				if (gp.ui.getFeedbackCounter() >= UI.FEEDBACK_DURATION) {
					boolean correct = gp.ui.isAnswerCorrect();

					if (correct) {
						if (gp.ui.advanceFillBlankIfNeeded()) {
							return;
						}
						int earnedPoints = gp.ui.getResolvedQuizPoints();
						
						// Punkte hinzufügen
						gp.player.score += earnedPoints;
						gp.ui.showFloatingScore(earnedPoints);
						
						// Reward-Check erfolgt nur beim Öffnen der Truhe, nicht nach jeder Frage
						
						gp.ui.closeQuiz();
						int idx = gp.interactingObjectIndex;
						if (idx != -1 && gp.obj.get(idx) != null) {
							gp.obj.get(idx).onConfirm(gp, idx);
						if (!gp.ui.isQuizOpen()) {
								gp.interactingObjectIndex = -1;
								gp.gameState = GameState.PLAY;
							}
						}
					} else {
						int lostPoints = gp.ui.getResolvedQuizPoints();
						
						gp.player.score = Math.max(0, gp.player.score + lostPoints); // lostPoints is already negative
						gp.ui.showFloatingScore(lostPoints);
						gp.playSE(5);
						gp.player.takeHalfHeartDamage();
						if (gp.player.isDead) {
							gp.playSE(6);
							gp.ui.closeQuiz();
							gp.interactingObjectIndex = -1;
							gp.gameState = GameState.PLAY;
							return;
						}
						gp.ui.resetQuizAfterWrongAnswer();
					}
				}
			} else {
				int num = gp.keyHandler.numberPressed;
				if (num >= 1 && gp.ui.getCurrentQuestion() != null) {
					if (gp.ui.handleQuizNumberInput(num)) {
						gp.keyHandler.numberPressed = -1;
					}
				}
				if (gp.keyHandler.enterPressed && gp.ui.getCurrentQuestion() != null) {
					gp.keyHandler.enterPressed = false;
					gp.ui.submitQuizSelection();
				}
				if (gp.keyHandler.escPressed) {
					gp.keyHandler.escPressed = false;
					gp.ui.closeQuiz();
					gp.interactingObjectIndex = -1;
					gp.gameState = GameState.PLAY;
				}
			}
		} else if (gp.gameState == GameState.PAUSE) {
			handlePauseInput();
		} else if (gp.gameState == GameState.SAVE_SLOT) {
			handleSaveSlotInput();
		} else if (gp.gameState == GameState.LOAD_SLOT) {
			handleLoadSlotInput();
		}
	}

	private void handlePauseInput() {
		if (pauseNavCooldown > 0) pauseNavCooldown--;
		if (gp.keyHandler.escPressed) {
			gp.keyHandler.escPressed = false;
			gp.gameState = GameState.PLAY;
			return;
		}
		if (pauseNavCooldown == 0) {
			if (gp.keyHandler.upPressed) {
				gp.ui.navigatePauseUp();
				pauseNavCooldown = 12;
			} else if (gp.keyHandler.downPressed) {
				gp.ui.navigatePauseDown();
				pauseNavCooldown = 12;
			}
		}
		if (gp.keyHandler.enterPressed) {
			gp.keyHandler.enterPressed = false;
			switch (gp.ui.getPauseSelectedOption()) {
				case 0 -> gp.gameState = GameState.PLAY;
				case 1 -> {
					gp.ui.openSaveSlot();
					slotNavCooldown = 12;
					gp.gameState = GameState.SAVE_SLOT;
				}
				case 2 -> {
					gp.ui.openLoadSlot();
					slotNavCooldown = 12;
					loadSlotOrigin = GameState.PAUSE;
					gp.gameState = GameState.LOAD_SLOT;
				}
				case 3 -> Platform.exit();
			}
		}
	}

	private void handleSaveSlotInput() {
		if (slotNavCooldown > 0) slotNavCooldown--;

		if (gp.ui.isConfirmDelete()) {
			if (gp.keyHandler.escPressed) {
				gp.keyHandler.escPressed = false;
				gp.ui.setConfirmDelete(false);
			} else if (gp.keyHandler.enterPressed) {
				gp.keyHandler.enterPressed = false;
				gp.saveManager.deleteSave(gp.ui.getSelectedSlot());
				gp.ui.setConfirmDelete(false);
				gp.ui.refreshSlotInfos();
			}
			return;
		}

		if (gp.keyHandler.delPressed) {
			gp.keyHandler.delPressed = false;
			if (gp.saveManager.hasSave(gp.ui.getSelectedSlot())) {
				gp.ui.setConfirmDelete(true);
			}
			return;
		}

		if (gp.keyHandler.escPressed) {
			gp.keyHandler.escPressed = false;
			gp.ui.resetPauseScreen();
			gp.gameState = GameState.PAUSE;
			return;
		}
		if (slotNavCooldown == 0) {
			if (gp.keyHandler.upPressed) {
				gp.ui.navigateSlotUp();
				slotNavCooldown = 12;
			} else if (gp.keyHandler.downPressed) {
				gp.ui.navigateSlotDown();
				slotNavCooldown = 12;
			}
		}
		if (gp.keyHandler.enterPressed) {
			gp.keyHandler.enterPressed = false;
			int slot = gp.ui.getSelectedSlot();
			boolean ok = gp.saveManager.save(gp, slot);
			gp.ui.openMessage(ok ? "Saved to Slot " + slot + "!" : "Save failed!");
			gp.gameState = GameState.PLAY;
		}
	}

	private void handleLoadSlotInput() {
		if (slotNavCooldown > 0) slotNavCooldown--;

		if (gp.ui.isConfirmDelete()) {
			if (gp.keyHandler.escPressed) {
				gp.keyHandler.escPressed = false;
				gp.ui.setConfirmDelete(false);
			} else if (gp.keyHandler.enterPressed) {
				gp.keyHandler.enterPressed = false;
				gp.saveManager.deleteSave(gp.ui.getSelectedSlot());
				gp.ui.setConfirmDelete(false);
				gp.ui.refreshSlotInfos();
			}
			return;
		}

		if (gp.keyHandler.delPressed) {
			gp.keyHandler.delPressed = false;
			if (gp.saveManager.hasSave(gp.ui.getSelectedSlot())) {
				gp.ui.setConfirmDelete(true);
			}
			return;
		}

		if (gp.keyHandler.escPressed) {
			gp.keyHandler.escPressed = false;
			if (loadSlotOrigin == GameState.CHARACTER_NAME) {
				gp.gameState = GameState.CHARACTER_NAME;
			} else {
				gp.ui.resetPauseScreen();
				gp.gameState = GameState.PAUSE;
			}
			return;
		}
		if (slotNavCooldown == 0) {
			if (gp.keyHandler.upPressed) {
				gp.ui.navigateSlotUp();
				slotNavCooldown = 12;
			} else if (gp.keyHandler.downPressed) {
				gp.ui.navigateSlotDown();
				slotNavCooldown = 12;
			}
		}
		if (gp.keyHandler.enterPressed) {
			gp.keyHandler.enterPressed = false;
			int slot = gp.ui.getSelectedSlot();
			if (gp.saveManager.hasSave(slot)) {
				gp.saveManager.load(gp, slot);
				gp.ui.resetGame();
				loadingCounter = 0;
				gp.gameState = GameState.LOADING;
			}
		}
	}

	private void handleCharacterNameInput() {
		if (gp.keyHandler.tabPressed) {
			gp.keyHandler.tabPressed = false;
			gp.ui.openLoadSlot();
			slotNavCooldown = 12;
			loadSlotOrigin = GameState.CHARACTER_NAME;
			gp.gameState = GameState.LOAD_SLOT;
			return;
		}
		if (!gp.keyHandler.typedCharacter.isEmpty()) {
			if (gp.player.playerName.length() < gp.ui.getCharacterNameMaxLength()) {
				gp.player.playerName += gp.keyHandler.typedCharacter;
			}
			gp.keyHandler.typedCharacter = ""; // Reset after use
		}

		// Handle backspace
		if (gp.keyHandler.backspacePressed) {
			if (gp.player.playerName.length() > 0) {
				gp.player.playerName = gp.player.playerName.substring(0, gp.player.playerName.length() - 1);
			}
			gp.keyHandler.backspacePressed = false; // Reset to prevent repeating
		}

		// Handle ENTER to go to character creation
		if (gp.keyHandler.enterPressed) {
			gp.keyHandler.enterPressed = false;
			// Set a default name if empty
			if (gp.player.playerName.trim().isEmpty()) {
				gp.player.playerName = "Player";
			}
			gp.ui.resetCharacterCreationScreen();
			gp.gameState = GameState.CHARACTER_CREATION;
		}

		// Handle ESC to go back to title
		if (gp.keyHandler.escPressed) {
			gp.keyHandler.escPressed = false;
			gp.player.playerName = "Player";
			gp.gameState = GameState.TITLE;
		}
	}

	private void handleCharacterCreationInput() {
		if (creationNavCooldown > 0) creationNavCooldown--;

		if (gp.keyHandler.escPressed) {
			gp.keyHandler.escPressed = false;
			gp.gameState = GameState.CHARACTER_NAME;
			return;
		}

		if (creationNavCooldown == 0) {
			if (gp.keyHandler.upPressed) {
				gp.ui.navigateCharacterCreationUp();
				creationNavCooldown = 12;
			} else if (gp.keyHandler.downPressed) {
				gp.ui.navigateCharacterCreationDown();
				creationNavCooldown = 12;
			} else if (gp.keyHandler.leftPressed) {
				gp.ui.navigateCharacterCreationLeft();
				creationNavCooldown = 8;
			} else if (gp.keyHandler.rightPressed) {
				gp.ui.navigateCharacterCreationRight();
				creationNavCooldown = 8;
			}
		}

		if (gp.keyHandler.enterPressed) {
			gp.keyHandler.enterPressed = false;
			gp.ui.applyCharacterCreation();
			gp.player.getPlayerSprites();
			loadingCounter = 0;
			gp.gameState = GameState.LOADING;
		}
	}

	private void handleDebugInput() {
		KeyHandler k = gp.keyHandler;

		if (k.f3Pressed) {
			k.f3Pressed = false;
			gp.player.addItem("key_iron", 3);
			gp.ui.openMessage("[DEBUG] +3 Iron Keys");
		}
		if (k.f4Pressed) {
			k.f4Pressed = false;
			gp.player.addItem("key_gold", 3);
			gp.ui.openMessage("[DEBUG] +3 Gold Keys");
		}
		if (k.f5Pressed) {
			k.f5Pressed = false;
			gp.player.addItem("key_copper", 3);
			gp.ui.openMessage("[DEBUG] +3 Copper Keys");
		}
		if (k.f6Pressed) {
			k.f6Pressed = false;
			int nextId = gp.currentMap.Id + 1;
			try {
				gp.loadMap(GameMaps.fromId(nextId));
				gp.ui.openMessage("[DEBUG] Map → " + gp.currentMap.name);
			} catch (IllegalArgumentException ignored) {
				gp.ui.openMessage("[DEBUG] Letzte Map erreicht");
			}
		}
		if (k.f7Pressed) {
			k.f7Pressed = false;
			int prevId = gp.currentMap.Id - 1;
			try {
				gp.loadMap(GameMaps.fromId(prevId));
				gp.ui.openMessage("[DEBUG] Map → " + gp.currentMap.name);
			} catch (IllegalArgumentException ignored) {
				gp.ui.openMessage("[DEBUG] Erste Map erreicht");
			}
		}
		if (k.f8Pressed) {
			k.f8Pressed = false;
			gp.player.healthHalf = gp.player.maxHealthHalf;
			gp.ui.openMessage("[DEBUG] Volle Gesundheit");
		}
		if (k.f9Pressed) {
			k.f9Pressed = false;
			boolean found = false;
			for (int i = 0; i < gp.obj.size(); i++) {
				if (gp.obj.get(i) instanceof OBJ_QuizStation qs && !qs.completed) {
					qs.completeInstantly(gp, i);
					found = true;
					break;
				}
			}
			if (!found) gp.ui.openMessage("[DEBUG] Keine offene Quiz-Station");
		}
		if (k.f10Pressed) {
			k.f10Pressed = false;
			gp.player.score = 600;
			gp.ui.setMaxPossiblePoints(1000);
			gp.ui.calculateReward();
			gp.ui.gameFinished = true;
			gp.stopMusic();
			gp.playSE(4);
		}
		if (k.f11Pressed) {
			k.f11Pressed = false;
			gp.player.score = 800;
			gp.ui.setMaxPossiblePoints(1000);
			gp.ui.calculateReward();
			gp.ui.gameFinished = true;
			gp.stopMusic();
			gp.playSE(4);
		}
		if (k.f12Pressed) {
			k.f12Pressed = false;
			gp.player.score = 990;
			gp.ui.setMaxPossiblePoints(1000);
			gp.ui.calculateReward();
			gp.ui.gameFinished = true;
			gp.stopMusic();
			gp.playSE(4);
		}
	}

	private void renderScreen() {
		gp.gc.clearRect(0, 0, GeneralSettings.getScreenWidth(), GeneralSettings.getScreenHeight());
		gp.gc.setFill(Color.BLACK);
		gp.gc.fillRect(0, 0, GeneralSettings.getScreenWidth(), GeneralSettings.getScreenHeight());

		if (gp.gameState == GameState.TITLE) {
			gp.ui.drawTitleScreen(gp.gc);
			return;
		}

		if (gp.gameState == GameState.CHARACTER_NAME) {
			gp.ui.drawCharacterNameScreen(gp.gc);
			return;
		}

		if (gp.gameState == GameState.CHARACTER_CREATION) {
			gp.ui.drawCharacterCreationScreen(gp.gc);
			return;
		}
		if (gp.gameState == GameState.LOADING) {
			gp.ui.drawLoadingScreen(gp.gc, loadingCounter);
			return;
		}

		if (gp.gameState == GameState.LOAD_SLOT && loadSlotOrigin == GameState.CHARACTER_NAME) {
			gp.ui.drawCharacterNameScreen(gp.gc);
			gp.ui.drawLoadSlotScreen(gp.gc);
			return;
		}

		gp.renderSystem.render();

		if (gp.gameState == GameState.PAUSE) {
			gp.ui.drawPauseScreen(gp.gc);
		} else if (gp.gameState == GameState.SAVE_SLOT) {
			gp.ui.drawSaveSlotScreen(gp.gc);
		} else if (gp.gameState == GameState.LOAD_SLOT) {
			gp.ui.drawLoadSlotScreen(gp.gc);
		} else {
			gp.ui.draw(gp.gc);
		}
	}
}
