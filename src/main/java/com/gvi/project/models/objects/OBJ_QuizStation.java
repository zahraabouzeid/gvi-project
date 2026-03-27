package com.gvi.project.models.objects;

import com.gvi.project.components.AnimationComponent;
import com.gvi.project.GamePanel;
import com.gvi.project.GameState;
import com.gvi.project.models.entities.Player;
import com.gvi.project.models.questions.Question;
import com.gvi.project.models.questions.QuestionService;
import com.gvi.project.models.questions.TopicArea;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class OBJ_QuizStation extends AnimatedObject {
	private static final Logger log = LoggerFactory.getLogger(OBJ_QuizStation.class);

	private final TopicArea topicArea;
	private List<Question> remainingQuestions;
	private final List<Integer> answeredQuestionIds = new ArrayList<>();
	public boolean completed = false;

	public OBJ_QuizStation(TopicArea topicArea, String spriteGroupId) {
		super("/sprites/tilemaps/damp-dungeons/Animations/Dungeon_ObjectsDungeon",spriteGroupId);
		this.topicArea = topicArea;
		name = "Quiz station";
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
			answeredQuestionIds.add(remainingQuestions.getFirst().getId());
			remainingQuestions.removeFirst();
		}
		if (remainingQuestions != null && remainingQuestions.isEmpty()) {
			completed = true;
		}
	}

	public List<Integer> getAnsweredQuestionIds() {
		return Collections.unmodifiableList(answeredQuestionIds);
	}

	public void restoreProgress(List<Integer> answered, QuestionService provider) {
		Set<Integer> answeredSet = new HashSet<>(answered);
		this.answeredQuestionIds.clear();
		this.answeredQuestionIds.addAll(answered);
		this.remainingQuestions = new ArrayList<>(provider.getQuestionsByTopic(topicArea));
		this.remainingQuestions.removeIf(q -> answeredSet.contains(q.getId()));
		if (this.remainingQuestions.isEmpty()) {
			completed = true;
		}
	}

	public void completeInstantly(GamePanel gp, int objIndex) {
		if (completed) return;
		if (remainingQuestions == null) {
			remainingQuestions = new ArrayList<>(gp.questionProvider.getQuestionsByTopic(topicArea));
		}
		while (!remainingQuestions.isEmpty()) {
			answeredQuestionIds.add(remainingQuestions.getFirst().getId());
			remainingQuestions.removeFirst();
		}
		completed = true;
		spawnKey(gp, objIndex);
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
			List<Question> questionsByTopic = gp.questionProvider.getQuestionsByTopic(topicArea);
			if (questionsByTopic == null || questionsByTopic.isEmpty()) {
				log.warn("No questions available for topic area {}.", topicArea);
			}
			if (questionsByTopic != null && questionsByTopic.size() < gp.maxQuestionsPerQuizStation) {
				log.warn("Configured max questions ({}) exceeds available questions ({}) for topic area {}. Using all available questions.",
						gp.maxQuestionsPerQuizStation, questionsByTopic.size(), topicArea);
			}
			remainingQuestions = selectQuestionsForStation(questionsByTopic, gp.maxQuestionsPerQuizStation);
		}

		Question question = getNextQuestion();
		if (question != null) {
			// Öffne Quiz-Dialog mit der nächsten Frage
			// maxPossiblePoints wird automatisch beim Spielstart auf die Gesamtzahl aller Fragen gesetzt (563)
			gp.ui.openQuiz(question, getRemainingCount());
			gp.interactingObjectIndex = objIndex;
			gp.gameState = GameState.QUIZ;
		} else {
			completed = true;
			gp.ui.openMessage("Bereich abgeschlossen!");
		}
	}

	static List<Question> selectQuestionsForStation(List<Question> shuffledQuestions, int maxQuestionsPerQuizStation) {
		if (shuffledQuestions == null || shuffledQuestions.isEmpty()) {
			return new ArrayList<>();
		}

		if (maxQuestionsPerQuizStation <= 0) {
			return new ArrayList<>(shuffledQuestions);
		}

		int selectedCount = Math.min(maxQuestionsPerQuizStation, shuffledQuestions.size());
		return new ArrayList<>(shuffledQuestions.subList(0, selectedCount));
	}

	@Override
	public void onConfirm(GamePanel gp, int objIndex) {
		markCurrentCorrect();


		gp.playSE(1);

		if (completed) {
			gp.ui.openMessage(topicArea.getDisplayName() + " abgeschlossen!");
			spawnKey(gp, objIndex);
		} else {
			Question next = getNextQuestion();
			if (next != null) {
				// Track max possible points for reward calculation
				gp.ui.addMaxPossiblePoints(next.getMaxPoints());
				gp.ui.openQuiz(next, getRemainingCount());
				gp.interactingObjectIndex = objIndex;
				gp.gameState = GameState.QUIZ;
			}
		}
	}

	@Override
	public void setUpAnimationComponent(){
		AnimationComponent animComp = (AnimationComponent) this.components.get("Animation");
		animComp.triggerLoop();
		animComp.cycleLength = 1.5;
		animComp.setCycleOrder(List.of(0,1,2,2,1,0));
		animComp.delayBetweenCycles = 0.3;
		animComp.setStartOffset(Math.random() * animComp.cycleLength);

		sprite = animComp.getCurrentSprite();

	}

    private void spawnKey(GamePanel gp, int objIndex) {
		String crystalColor = id.substring(0, id.indexOf("_quiz"));

		OBJ_Key key = switch (crystalColor) {
			case "crystal_blue" -> new OBJ_Key(KeyType.IRON);
			case "crystal_green" -> new OBJ_Key(KeyType.GOLD);
			default -> new OBJ_Key(KeyType.COPPER);
		};

		gp.obj.remove(objIndex);
		gp.obj.add(objIndex, key);
		gp.obj.get(objIndex).worldX = this.worldX;
		gp.obj.get(objIndex).worldY = this.worldY;
	}
}
