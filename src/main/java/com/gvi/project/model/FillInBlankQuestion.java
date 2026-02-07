package com.gvi.project.model;

import java.util.List;

public class FillInBlankQuestion extends Question {

    private final List<Blank> blanks;

    public FillInBlankQuestion(int id, TopicArea topicArea, String introText, String questionText,
                               List<Blank> blanks) {
        super(id, topicArea, introText, questionText, QuestionType.FILL_IN_BLANK);
        this.blanks = List.copyOf(blanks);
    }

    public List<Blank> getBlanks() {
        return blanks;
    }

    @Override
    public int getMaxPoints() {
        return blanks.stream()
                .mapToInt(b -> b.options().stream().mapToInt(Answer::points).max().orElse(0))
                .sum();
    }

    @Override
    public List<Answer> getAnswers() {
        return blanks.stream()
                .flatMap(b -> b.options().stream())
                .toList();
    }

    public record Blank(String textBefore, List<Answer> options, String textAfter) {

        public Blank {
            options = List.copyOf(options);
        }
    }
}
