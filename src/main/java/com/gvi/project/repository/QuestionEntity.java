package com.gvi.project.repository;

import jakarta.persistence.*;

/**
 * JPA Entity representing a Question in the database.
 * Maps to the question table with support for different question types (MC, TF, GAP)
 */
@Entity
@Table(name = "question")
public class QuestionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "question_id")
    private Long id;

    @Column(name = "question_set_id", nullable = false)
    private Long questionSetId;

    @Column(name = "question_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private QuestionType questionType;

    @Column(name = "start_text")
    private String startText;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "end_text")
    private String endText;

    @Column(name = "allows_multiple")
    private Boolean allowsMultiple = false;

    @Column(name = "points")
    private Integer points = 1;

    public QuestionEntity() {
    }

    public QuestionEntity(Long questionSetId, QuestionType questionType, String startText) {
        this.questionSetId = questionSetId;
        this.questionType = questionType;
        this.startText = startText;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getQuestionSetId() {
        return questionSetId;
    }

    public void setQuestionSetId(Long questionSetId) {
        this.questionSetId = questionSetId;
    }

    public QuestionType getQuestionType() {
        return questionType;
    }

    public void setQuestionType(QuestionType questionType) {
        this.questionType = questionType;
    }

    public String getStartText() {
        return startText;
    }

    public void setStartText(String startText) {
        this.startText = startText;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getEndText() {
        return endText;
    }

    public void setEndText(String endText) {
        this.endText = endText;
    }

    public Boolean getAllowsMultiple() {
        return allowsMultiple;
    }

    public void setAllowsMultiple(Boolean allowsMultiple) {
        this.allowsMultiple = allowsMultiple;
    }

    public Integer getPoints() {
        return points;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }

    @Override
    public String toString() {
        return "QuestionEntity{" +
                "id=" + id +
                ", questionSetId=" + questionSetId +
                ", questionType=" + questionType +
                ", startText='" + startText + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", endText='" + endText + '\'' +
                ", allowsMultiple=" + allowsMultiple +
                ", points=" + points +
                '}';
    }
}
