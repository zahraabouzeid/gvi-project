package com.gvi.project.repository;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "gap_option")
public class GapOptionEntity {

    @Id
    @Column(name = "gap_option_id")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gap_id", nullable = false)
    private GapFieldEntity gapField;

    @Column(name = "option_text", nullable = false, columnDefinition = "TEXT")
    private String optionText;

    @Column(name = "is_correct", nullable = false)
    private Boolean correct;

    @Column(name = "option_order", nullable = false)
    private Integer optionOrder;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public GapFieldEntity getGapField() {
        return gapField;
    }

    public void setGapField(GapFieldEntity gapField) {
        this.gapField = gapField;
    }

    public String getOptionText() {
        return optionText;
    }

    public void setOptionText(String optionText) {
        this.optionText = optionText;
    }

    public boolean isCorrect() {
        return Boolean.TRUE.equals(correct);
    }

    public void setCorrect(boolean correct) {
        this.correct = correct;
    }

    public Integer getOptionOrder() {
        return optionOrder;
    }

    public void setOptionOrder(Integer optionOrder) {
        this.optionOrder = optionOrder;
    }
}
