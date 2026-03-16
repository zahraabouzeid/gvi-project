package com.gvi.project;

import com.gvi.project.models.objects.SuperObject;
import com.gvi.project.models.questions.Answer;
import com.gvi.project.systems.AnimationSystem;
import com.gvi.project.ui.LoadingScreen;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.scene.paint.Color;

import java.util.List;

public class GameLoop extends AnimationTimer {

	private final GamePanel gp;

	double delta = 0;
	long lastTime = System.nanoTime();
	int drawCount = 0;
	long timer = 0;
	private int pauseNavCooldown = 0;
	private int slotNavCooldown  = 0;
	private int loadingCounter   = 0;
	private GameState loadSlotOrigin = GameState.PAUSE;

	public GameLoop(GamePanel gp) {
		this.gp = gp;
	}

	@Override
	public void handle(long now) {

		double deltaSeconds = (now - lastTime) / 1_000_000_000.0;

		delta += (now - lastTime) / gp.generalSettings.drawInterval;
		timer += (now - lastTime);
		lastTime = now;

		while (delta >= 1) {
			update(deltaSeconds);
			delta--;
			drawCount++;
		}

		renderScreen();

		if (timer >= 1_000_000_000) {
			drawCount = 0;
			timer = 0;
		}
	}

	private void update(double deltaSeconds) {
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
		if (gp.gameState == GameState.LOADING) {
			loadingCounter++;
			if (loadingCounter >= LoadingScreen.DURATION) {
				gp.gameState = GameState.PLAY;
			}
			return;
		}
		if (gp.player.isDead) {
			if (gp.keyHandler.enterPressed) {
				gp.keyHandler.enterPressed = false;
				gp.player.setDefaultValues();
				gp.ui.resetGame();
				gp.interactingObjectIndex = -1;
				gp.gameState = GameState.PLAY;
			}
			return;
		}
		if (gp.gameState == GameState.PLAY) {
			gp.generalSettings.isDevMode = gp.keyHandler.f2Pressed;

			if (gp.keyHandler.escPressed) {
				gp.keyHandler.escPressed = false;
				gp.ui.resetPauseScreen();
				gp.gameState = GameState.PAUSE;
				return;
			}
			gp.animationSystem.tick(deltaSeconds);
			gp.player.update();
		} else if (gp.gameState == GameState.QUIZ) {
			if (gp.ui.isAnswerFeedback()) {
				if (gp.ui.getFeedbackCounter() >= UI.FEEDBACK_DURATION) {
					boolean correct = gp.ui.isAnswerCorrect();

					if (correct) {
						if (gp.ui.advanceFillBlankIfNeeded()) {
							return;
						}
						gp.player.score += 10;
						gp.ui.showFloatingScore(10);
						gp.ui.closeQuiz();
						int idx = gp.interactingObjectIndex;
						if (idx != -1 && gp.obj.get(idx) != null) {
							gp.obj.get(idx).onConfirm(gp.player, gp, idx);
						if (!gp.ui.isQuizOpen()) {
								gp.interactingObjectIndex = -1;
								gp.gameState = GameState.PLAY;
							}
						}
					} else {
						gp.player.score = Math.max(0, gp.player.score - 10);
						gp.ui.showFloatingScore(-10);
						gp.playSE(5);
						gp.player.takeHalfHeartDamage();
						if (gp.player.isDead) {
							gp.playSE(6);
							gp.ui.closeQuiz();
							gp.interactingObjectIndex = -1;
							gp.gameState = GameState.PLAY;
							return;
						}
						gp.ui.setSelectedAnswer(-1);
						gp.ui.setAnswerFeedback(false);
						gp.ui.setAnswerCorrect(false);
						gp.ui.setFeedbackCounter(0);
					}
				}
			} else {
				int num = gp.keyHandler.numberPressed;
				if (num >= 1 && gp.ui.getCurrentQuestion() != null) {
					List<Answer> answers = gp.ui.getSelectableAnswers();
					if (num <= answers.size()) {
						gp.keyHandler.numberPressed = -1;
						gp.ui.setSelectedAnswer(num);
						gp.ui.setAnswerCorrect(answers.get(num - 1).points() > 0);
						gp.ui.setAnswerFeedback(true);
					}
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

		// Handle ENTER to start game
		if (gp.keyHandler.enterPressed) {
			gp.keyHandler.enterPressed = false;
			// Set a default name if empty
			if (gp.player.playerName.trim().isEmpty()) {
				gp.player.playerName = "Player";
			}
			gp.gameState = GameState.PLAY;
		}

		// Handle ESC to go back to title
		if (gp.keyHandler.escPressed) {
			gp.keyHandler.escPressed = false;
			gp.player.playerName = "Player";
			gp.gameState = GameState.TITLE;
		}
	}

	private void renderScreen() {
		gp.gc.clearRect(0, 0, gp.generalSettings.screenWidth, gp.generalSettings.screenHeight);
		gp.gc.setFill(Color.BLACK);
		gp.gc.fillRect(0, 0, gp.generalSettings.screenWidth, gp.generalSettings.screenHeight);

		if (gp.gameState == GameState.TITLE) {
			gp.ui.drawTitleScreen(gp.gc);
			return;
		}

		if (gp.gameState == GameState.CHARACTER_NAME) {
			gp.ui.drawCharacterNameScreen(gp.gc);
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
