package com.gvi.project.models.questions;

import java.util.List;


public class MultipleChoiceQuestion extends Question {

    private final List<Answer> answers;
    private final boolean allowMultipleSelection;

    public MultipleChoiceQuestion(int id, TopicArea topicArea, String introText, String questionText,
                                  List<Answer> answers, boolean allowMultipleSelection) {
        super(id, topicArea, introText, questionText, QuestionType.MULTIPLE_CHOICE);
        this.answers = List.copyOf(answers);
        this.allowMultipleSelection = allowMultipleSelection;
    }

    @Override
    public List<Answer> getAnswers() {
        return answers;
    }

    public boolean isAllowMultipleSelection() {
        return allowMultipleSelection;
    }

    @Override
    public int getMaxPoints() {
        if (allowMultipleSelection) {
            return answers.stream().mapToInt(Answer::points).filter(p -> p > 0).sum();
        }
        return answers.stream().mapToInt(Answer::points).max().orElse(0);
    }
}
