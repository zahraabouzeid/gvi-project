package com.gvi.project.models.objects;

import com.gvi.project.Components.AnimationComponent;
import com.gvi.project.GamePanel;
import com.gvi.project.GameState;
import com.gvi.project.models.entities.Player;
import com.gvi.project.models.questions.Question;
import com.gvi.project.models.questions.TopicArea;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class OBJ_QuizStation extends AnimatedObject {

	private final TopicArea topicArea;
	private List<Question> remainingQuestions;
	public boolean completed = false;

	public OBJ_QuizStation(TopicArea topicArea, String spriteGroupId) {
		super("/sprites/tilemaps/damp-dungeons/Animations/Dungeon_ObjectsDungeon",spriteGroupId);
		this.topicArea = topicArea;
		name = spriteGroupId;
		interactHint = "[F] " + topicArea.getDisplayName();
		collision = true;
		spriteDirectionUp = true;
		canInteract = true;

		setUpAnimationComponent();
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

	@Override
	public void setUpAnimationComponent(){
		AnimationComponent animComp = (AnimationComponent) this.components.get("Animation");
		animComp.isLooping();
		animComp.cycleLength = 1.5;
		animComp.setCycleOrder(List.of(0,1,2,2,1));
		animComp.delayBetweenCycles = 0.3;
		animComp.setStartOffset(Math.random() * animComp.cycleLength);

		sprite = animComp.getCurrentSprite();
	};

	private void spawnKey(GamePanel gp, int objIndex) {
		OBJ_Key key = switch (this.name) {
			case "crystal_blue" -> new OBJ_Key("key_iron");
			case "crystal_green" -> new OBJ_Key("key_gold");
			default -> new OBJ_Key("key_copper");
		};

		gp.obj.remove(objIndex);
		gp.obj.add(objIndex, key);
		gp.obj.get(objIndex).worldX = this.worldX;
		gp.obj.get(objIndex).worldY = this.worldY;
	}
}
