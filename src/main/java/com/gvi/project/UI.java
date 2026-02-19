package com.gvi.project;

import com.gvi.project.models.objects.OBJ_Key;
import com.gvi.project.models.questions.Answer;
import com.gvi.project.models.questions.Question;
import com.gvi.project.ui.*;
import javafx.scene.canvas.GraphicsContext;

import java.text.DecimalFormat;
import java.util.List;

public class UI {

    private final GamePanel gp;

    private final TitleScreen titleScreen;
    private final GameOverScreen gameOverScreen;
    private final WinScreen winScreen;
    private final HUD hud;
    private final QuizDialog quizDialog;
    public final Minimap minimap;

    public boolean messageOn = false;
    public String message = "";
    private int messageCounter = 0;
    public boolean gameFinished = false;
    double playtime;

    private final DecimalFormat df = new DecimalFormat("#.00");

    public static final int FEEDBACK_DURATION = QuizDialog.FEEDBACK_DURATION;

    public UI(GamePanel gp) {
        this.gp = gp;

        titleScreen = new TitleScreen(gp.screenWidth, gp.screenHeight);
        gameOverScreen = new GameOverScreen(gp.screenWidth, gp.screenHeight);
        winScreen = new WinScreen(gp.screenWidth, gp.screenHeight);
        hud = new HUD(gp, new OBJ_Key().image);
        quizDialog = new QuizDialog(gp.screenWidth, gp.screenHeight);
        minimap = new Minimap(gp);
    }

    public void openQuiz(Question question, int remaining) {
        quizDialog.open(question, remaining);
    }

    public void closeQuiz() {
        quizDialog.close();
    }

    public List<Answer> getSelectableAnswers() {
        return quizDialog.getSelectableAnswers();
    }

    public boolean advanceFillBlankIfNeeded() {
        return quizDialog.advanceFillBlankIfNeeded();
    }

    public Question getCurrentQuestion()      { return quizDialog.currentQuestion; }
    public int getSelectedAnswer()            { return quizDialog.selectedAnswer; }
    public void setSelectedAnswer(int v)      { quizDialog.selectedAnswer = v; }
    public boolean isAnswerFeedback()         { return quizDialog.answerFeedback; }
    public void setAnswerFeedback(boolean v)  { quizDialog.answerFeedback = v; }
    public boolean isAnswerCorrect()          { return quizDialog.answerCorrect; }
    public void setAnswerCorrect(boolean v)   { quizDialog.answerCorrect = v; }
    public int getFeedbackCounter()           { return quizDialog.feedbackCounter; }
    public void setFeedbackCounter(int v)     { quizDialog.feedbackCounter = v; }
    public boolean isQuizOpen()               { return quizDialog.quizOpen; }


    public void openMessage(String msg) {
        this.message = msg;
        messageOn = true;
    }

    public void closeMessage() {
        messageOn = false;
    }

    public void resetGame() {
        closeQuiz();
        gameFinished = false;
        messageOn = false;
        playtime = 0;
        gameOverScreen.reset();
        hud.reset();
    }

    public void showFloatingScore(int points) {
        hud.showFloatingScore(points);
    }

    public void drawTitleScreen(GraphicsContext gc) {
        titleScreen.draw(gc);
    }

    public void draw(GraphicsContext gc) {
        if (gp.player.isDead) {
            gameOverScreen.draw(gc);
            return;
        }

        if (gameFinished) {
            winScreen.draw(gc, df.format(playtime));
            return;
        }

        playtime += 1.0 / 60.0;
        String timeStr = "Time: " + df.format(playtime);
        hud.draw(gc, timeStr);

        if (messageOn) {
            drawMessage(gc);
        }

        if (gp.player.nearbyObjectIndex != -1 && !quizDialog.quizOpen) {
            hud.drawInteractHint(gc);
        }

        if (quizDialog.quizOpen) {
            quizDialog.draw(gc);
        }
    }

    private void drawMessage(GraphicsContext gc) {
        gc.setFont(UITheme.FONT_SM);
        double msgW = UIUtils.getTextWidth(message, UITheme.FONT_SM);
        double msgX = gp.screenWidth / 2.0 - msgW / 2.0;
        double msgY = gp.screenHeight / 2.0 - 80;
        UIUtils.drawPixelBox(gc, msgX - 16, msgY - 22, msgW + 32, 34);
        gc.setFill(UITheme.TEXT_WHITE);
        gc.fillText(message, msgX, msgY);

        messageCounter++;
        if (messageCounter > 120) {
            messageCounter = 0;
            messageOn = false;
        }
    }
}
