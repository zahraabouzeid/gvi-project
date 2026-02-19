package com.gvi.project;

import com.gvi.project.models.objects.SuperObject;
import com.gvi.project.models.questions.Answer;
import javafx.animation.AnimationTimer;
import javafx.scene.paint.Color;

import java.util.List;

public class GameLoop extends AnimationTimer {

	private final GamePanel gp;

	double delta = 0;
	long lastTime = System.nanoTime();
	int drawCount = 0;
	long timer = 0;

	public GameLoop(GamePanel gp) {
		this.gp = gp;
	}

	@Override
	public void handle(long now) {

		long currentTime = System.nanoTime();

		delta += (currentTime - lastTime) / gp.drawInterval;
		timer += (currentTime - lastTime);
		lastTime = currentTime;

		if (delta >= 1) {
			update();
			renderScreen();
			delta--;
			drawCount++;
		}

		if (timer >= 1_000_000_000) {
			System.out.println("FPS: " + drawCount);
			drawCount = 0;
			timer = 0;
		}
	}

	private void update() {
		if (gp.gameState == GameState.TITLE) {
			if (gp.keyHandler.enterPressed) {
				gp.keyHandler.enterPressed = false;
				gp.gameState = GameState.PLAY;
			}
			return;
		}
		if (gp.player.isDead) {
			if (gp.keyHandler.enterPressed) {
				gp.keyHandler.enterPressed = false;
				gp.player.setDefaultValues();
				gp.ui.resetGame();
				gp.assetSetter.setObject();
				gp.interactingObjectIndex = -1;
				gp.gameState = GameState.PLAY;
			}
			return;
		}
		if (gp.gameState == GameState.PLAY) {
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
						if (idx != -1 && gp.obj[idx] != null) {
							gp.obj[idx].onConfirm(gp.player, gp, idx);
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
		}
	}

	private void renderScreen() {
		gp.gc.clearRect(0, 0, gp.screenWidth, gp.screenHeight);
		gp.gc.setFill(Color.BLACK);
		gp.gc.fillRect(0, 0, gp.screenWidth, gp.screenHeight);

		if (gp.gameState == GameState.TITLE) {
			gp.ui.drawTitleScreen(gp.gc);
			return;
		}

		gp.tileManager.draw(gp.gc);

		for (SuperObject obj : gp.obj) {
			if(obj != null) {
				obj.draw(gp);
			}
		}

		gp.player.draw(gp.gc);
		gp.ui.minimap.draw(gp.gc);
		gp.ui.draw(gp.gc);
	}
}
