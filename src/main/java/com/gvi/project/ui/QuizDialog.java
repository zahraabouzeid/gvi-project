package com.gvi.project.ui;

import com.gvi.project.models.questions.Answer;
import com.gvi.project.models.questions.FillInBlankQuestion;
import com.gvi.project.models.questions.Question;
import com.gvi.project.models.questions.QuestionType;
import javafx.scene.canvas.GraphicsContext;

import java.util.List;

import static com.gvi.project.ui.UITheme.*;
import static com.gvi.project.ui.UIUtils.*;

public class QuizDialog {

    private final int screenWidth;
    private final int screenHeight;

    public boolean quizOpen = false;
    public Question currentQuestion = null;
    public int remainingQuestions = 0;
    public int selectedAnswer = -1;
    public boolean answerFeedback = false;
    public boolean answerCorrect = false;
    public int feedbackCounter = 0;
    private int fillBlankIndex = 0;

    public static final int FEEDBACK_DURATION = 15; 

    public QuizDialog(int screenWidth, int screenHeight) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
    }

    public void open(Question question, int remaining) {
        this.currentQuestion = question;
        this.remainingQuestions = remaining;
        this.selectedAnswer = -1;
        this.answerFeedback = false;
        this.answerCorrect = false;
        this.feedbackCounter = 0;
        this.fillBlankIndex = 0;
        this.quizOpen = true;
    }

    public void close() {
        this.quizOpen = false;
        this.currentQuestion = null;
        this.selectedAnswer = -1;
        this.answerFeedback = false;
        this.feedbackCounter = 0;
        this.fillBlankIndex = 0;
    }

    public List<Answer> getSelectableAnswers() {
        if (currentQuestion == null) return List.of();
        if (currentQuestion.getType() == QuestionType.FILL_IN_BLANK) {
            FillInBlankQuestion fib = (FillInBlankQuestion) currentQuestion;
            if (fillBlankIndex < 0 || fillBlankIndex >= fib.getBlanks().size()) return List.of();
            return fib.getBlanks().get(fillBlankIndex).options();
        }
        return currentQuestion.getAnswers();
    }

    public boolean advanceFillBlankIfNeeded() {
        if (currentQuestion == null || currentQuestion.getType() != QuestionType.FILL_IN_BLANK) {
            return false;
        }
        FillInBlankQuestion fib = (FillInBlankQuestion) currentQuestion;
        if (fillBlankIndex < fib.getBlanks().size() - 1) {
            fillBlankIndex++;
            selectedAnswer = -1;
            answerFeedback = false;
            answerCorrect = false;
            feedbackCounter = 0;
            return true;
        }
        return false;
    }

    public int getSelectedPoints() {
        if (currentQuestion == null || selectedAnswer < 1) return 0;
        List<Answer> answers = getSelectableAnswers();
        int idx = selectedAnswer - 1;
        if (idx < answers.size()) {
            return answers.get(idx).points();
        }
        return 0;
    }

    public void draw(GraphicsContext gc) {
        if (currentQuestion == null) return;

        double boxW = screenWidth - 80;
        double boxX = 40;

        // Calculate required height dynamically
        double requiredHeight = calculateRequiredHeight(boxW);
        double minHeight = 200;
        double boxH = Math.max(minHeight, requiredHeight + 10); // Kleiner Sicherheitspuffer
        double boxY = screenHeight - boxH - 20;

        drawPixelBox(gc, boxX, boxY, boxW, boxH);

        double contentX = boxX + 18;
        double contentY = boxY + 26;

        gc.setFont(FONT_XS);
        gc.setFill(TEXT_GOLD);
        String topicText = currentQuestion.getTopicArea().getDisplayName();
        if (remainingQuestions > 0) {
            topicText += "  (" + remainingQuestions + " uebrig)";
        }
        gc.fillText(topicText, contentX, contentY);

        // ESC 
        gc.setFont(FONT_XS);
        gc.setFill(TEXT_GRAY);
        String escHint = "[ESC]";
        double escW = getTextWidth(escHint, FONT_XS);
        gc.fillText(escHint, boxX + boxW - escW - 18, contentY);

        contentY += 20;

        if (currentQuestion.getIntroText() != null && !currentQuestion.getIntroText().isEmpty()) {
            gc.setFont(FONT_XS);
            gc.setFill(TEXT_GRAY);
            contentY += 16;
            gc.fillText(currentQuestion.getIntroText(), contentX, contentY);
            contentY += 10;
        }

        gc.setFont(FONT_SM);
        gc.setFill(TEXT_WHITE);

        double maxTextW = boxW - 36;

        if (currentQuestion.getType() == QuestionType.FILL_IN_BLANK) {
            FillInBlankQuestion fib = (FillInBlankQuestion) currentQuestion;
            String blankInfo = "Luecke " + (fillBlankIndex + 1) + "/" + fib.getBlanks().size();
            gc.setFont(FONT_XS);
            gc.setFill(TEXT_GRAY);
            contentY += 16;
            gc.fillText(blankInfo, contentX, contentY);
            contentY += 10;

            gc.setFont(FONT_SM);
            gc.setFill(TEXT_WHITE);
            for (String line : wrapText(currentQuestion.getQuestionText(), FONT_SM, maxTextW)) {
                contentY += 22;
                gc.fillText(line, contentX, contentY);
            }

            contentY += 15;
            String blankLine = buildFillBlankLine(fib, fillBlankIndex);
            for (String line : wrapText(blankLine, FONT_SM, maxTextW)) {
                contentY += 22;
                gc.fillText(line, contentX, contentY);
            }
        } else {
            for (String line : wrapText(currentQuestion.getQuestionText(), FONT_SM, maxTextW)) {
                contentY += 22;
                gc.fillText(line, contentX, contentY);
            }
        }

        contentY += 25;

        // 2x2 answer grid
        drawAnswerGrid(gc, contentX, contentY, boxW, boxX, boxY, boxH);
    }

    private double calculateRequiredHeight(double boxW) {
        double height = 0;
        double maxTextW = boxW - 36;

        // Initial: boxY + 26 für Topic, dann +20
        height += 26 + 20;

        // Intro text (if present): +16 spacing, dann Text, dann +10
        if (currentQuestion.getIntroText() != null && !currentQuestion.getIntroText().isEmpty()) {
            height += 16 + 10;
        }

        // For FILL_IN_BLANK: blank info line (+16 spacing, dann Text, dann +10)
        if (currentQuestion.getType() == QuestionType.FILL_IN_BLANK) {
            height += 16 + 10;
        }

        // Question text lines (pro Zeile +22)
        int questionLines = wrapText(currentQuestion.getQuestionText(), FONT_SM, maxTextW).size();
        height += Math.max(1, questionLines) * 22;

        // For FILL_IN_BLANK: blank line (+15 spacing, dann pro Zeile +22)
        if (currentQuestion.getType() == QuestionType.FILL_IN_BLANK) {
            FillInBlankQuestion fib = (FillInBlankQuestion) currentQuestion;
            String blankLine = buildFillBlankLine(fib, fillBlankIndex);
            int blankLines = wrapText(blankLine, FONT_SM, maxTextW).size();
            height += 15 + (Math.max(1, blankLines) * 22);
        }

        // Spacing before answers
        height += 25;

        // Answer grid height (dynamisch basierend auf Text-Länge)
        List<Answer> answers = getSelectableAnswers();
        if (!answers.isEmpty()) {
            int cols = 2;
            double answerGap = 8;
            double totalAnswerW = boxW - 36;
            double answerW = (totalAnswerW - answerGap) / cols;
            double maxAnsTextW = answerW - 34;
            
            // Berechne Höhe für jede Reihe
            int rows = (answers.size() + cols - 1) / cols;
            for (int row = 0; row < rows; row++) {
                double maxRowHeight = 32; // Minimum
                for (int col = 0; col < cols; col++) {
                    int idx = row * cols + col;
                    if (idx < answers.size()) {
                        List<String> lines = wrapText(answers.get(idx).text(), FONT_XS, maxAnsTextW);
                        int lineCount = Math.max(1, lines.size());
                        double answerHeight = Math.max(32, 10 + lineCount * 14 + 8);
                        maxRowHeight = Math.max(maxRowHeight, answerHeight);
                    }
                }
                height += maxRowHeight;
                if (row < rows - 1) {
                    height += answerGap;
                }
            }
        }

        // Bottom padding
        height += 15;

        return height;
    }

    private void drawAnswerGrid(GraphicsContext gc, double contentX, double contentY,
                                 double boxW, double boxX, double boxY, double boxH) {
        List<Answer> answers = getSelectableAnswers();
        if (answers.isEmpty()) return;

        double answerGap = 8;
        int cols = 2;
        double totalAnswerW = boxW - 36;
        double answerW = (totalAnswerW - answerGap) / cols;
        double maxAnsTextW = answerW - 34;
        
        // Berechne Höhen für jede Antwort (mehrzeilig)
        double[] answerHeights = new double[answers.size()];
        for (int i = 0; i < answers.size(); i++) {
            List<String> lines = wrapText(answers.get(i).text(), FONT_XS, maxAnsTextW);
            int lineCount = Math.max(1, lines.size());
            answerHeights[i] = Math.max(32, 10 + lineCount * 14 + 8); // Min 32px, 10px top, 14px per line, 8px bottom
        }

        // Zeichne Antworten mit dynamischer Höhe
        double currentY = contentY;
        for (int i = 0; i < answers.size(); i++) {
            int col = i % cols;
            int row = i / cols;
            
            // Hole maximale Höhe in dieser Reihe
            double rowHeight = answerHeights[row * cols];
            if (row * cols + 1 < answers.size()) {
                rowHeight = Math.max(rowHeight, answerHeights[row * cols + 1]);
            }
            
            double ax = contentX + col * (answerW + answerGap);
            double ay = currentY;
            
            // Wenn neue Reihe, update currentY
            if (col == 0 && i > 0) {
                ay = currentY;
            }

            // Answer background
            if (answerFeedback && selectedAnswer == i + 1) {
                gc.setFill(answers.get(i).points() > 0 ? CORRECT_BG : WRONG_BG);
            } else {
                gc.setFill(ANSWER_BG);
            }
            gc.fillRect(ax, ay, answerW, rowHeight);

            // Pixel border
            gc.setStroke(BOX_BORDER_INNER);
            gc.setLineWidth(2);
            gc.strokeRect(ax, ay, answerW, rowHeight);

            // Number badge
            gc.setFont(FONT_XS);
            gc.setFill(TEXT_GOLD);
            gc.fillText((i + 1) + ".", ax + 6, ay + 18);

            // Answer text (mehrzeilig)
            gc.setFont(FONT_XS);
            gc.setFill(TEXT_WHITE);
            List<String> lines = wrapText(answers.get(i).text(), FONT_XS, maxAnsTextW);
            double textY = ay + 18;
            for (String line : lines) {
                if (!line.isEmpty()) {
                    gc.fillText(line, ax + 26, textY);
                }
                textY += 14;
            }
            
            // Nach der letzten Spalte einer Reihe: update currentY
            if (col == cols - 1 || i == answers.size() - 1) {
                currentY += rowHeight + answerGap;
            }
        }

        // Feedback
        if (answerFeedback) {
            feedbackCounter++;
        }
    }

    private String buildFillBlankLine(FillInBlankQuestion fib, int blankIndex) {
        if (fib.getBlanks().isEmpty()) return "";
        if (blankIndex < 0 || blankIndex >= fib.getBlanks().size()) blankIndex = 0;
        FillInBlankQuestion.Blank blank = fib.getBlanks().get(blankIndex);
        return blank.textBefore() + " ____ " + blank.textAfter();
    }
}
