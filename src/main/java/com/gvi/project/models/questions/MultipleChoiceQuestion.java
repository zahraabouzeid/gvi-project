package com.gvi.project.models.questions;

import java.util.List;


public class MultipleChoiceQuestion extends Question {

    private final List<Answer> answers;
    private final boolean allowMultipleSelection;
    private final int numberOfCorrectOptions;

    public MultipleChoiceQuestion(int id, TopicArea topicArea, String introText, String questionText,
                                  List<Answer> answers, boolean allowMultipleSelection, Difficulty difficulty) {
        super(id, topicArea, introText, questionText, QuestionType.MULTIPLE_CHOICE, difficulty);
        this.answers = List.copyOf(answers);
        this.allowMultipleSelection = allowMultipleSelection;
        // Count how many answers are marked as correct (points > 0)
        this.numberOfCorrectOptions = (int) answers.stream().filter(a -> a.points() > 0).count();
    }

    @Override
    public List<Answer> getAnswers() {
        return answers;
    }

    public boolean isAllowMultipleSelection() {
        return allowMultipleSelection;
    }

    public int getNumberOfCorrectOptions() {
        return numberOfCorrectOptions;
    }

    @Override
    public int getMaxPoints() {
        return ScoreCalculator.calculateMultipleChoiceMaxPoints(getDifficulty(), numberOfCorrectOptions);
    }
}
