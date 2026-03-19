package com.gvi.project.repository;

import jakarta.persistence.*;

import java.util.LinkedHashSet;
import java.util.Set;

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
    private Integer id;

    @Column(name = "question_set_id", nullable = false)
    private Integer questionSetId;

    @Column(name = "question_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private QuestionType questionType;

    @Column(name = "start_text", columnDefinition = "TEXT")
    private String startText;

    @Column(name = "image_url", columnDefinition = "TEXT")
    private String imageUrl;

    @Column(name = "end_text", columnDefinition = "TEXT")
    private String endText;

    @Column(name = "allows_multiple")
    private Boolean allowsMultiple = false;

    @Column(name = "points")
    private Integer points = 1;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "`Question_Theme`",
            joinColumns = @JoinColumn(name = "question_id"),
            inverseJoinColumns = @JoinColumn(name = "theme_id")
    )
    private Set<ThemeEntity> themes = new LinkedHashSet<>();

    @OneToMany(mappedBy = "question", fetch = FetchType.LAZY)
    @OrderBy("optionOrder ASC")
    private Set<McAnswerEntity> mcAnswers = new LinkedHashSet<>();

    @OneToMany(mappedBy = "question", fetch = FetchType.LAZY)
    @OrderBy("gapIndex ASC")
    private Set<GapFieldEntity> gapFields = new LinkedHashSet<>();

    public QuestionEntity() {
    }

    public QuestionEntity(Integer questionSetId, QuestionType questionType, String startText) {
        this.questionSetId = questionSetId;
        this.questionType = questionType;
        this.startText = startText;
    }

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getQuestionSetId() {
        return questionSetId;
    }

    public void setQuestionSetId(Integer questionSetId) {
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

    public Set<ThemeEntity> getThemes() {
        return themes;
    }

    public void setThemes(Set<ThemeEntity> themes) {
        this.themes = themes;
    }

    public Set<McAnswerEntity> getMcAnswers() {
        return mcAnswers;
    }

    public void setMcAnswers(Set<McAnswerEntity> mcAnswers) {
        this.mcAnswers = mcAnswers;
    }

    public Set<GapFieldEntity> getGapFields() {
        return gapFields;
    }

    public void setGapFields(Set<GapFieldEntity> gapFields) {
        this.gapFields = gapFields;
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
                ", allowsMultiple=" + getAllowsMultiple() +
                ", points=" + points +
                '}';
    }
}
