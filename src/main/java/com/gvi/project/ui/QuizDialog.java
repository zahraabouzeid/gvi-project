package com.gvi.project.ui;

import com.gvi.project.GeneralSettings;
import com.gvi.project.models.questions.Answer;
import com.gvi.project.models.questions.FillInBlankQuestion;
import com.gvi.project.models.questions.MultipleChoiceQuestion;
import com.gvi.project.models.questions.Question;
import com.gvi.project.models.questions.QuestionType;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.text.Font;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static com.gvi.project.ui.UITheme.*;
import static com.gvi.project.ui.UIUtils.*;

public class QuizDialog extends GameScreen {

    private static final int ANSWER_COLS = 2;
    private static final double ANSWER_GAP = 8;
    private static final double ANSWER_MIN_HEIGHT = 32;
    private static final double ANSWER_TEXT_LINE_HEIGHT = 14;
    private static final int MAX_NUMBER_SHORTCUT = 9;

    public boolean quizOpen = false;
    public Question currentQuestion = null;
    public int remainingQuestions = 0;
    public int selectedAnswer = -1;
    public boolean answerFeedback = false;
    public boolean answerCorrect = false;
    public int feedbackCounter = 0;
    private int fillBlankIndex = 0;
    private List<Answer> selectableAnswers = List.of();
    private final Set<Integer> selectedAnswers = new LinkedHashSet<>();
    private int resolvedPoints = 0;

    public static final int FEEDBACK_DURATION = 15; 

    public QuizDialog() {}

    public void open(Question question, int remaining) {
        this.currentQuestion = question;
        this.remainingQuestions = remaining;
        this.selectedAnswer = -1;
        this.answerFeedback = false;
        this.answerCorrect = false;
        this.feedbackCounter = 0;
        this.fillBlankIndex = 0;
        this.selectableAnswers = shuffleAnswers(resolveAnswersForCurrentStep());
        this.selectedAnswers.clear();
        this.resolvedPoints = 0;
        this.quizOpen = true;
    }

    public void close() {
        this.quizOpen = false;
        this.currentQuestion = null;
        this.selectedAnswer = -1;
        this.answerFeedback = false;
        this.feedbackCounter = 0;
        this.fillBlankIndex = 0;
        this.selectableAnswers = List.of();
        this.selectedAnswers.clear();
        this.resolvedPoints = 0;
    }

