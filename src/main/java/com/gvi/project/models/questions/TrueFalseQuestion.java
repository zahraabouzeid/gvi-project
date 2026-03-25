package com.gvi.project.models.questions;

import java.util.List;


public class TrueFalseQuestion extends Question {

    private final boolean correctAnswer;

    public TrueFalseQuestion(int id, TopicArea topicArea, String introText, String questionText,
                             boolean correctAnswer, Difficulty difficulty) {
        super(id, topicArea, introText, questionText, QuestionType.TRUE_FALSE, difficulty);
        this.correctAnswer = correctAnswer;
    }

    public boolean isCorrectAnswer() {
        return correctAnswer;
    }

    @Override
    public int getMaxPoints() {
        return ScoreCalculator.calculateTrueFalsePoints(getDifficulty(), true);
    }

    @Override
    public List<Answer> getAnswers() {
        int correctPoints = ScoreCalculator.calculateTrueFalsePoints(getDifficulty(), true);
        int wrongPoints = ScoreCalculator.calculateTrueFalsePoints(getDifficulty(), false);
        
        return List.of(
                new Answer("Wahr", correctAnswer ? correctPoints : wrongPoints),
                new Answer("Falsch", correctAnswer ? wrongPoints : correctPoints)
        );
    }

    public int evaluate(boolean answeredTrue) {
        return ScoreCalculator.calculateTrueFalsePoints(getDifficulty(), answeredTrue == correctAnswer);
    }
}
