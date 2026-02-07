package com.gvi.project.model;

import java.util.List;

public abstract class Question {

    private final int id;
    private final TopicArea topicArea;
    private final String introText;
    private final String questionText;
    private final QuestionType type;

    protected Question(int id, TopicArea topicArea, String introText, String questionText, QuestionType type) {
        this.id = id;
        this.topicArea = topicArea;
        this.introText = introText;
        this.questionText = questionText;
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public TopicArea getTopicArea() {
        return topicArea;
    }

    public String getIntroText() {
        return introText;
    }

    public String getQuestionText() {
        return questionText;
    }

    public QuestionType getType() {
        return type;
    }

    public abstract int getMaxPoints();

    public abstract List<Answer> getAnswers();
}
