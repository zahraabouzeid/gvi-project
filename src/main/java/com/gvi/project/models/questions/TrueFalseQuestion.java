package com.gvi.project.models.questions;

import java.util.List;


public class TrueFalseQuestion extends Question {

    private static final int CORRECT_POINTS = 10;
    private static final int WRONG_POINTS = -5;

    private final boolean correctAnswer;

    public TrueFalseQuestion(int id, TopicArea topicArea, String introText, String questionText,
                             boolean correctAnswer) {
        super(id, topicArea, introText, questionText, QuestionType.TRUE_FALSE);
        this.correctAnswer = correctAnswer;
    }

    public boolean isCorrectAnswer() {
        return correctAnswer;
    }

    @Override
    public int getMaxPoints() {
        return CORRECT_POINTS;
    }

    @Override
    public List<Answer> getAnswers() {
        return List.of(
                new Answer("Wahr", correctAnswer ? CORRECT_POINTS : WRONG_POINTS),
                new Answer("Falsch", correctAnswer ? WRONG_POINTS : CORRECT_POINTS)
        );
    }

    public int evaluate(boolean answeredTrue) {
        return (answeredTrue == correctAnswer) ? CORRECT_POINTS : WRONG_POINTS;
    }
}
