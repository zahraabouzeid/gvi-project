package com.gvi.project.models.objects;

import com.gvi.project.GamePanel;
import com.gvi.project.GameState;
import com.gvi.project.helper.ImageHelper;
import com.gvi.project.models.entities.Player;
import com.gvi.project.models.questions.Question;
import com.gvi.project.models.questions.TopicArea;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class OBJ_QuizStation extends SuperObject {

	private final TopicArea topicArea;
	private List<Question> remainingQuestions;
	public boolean completed = false;

	public OBJ_QuizStation(TopicArea topicArea) {
		this.topicArea = topicArea;
		name = "Crystal";
		interactHint = "[F] " + topicArea.getDisplayName();

		try {
			image = ImageHelper.getImage("/sprites/objects/crystal.png");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public TopicArea getTopicArea() {
		return topicArea;
	}

	public Question getNextQuestion() {
		if (remainingQuestions == null || remainingQuestions.isEmpty()) {
			return null;
		}
		return remainingQuestions.getFirst();
	}

	public void markCurrentCorrect() {
		if (remainingQuestions != null && !remainingQuestions.isEmpty()) {
			remainingQuestions.removeFirst();
		}
		if (remainingQuestions != null && remainingQuestions.isEmpty()) {
			completed = true;
		}
	}

	public int getRemainingCount() {
		return remainingQuestions == null ? 0 : remainingQuestions.size();
	}

	public int getTotalCount() {
		return remainingQuestions == null ? 0 : remainingQuestions.size();
	}

	@Override
	public void onInteract(Player player, GamePanel gp, int objIndex) {
		if (completed) {
			gp.ui.openMessage("Bereich abgeschlossen!");
			return;
		}

		if (remainingQuestions == null) {
			remainingQuestions = new ArrayList<>(gp.questionProvider.getQuestionsByTopic(topicArea));
			Collections.shuffle(remainingQuestions);
		}

		Question question = getNextQuestion();
		if (question != null) {
			gp.ui.openQuiz(question, getRemainingCount());
			gp.interactingObjectIndex = objIndex;
			gp.gameState = GameState.QUIZ;
		} else {
			completed = true;
			gp.ui.openMessage("Bereich abgeschlossen!");
		}
	}

	@Override
	public void onConfirm(Player player, GamePanel gp, int objIndex) {
		markCurrentCorrect();
		gp.playSE(1);

		if (completed) {
			gp.ui.openMessage(topicArea.getDisplayName() + " abgeschlossen!");
			spawnKey(gp, objIndex);
		} else {
			Question next = getNextQuestion();
			if (next != null) {
				gp.ui.openQuiz(next, getRemainingCount());
				gp.interactingObjectIndex = objIndex;
				gp.gameState = GameState.QUIZ;
			}
		}
	}

	private void spawnKey(GamePanel gp, int objIndex) {
		gp.obj[objIndex] = new OBJ_Key();
		gp.obj[objIndex].worldX = this.worldX;
		gp.obj[objIndex].worldY = this.worldY;
	}
}
