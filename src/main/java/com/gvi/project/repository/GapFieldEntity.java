package com.gvi.project.repository;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;

import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "gap_field")
public class GapFieldEntity {

    @Id
    @Column(name = "gap_id")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private QuestionEntity question;

    @Column(name = "gap_index", nullable = false)
    private Integer gapIndex;

    @Column(name = "text_before", columnDefinition = "TEXT")
    private String textBefore;

    @Column(name = "text_after", columnDefinition = "TEXT")
    private String textAfter;

    @OneToMany(mappedBy = "gapField", fetch = FetchType.LAZY)
    @OrderBy("optionOrder ASC")
    private Set<GapOptionEntity> options = new LinkedHashSet<>();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public QuestionEntity getQuestion() {
        return question;
    }

    public void setQuestion(QuestionEntity question) {
        this.question = question;
    }

    public Integer getGapIndex() {
        return gapIndex;
    }

    public void setGapIndex(Integer gapIndex) {
        this.gapIndex = gapIndex;
    }

    public String getTextBefore() {
        return textBefore;
    }

    public void setTextBefore(String textBefore) {
        this.textBefore = textBefore;
    }

    public String getTextAfter() {
        return textAfter;
    }

    public void setTextAfter(String textAfter) {
        this.textAfter = textAfter;
    }

    public Set<GapOptionEntity> getOptions() {
        return options;
    }

    public void setOptions(Set<GapOptionEntity> options) {
        this.options = options;
    }
}