    public List<Answer> getSelectableAnswers() {
        return selectableAnswers;
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
            selectableAnswers = shuffleAnswers(resolveAnswersForCurrentStep());
            selectedAnswers.clear();
            resolvedPoints = 0;
            return true;
        }
        return false;
    }

    public boolean isMultiSelectQuestion() {
        if (currentQuestion == null || currentQuestion.getType() != QuestionType.MULTIPLE_CHOICE) {
            return false;
        }
        if (!(currentQuestion instanceof MultipleChoiceQuestion mc)) {
            return false;
        }
        return mc.isAllowMultipleSelection();
    }

    public boolean isMultipleChoiceQuestion() {
        return currentQuestion != null && currentQuestion.getType() == QuestionType.MULTIPLE_CHOICE;
    }

    public boolean handleNumberInput(int number) {
        List<Answer> answers = getSelectableAnswers();
        int inputLimit = Math.min(MAX_NUMBER_SHORTCUT, answers.size());
        if (number < 1 || number > inputLimit || answerFeedback) {
            return false;
        }

        if (isMultiSelectQuestion()) {
            if (selectedAnswers.contains(number)) {
                selectedAnswers.remove(number);
            } else {
                selectedAnswers.add(number);
            }
            return true;
        }

        // Radio-button style single selection (MC single, TRUE_FALSE, GAP)
        selectedAnswer = number;
        selectedAnswers.clear();
        selectedAnswers.add(number);
        return true;
    }

    public boolean submitSelectionIfNeeded() {
        if (currentQuestion == null || answerFeedback) {
            return false;
        }
        if (selectedAnswers.isEmpty()) {
            return false;
        }

        List<Answer> answers = getSelectableAnswers();
        if (isMultiSelectQuestion()) {
            Set<Integer> correctIndices = new LinkedHashSet<>();
            for (int i = 0; i < answers.size(); i++) {
                if (answers.get(i).points() > 0) {
                    correctIndices.add(i + 1);
                }
            }

            answerCorrect = selectedAnswers.equals(correctIndices);
            resolvedPoints = answerCorrect ? sumPoints(correctIndices, answers) : calculateWrongPointsForMulti(answers);
            selectedAnswer = -1;
        } else {
            int selectedIndex = selectedAnswers.iterator().next() - 1;
            if (selectedIndex < 0 || selectedIndex >= answers.size()) {
                return false;
            }
            selectedAnswer = selectedIndex + 1;
            resolvedPoints = answers.get(selectedIndex).points();
            answerCorrect = resolvedPoints > 0;
        }

        answerFeedback = true;
        feedbackCounter = 0;
        return true;
    }

    public int getResolvedPoints() {
        return resolvedPoints;
    }

    public void resetAfterWrongAnswer() {
        selectedAnswer = -1;
        answerFeedback = false;
        answerCorrect = false;
        feedbackCounter = 0;
        selectedAnswers.clear();
        resolvedPoints = 0;
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
        double questionLineHeight = 18;
        double maxTextW = boxW - 36;

        // Calculate required box height dynamically
        double contentHeight = 26; // Initial top padding
        contentHeight += 12; // Topic line + spacing

        String introText = normalizeQuestionWhitespace(currentQuestion.getIntroText());
        if (introText.equals(resolveTopicLabel())) {
            introText = "";
        }
        if (!introText.isEmpty()) {
            List<String> introLines = wrapMultilineText(introText, FONT_XS, maxTextW);
            contentHeight += introLines.size() * 14 + 6;
        }

        List<String> questionLines = wrapMultilineText(currentQuestion.getQuestionText(), FONT_SM, maxTextW);

        if (currentQuestion.getType() == QuestionType.FILL_IN_BLANK) {
            FillInBlankQuestion fib = (FillInBlankQuestion) currentQuestion;
            contentHeight += 20; // Blank info
            contentHeight += questionLines.size() * questionLineHeight;
            
            String blankLine = buildFillBlankLine(fib, fillBlankIndex);
            List<String> blankLines = wrapText(blankLine, FONT_SM, maxTextW);
            contentHeight += 10 + blankLines.size() * questionLineHeight;
        } else {
            contentHeight += questionLines.size() * questionLineHeight;
        }

        contentHeight += 16; // Spacing before answers

        // Calculate answer grid height
        List<Answer> answers = getSelectableAnswers();
        double answerGridHeight = calculateAnswerGridHeight(boxW, answers, Integer.MAX_VALUE);
        contentHeight += answerGridHeight;

        contentHeight += 30; // Bottom padding for hint (increased from 20)
        
        double boxH = Math.max(240, contentHeight); // Minimum 240, but grow as needed
        double boxY = screenHeight - boxH - 50; // 50px padding from bottom

        drawPixelBox(gc, boxX, boxY, boxW, boxH);

        double contentX = boxX + 18;
        double contentY = boxY + 26;

        gc.setFont(FONT_XS);
        gc.setFill(TEXT_GOLD);
        String topicText = resolveTopicLabel();
        if (remainingQuestions > 0) {
            topicText += "  (" + remainingQuestions + " uebrig)";
        }
        gc.fillText(topicText, contentX, contentY);

        // ESC 
        gc.setFont(FONT_XS);
        gc.setFill(TEXT_GRAY);
        String escHint = "[ENTER] bestaetigen  [ESC]";
        double escW = getTextWidth(escHint, FONT_XS);
        gc.fillText(escHint, boxX + boxW - escW - 18, contentY);

        contentY += 12;

        if (!introText.isEmpty()) {
            gc.setFont(FONT_XS);
            gc.setFill(TEXT_GRAY);
            for (String line : wrapMultilineText(introText, FONT_XS, maxTextW)) {
                contentY += 14;
                gc.fillText(line, contentX, contentY);
            }
            contentY += 6;
        }

        gc.setFont(FONT_SM);
        gc.setFill(TEXT_WHITE);

        if (currentQuestion.getType() == QuestionType.FILL_IN_BLANK) {
            FillInBlankQuestion fib = (FillInBlankQuestion) currentQuestion;
            String blankInfo = "Luecke " + (fillBlankIndex + 1) + "/" + fib.getBlanks().size();
            gc.setFont(FONT_XS);
            gc.setFill(TEXT_GRAY);
            gc.fillText(blankInfo, contentX, contentY + 14);
            contentY += 20;

            gc.setFont(FONT_SM);
            gc.setFill(TEXT_WHITE);
            for (String line : questionLines) {
                contentY += questionLineHeight;
                gc.fillText(line, contentX, contentY);
            }

            contentY += 10;
            String blankLine = buildFillBlankLine(fib, fillBlankIndex);
            for (String line : wrapText(blankLine, FONT_SM, maxTextW)) {
                contentY += questionLineHeight;
                gc.fillText(line, contentX, contentY);
            }
        } else {
            gc.setFont(FONT_SM);
            for (String line : questionLines) {
                contentY += questionLineHeight;
                gc.fillText(line, contentX, contentY);
            }
        }

        contentY += 16;

        // 2x2 answer grid
        drawAnswerGrid(gc, contentX, contentY, boxW, boxX, boxY, boxH, Integer.MAX_VALUE);

        if (!answerFeedback) {
            gc.setFont(FONT_XS);
            gc.setFill(TEXT_GRAY);
            String hint = buildSelectionHint(getSelectableAnswers().size());
            gc.fillText(hint, contentX, boxY + boxH - 10);
        }
    }

    private String buildSelectionHint(int answerCount) {
        int maxSelectable = Math.min(MAX_NUMBER_SHORTCUT, answerCount);
        String rangeHint = maxSelectable <= 1 ? "[1]" : "[1-" + maxSelectable + "]";
        String hint = "Mit " + rangeHint + " auswaehlen";
        if (answerCount > MAX_NUMBER_SHORTCUT) {
            hint += " (max " + MAX_NUMBER_SHORTCUT + ")";
        }
        return hint;
    }

    private void drawAnswerGrid(GraphicsContext gc, double contentX, double contentY,
                                 double boxW, double boxX, double boxY, double boxH, int answerMaxLines) {
        List<Answer> answers = getSelectableAnswers();
        if (answers.isEmpty()) return;

        double totalAnswerW = boxW - 36;
        double answerW = (totalAnswerW - ANSWER_GAP) / ANSWER_COLS;
        List<Double> rowHeights = calculateAnswerRowHeights(answerW, answers, answerMaxLines);

        double rowStartY = contentY;
        for (int row = 0; row < rowHeights.size(); row++) {
            double rowHeight = rowHeights.get(row);

            for (int col = 0; col < ANSWER_COLS; col++) {
                int index = row * ANSWER_COLS + col;
                if (index >= answers.size()) {
                    continue;
                }

                double ax = contentX + col * (answerW + ANSWER_GAP);
                double ay = rowStartY;
                boolean isSelected = selectedAnswers.contains(index + 1);

                if (answerFeedback && isSelected) {
                    gc.setFill(answers.get(index).points() > 0 ? CORRECT_BG : WRONG_BG);
                } else if (!answerFeedback && isSelected) {
                    gc.setFill(CORRECT_BG);
                } else {
                    gc.setFill(ANSWER_BG);
                }
                gc.fillRect(ax, ay, answerW, rowHeight);

                gc.setStroke(BOX_BORDER_INNER);
                gc.setLineWidth(2);
                gc.strokeRect(ax, ay, answerW, rowHeight);

                gc.setFont(FONT_XS);
                gc.setFill(TEXT_GOLD);
                String numberLabel = getNumberLabel(index + 1, isSelected);
                double labelX = ax + 6;
                double labelY = ay + 18;
                gc.fillText(numberLabel, labelX, labelY);

                double maxAnsW = getMaxAnswerTextWidth(answerW, numberLabel);
                List<String> wrappedAnswerLines = wrapAnswerText(answers.get(index).text(), maxAnsW, answerMaxLines);

                gc.setFont(FONT_XS);
                gc.setFill(TEXT_WHITE);
                double textStartX = labelX + getTextWidth(numberLabel, FONT_XS) + 8;
                double textY = ay + 18;
                for (String line : wrappedAnswerLines) {
                    gc.fillText(line, textStartX, textY);
                    textY += ANSWER_TEXT_LINE_HEIGHT;
                }

                if (GeneralSettings.isDevMode() && answers.get(index).points() > 0) {
                    gc.setFont(FONT_XS);
                    gc.setFill(TEXT_GOLD);
                    String marker = "[C]";
                    double markerW = getTextWidth(marker, FONT_XS);
                    gc.fillText(marker, ax + answerW - markerW - 6, ay + 12);
                }
            }

            rowStartY += rowHeight + ANSWER_GAP;
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
        return normalizeInlineSpacing(blank.textBefore()) + " ____ " + normalizeInlineSpacing(blank.textAfter());
    }

    private List<Answer> resolveAnswersForCurrentStep() {
        if (currentQuestion == null) {
            return List.of();
        }
        if (currentQuestion.getType() == QuestionType.FILL_IN_BLANK) {
            FillInBlankQuestion fib = (FillInBlankQuestion) currentQuestion;
            if (fillBlankIndex < 0 || fillBlankIndex >= fib.getBlanks().size()) {
                return List.of();
            }
            return fib.getBlanks().get(fillBlankIndex).options();
        }
        return currentQuestion.getAnswers();
    }

    private List<Answer> shuffleAnswers(List<Answer> answers) {
        if (answers.isEmpty()) {
            return List.of();
        }
        List<Answer> shuffled = new ArrayList<>(answers);
        Collections.shuffle(shuffled);
        return List.copyOf(shuffled);
    }

    private int sumPoints(Set<Integer> indices, List<Answer> answers) {
        int sum = 0;
        for (Integer idx : indices) {
            int answerIndex = idx - 1;
            if (answerIndex >= 0 && answerIndex < answers.size()) {
                sum += answers.get(answerIndex).points();
            }
        }
        return sum;
    }

    private int calculateWrongPointsForMulti(List<Answer> answers) {
        int selectedNegativeSum = selectedAnswers.stream()
                .map(index -> index - 1)
                .filter(index -> index >= 0 && index < answers.size())
                .mapToInt(index -> answers.get(index).points())
                .filter(points -> points < 0)
                .sum();

        if (selectedNegativeSum < 0) {
            return selectedNegativeSum;
        }

        return answers.stream()
                .mapToInt(Answer::points)
                .filter(points -> points < 0)
                .min()
                .orElse(-10);
    }

    private String getNumberLabel(int answerNumber, boolean isSelected) {
        return isMultiSelectQuestion()
                ? (isSelected ? "[x] " : "[ ] ") + answerNumber + "."
                : answerNumber + ".";
    }

    private double getMaxAnswerTextWidth(double answerW, String numberLabel) {
        double prefixWidth = getTextWidth(numberLabel, FONT_XS) + 8;
        double rightPadding = 32;
        return Math.max(20, answerW - prefixWidth - rightPadding);
    }

    private List<String> wrapAnswerText(String text, double maxWidth, int maxLines) {
        List<String> lines = wrapText(text == null ? "" : text, FONT_XS, maxWidth);
        if (lines.isEmpty()) {
            return List.of("");
        }
        if (maxLines != Integer.MAX_VALUE && lines.size() > maxLines) {
            List<String> limited = new ArrayList<>(lines.subList(0, maxLines));
            int last = limited.size() - 1;
            String lastLine = limited.get(last);
            while (lastLine.length() > 2 && getTextWidth(lastLine + "..", FONT_XS) > maxWidth) {
                lastLine = lastLine.substring(0, lastLine.length() - 1);
            }
            limited.set(last, lastLine + "..");
            return limited;
        }
        return lines;
    }

    private List<Double> calculateAnswerRowHeights(double answerW, List<Answer> answers, int answerMaxLines) {
        int rows = (int) Math.ceil(Math.max(1, answers.size()) / (double) ANSWER_COLS);
        List<Double> rowHeights = new ArrayList<>(rows);

        for (int row = 0; row < rows; row++) {
            double rowHeight = ANSWER_MIN_HEIGHT;
            for (int col = 0; col < ANSWER_COLS; col++) {
                int index = row * ANSWER_COLS + col;
                if (index >= answers.size()) {
                    continue;
                }

                String numberLabel = getNumberLabel(index + 1, true);
                double maxAnsW = getMaxAnswerTextWidth(answerW, numberLabel);
                List<String> lines = wrapAnswerText(answers.get(index).text(), maxAnsW, answerMaxLines);
                double answerHeight = 12 + lines.size() * ANSWER_TEXT_LINE_HEIGHT;
                rowHeight = Math.max(rowHeight, answerHeight);
            }
            rowHeights.add(rowHeight);
        }

        return rowHeights;
    }

    private double calculateAnswerGridHeight(double boxW, List<Answer> answers, int answerMaxLines) {
        if (answers.isEmpty()) {
            return ANSWER_MIN_HEIGHT;
        }

        double totalAnswerW = boxW - 36;
        double answerW = (totalAnswerW - ANSWER_GAP) / ANSWER_COLS;
        List<Double> rowHeights = calculateAnswerRowHeights(answerW, answers, answerMaxLines);

        double rowsHeight = rowHeights.stream().mapToDouble(Double::doubleValue).sum();
        double gapsHeight = Math.max(0, rowHeights.size() - 1) * ANSWER_GAP;
        return rowsHeight + gapsHeight;
    }

    private List<String> wrapMultilineText(String text, Font font, double maxWidth) {
        String normalizedText = normalizeQuestionWhitespace(text);
        if (normalizedText.isEmpty()) {
            return List.of();
        }

        String normalized = normalizedText.replace("\r\n", "\n").replace('\r', '\n');
        String[] rawLines = normalized.split("\n", -1);
        List<String> result = new ArrayList<>();

        for (String rawLine : rawLines) {
            if (rawLine.isEmpty()) {
                result.add("");
                continue;
            }
            List<String> wrapped = wrapText(rawLine, font, maxWidth);
            if (wrapped.isEmpty()) {
                result.add("");
            } else {
                result.addAll(wrapped);
            }
        }

        return result;
    }

    private String normalizeQuestionWhitespace(String text) {
        if (text == null || text.isEmpty()) {
            return "";
        }

        String normalizedNewlines = text.replace("\r\n", "\n").replace('\r', '\n');
        String[] lines = normalizedNewlines.split("\n", -1);
        List<String> cleaned = new ArrayList<>(lines.length);

        for (String line : lines) {
            cleaned.add(normalizeInlineSpacing(line));
        }

        return String.join("\n", cleaned);
    }

    private String normalizeInlineSpacing(String text) {
        if (text == null || text.isEmpty()) {
            return "";
        }
        return text.replaceAll("[ \\t\\f\\x0B]+", " ").trim();
    }

    private String resolveTopicLabel() {
        return currentQuestion == null ? "" : currentQuestion.getTopicArea().getDisplayName();
    }
}
