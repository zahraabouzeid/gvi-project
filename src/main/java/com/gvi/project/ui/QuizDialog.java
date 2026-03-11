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
        double boxH = 200;
        double boxX = 40;
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

        contentY += 12;

        if (currentQuestion.getIntroText() != null && !currentQuestion.getIntroText().isEmpty()) {
            gc.setFont(FONT_XS);
            gc.setFill(TEXT_GRAY);
            gc.fillText(currentQuestion.getIntroText(), contentX, contentY + 14);
            contentY += 20;
        }

        gc.setFont(FONT_SM);
        gc.setFill(TEXT_WHITE);

        double maxTextW = boxW - 36;

        if (currentQuestion.getType() == QuestionType.FILL_IN_BLANK) {
            FillInBlankQuestion fib = (FillInBlankQuestion) currentQuestion;
            String blankInfo = "Luecke " + (fillBlankIndex + 1) + "/" + fib.getBlanks().size();
            gc.setFont(FONT_XS);
            gc.setFill(TEXT_GRAY);
            gc.fillText(blankInfo, contentX, contentY + 14);
            contentY += 20;

            gc.setFont(FONT_SM);
            gc.setFill(TEXT_WHITE);
            for (String line : wrapText(currentQuestion.getQuestionText(), FONT_SM, maxTextW)) {
                contentY += 18;
                gc.fillText(line, contentX, contentY);
            }

            contentY += 10;
            String blankLine = buildFillBlankLine(fib, fillBlankIndex);
            for (String line : wrapText(blankLine, FONT_SM, maxTextW)) {
                contentY += 18;
                gc.fillText(line, contentX, contentY);
            }
        } else {
            for (String line : wrapText(currentQuestion.getQuestionText(), FONT_SM, maxTextW)) {
                contentY += 18;
                gc.fillText(line, contentX, contentY);
            }
        }

        contentY += 16;

        // 2x2 answer grid
        drawAnswerGrid(gc, contentX, contentY, boxW, boxX, boxY, boxH);
    }

    private void drawAnswerGrid(GraphicsContext gc, double contentX, double contentY,
                                 double boxW, double boxX, double boxY, double boxH) {
        List<Answer> answers = getSelectableAnswers();
        if (answers.isEmpty()) return;

        double answerGap = 8;
        int cols = 2;
        double totalAnswerW = boxW - 36;
        double answerW = (totalAnswerW - answerGap) / cols;
        double answerH = 32;

        for (int i = 0; i < answers.size(); i++) {
            int col = i % cols;
            int row = i / cols;
            double ax = contentX + col * (answerW + answerGap);
            double ay = contentY + row * (answerH + answerGap);

            // Answer background
            if (answerFeedback && selectedAnswer == i + 1) {
                gc.setFill(answers.get(i).points() > 0 ? CORRECT_BG : WRONG_BG);
            } else {
                gc.setFill(ANSWER_BG);
            }
            gc.fillRect(ax, ay, answerW, answerH);

            // Pixel border
            gc.setStroke(BOX_BORDER_INNER);
            gc.setLineWidth(2);
            gc.strokeRect(ax, ay, answerW, answerH);

            // Number badge
            gc.setFont(FONT_XS);
            gc.setFill(TEXT_GOLD);
            gc.fillText((i + 1) + ".", ax + 6, ay + 20);

            // Answer text
            gc.setFont(FONT_XS);
            gc.setFill(TEXT_WHITE);
            String ansText = answers.get(i).text();
            double maxAnsW = answerW - 34;
            if (getTextWidth(ansText, FONT_XS) > maxAnsW) {
                while (ansText.length() > 3 && getTextWidth(ansText + "..", FONT_XS) > maxAnsW) {
                    ansText = ansText.substring(0, ansText.length() - 1);
                }
                ansText += "..";
            }
            gc.fillText(ansText, ax + 26, ay + 20);
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
