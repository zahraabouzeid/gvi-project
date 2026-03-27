package com.gvi.project;

import com.gvi.project.helper.SaveManager;
import com.gvi.project.manager.RewardPersistenceManager;
import com.gvi.project.models.questions.Answer;
import com.gvi.project.models.questions.Question;
import com.gvi.project.models.questions.Reward;
import com.gvi.project.models.questions.RewardCalculator;
import com.gvi.project.ui.*;
import javafx.scene.canvas.GraphicsContext;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Set;

public class UI {

    private final GamePanel gp;

    private final TitleScreen titleScreen;
    private final CharacterNameScreen characterNameScreen;
    private final CharacterCreationScreen characterCreationScreen;
    private final GameOverScreen gameOverScreen;
    private final WinScreen winScreen;
    private final PauseScreen pauseScreen;
    private final SaveSlotScreen saveSlotScreen;
    private final LoadingScreen loadingScreen;
    private final HUD hud;
    private final QuizDialog quizDialog;
    public final Minimap minimap;

    private SaveManager.SlotInfo[] cachedSlotInfos;

    public boolean messageOn = false;
    public String message = "";
    private int messageCounter = 0;
    public boolean gameFinished = false;
    double playtime;
    
    // Reward system tracking - Belohnungssystem für Medaillen
    private int maxPossiblePoints = 0;
    private Reward earnedReward = Reward.NONE;
    private double performancePercentage = 0.0;
    private boolean shouldShowWinScreen = false; // Flag: Winscreen nur anzeigen wenn neue Medaille erreicht
    
    // Manager für permanentes Speichern von Medaillen
    private final RewardPersistenceManager rewardPersistence;

    private final DecimalFormat df = new DecimalFormat("#.00");

    public static final int FEEDBACK_DURATION = QuizDialog.FEEDBACK_DURATION;

    public UI(GamePanel gp) {
        this.gp = gp;

        // Initialisiere RewardPersistenceManager für Medaillen-Speicherung
        rewardPersistence = new RewardPersistenceManager();

        titleScreen = new TitleScreen();
        characterNameScreen = new CharacterNameScreen();
        characterCreationScreen = new CharacterCreationScreen();
        gameOverScreen = new GameOverScreen();
        winScreen = new WinScreen(GeneralSettings.getScreenWidth(), GeneralSettings.getScreenHeight());
        pauseScreen = new PauseScreen();
        saveSlotScreen = new SaveSlotScreen();
        loadingScreen = new LoadingScreen();
        hud = new HUD(gp);
        quizDialog = new QuizDialog();
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
    public boolean isMultipleChoiceQuestion() { return quizDialog.isMultipleChoiceQuestion(); }
    public boolean isMultiSelectQuestion()    { return quizDialog.isMultiSelectQuestion(); }
    public boolean handleQuizNumberInput(int number) { return quizDialog.handleNumberInput(number); }
    public boolean submitQuizSelection()      { return quizDialog.submitSelectionIfNeeded(); }
    public int getResolvedQuizPoints()        { return quizDialog.getResolvedPoints(); }
    public void resetQuizAfterWrongAnswer()   { quizDialog.resetAfterWrongAnswer(); }


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
        shouldShowWinScreen = false;
        messageOn = false;
        playtime = 0;
        gameOverScreen.reset();
        hud.reset();
        maxPossiblePoints = 0;
        earnedReward = Reward.NONE;
        performancePercentage = 0.0;
    }

    /**
     * Adds max possible points for a question (to be called when question is presented).
     * @param points the maximum points achievable for this question
     */
    public void addMaxPossiblePoints(int points) {
        maxPossiblePoints += Math.abs(points); // Use absolute value
    }

    /**
     * Sets the max possible points (for testing purposes).
     * @param points the total max points
     */
    public void setMaxPossiblePoints(int points) {
        maxPossiblePoints = points;
    }

    /**
     * Berechnet die erreichte Belohnung basierend auf dem Spieler-Score.
     * Prüft, ob die Medaille neu ist und angezeigt werden soll.
     * Implementiert einmaliger Trigger: Jede Medaille wird nur einmal angezeigt.
     */
    public void calculateReward() {
        RewardCalculator calculator = new RewardCalculator(gp.player.score, maxPossiblePoints);
        performancePercentage = calculator.calculatePercentage();
        earnedReward = calculator.calculateReward();
        
        // Prüfe ob diese Medaille neu ist (einmaliger Trigger)
        // checkAndMarkReward gibt true zurück, wenn die Medaille neu ist
        shouldShowWinScreen = rewardPersistence.checkAndMarkReward(earnedReward);
    }

    public int getMaxPossiblePoints() {
        return maxPossiblePoints;
    }

    public Reward getEarnedReward() {
        return earnedReward;
    }

    public double getPerformancePercentage() {
        return performancePercentage;
    }
    
    /**
     * Prüft, ob der Winscreen angezeigt werden soll.
     * Wird nur true, wenn eine neue Medaille erreicht wurde.
     * 
     * @return true, wenn der Winscreen angezeigt werden soll
     */
    public boolean shouldShowWinScreen() {
        return shouldShowWinScreen;
    }
    
    /**
     * Schließt den Winscreen und setzt das entsprechende Flag zurück.
     * Das Spiel läuft nahtlos weiter (kein Reset).
     */
    public void closeWinScreen() {
        shouldShowWinScreen = false;
        gameFinished = false;
    }
    
    /**
     * Prüft, ob ein neuer Schwellenwert erreicht wurde und zeigt ggf. den Winscreen an.
     * Diese Methode wird nach jeder Punktevergabe aufgerufen (Benutzerführung: sofortige Belohnung).
     * 
     * @param gp das GamePanel für Sound-Effekte
     */
    public void checkRewardThreshold(GamePanel gp) {
        // Berechne aktuelle Belohnung
        calculateReward();
        
        // Wenn eine neue Medaille erreicht wurde, zeige Winscreen
        if (shouldShowWinScreen()) {
            gameFinished = true;
            gp.stopMusic();
            gp.playSE(4); // Victory sound
        }
    }
    
    /**
     * Gibt alle bereits erreichten Medaillen zurück (für HUD-Anzeige).
     * 
     * @return Set aller erreichten Medaillen
     */
    public Set<Reward> getAchievedRewards() {
        return rewardPersistence.getAchievedRewards();
    }
    
    /**
     * Setzt alle erreichten Medaillen zurück (nur für Dev-Mode/Testing).
     */
    public void resetAllRewards() {
        rewardPersistence.resetAllRewards();
    }

    public void showFloatingScore(int points) {
        hud.showFloatingScore(points);
    }

    public void drawTitleScreen(GraphicsContext gc) {
        titleScreen.draw(gc);
    }

    public void drawPauseScreen(GraphicsContext gc) {
        hud.draw(gc, "Time: " + df.format(playtime));
        pauseScreen.draw(gc);
    }

    public void resetPauseScreen() {
        pauseScreen.reset();
    }

    public void navigatePauseUp() {
        pauseScreen.navigateUp();
    }

    public void navigatePauseDown() {
        pauseScreen.navigateDown();
    }

    public int getPauseSelectedOption() {
        return pauseScreen.getSelectedOption();
    }

    public void openSaveSlot() {
        saveSlotScreen.open("save");
        refreshSlotInfos();
    }

    public void openLoadSlot() {
        saveSlotScreen.open("load");
        refreshSlotInfos();
    }

    public void refreshSlotInfos() {
        cachedSlotInfos = new SaveManager.SlotInfo[] {
            gp.saveManager.getSlotInfo(1),
            gp.saveManager.getSlotInfo(2),
            gp.saveManager.getSlotInfo(3)
        };
    }

    public void navigateSlotUp()   { saveSlotScreen.navigateUp(); }
    public void navigateSlotDown() { saveSlotScreen.navigateDown(); }
    public int  getSelectedSlot()  { return saveSlotScreen.getSelectedSlot(); }
    public boolean isConfirmDelete() { return saveSlotScreen.isConfirmDelete(); }
    public void setConfirmDelete(boolean confirm) { saveSlotScreen.setConfirmDelete(confirm); }

    public void drawSaveSlotScreen(GraphicsContext gc) {
        saveSlotScreen.draw(gc, cachedSlotInfos);
    }

    public void drawLoadSlotScreen(GraphicsContext gc) {
        saveSlotScreen.draw(gc, cachedSlotInfos);
    }

    public void drawLoadingScreen(GraphicsContext gc, int frame) {
        loadingScreen.draw(gc, frame);
    }

    public void drawCharacterNameScreen(GraphicsContext gc) {
        characterNameScreen.draw(gc, gp.player.playerName);
    }

    public int getCharacterNameMaxLength() {
        return characterNameScreen.getMaxNameLength();
    }

    public void drawCharacterCreationScreen(GraphicsContext gc) {
        characterCreationScreen.draw(gc, gp.player);
    }

    public void navigateCharacterCreationUp() {
        characterCreationScreen.navigateUp();
    }

    public void navigateCharacterCreationDown() {
        characterCreationScreen.navigateDown();
    }

    public void navigateCharacterCreationLeft() {
        characterCreationScreen.navigateLeft();
    }

    public void navigateCharacterCreationRight() {
        characterCreationScreen.navigateRight();
    }

    public void applyCharacterCreation() {
        characterCreationScreen.applyChanges(gp.player);
    }

    public void resetCharacterCreationScreen() {
        characterCreationScreen.reset();
    }

    public void draw(GraphicsContext gc) {
        if (gp.player.isDead) {
            gameOverScreen.draw(gc);
            return;
        }

        if (gameFinished) {
            winScreen.draw(gc, df.format(playtime), earnedReward, performancePercentage, 
                          gp.player.score, maxPossiblePoints);
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
        double msgH = UIUtils.getTextHeight(message,UITheme.FONT_SM);
        double fontH = UIUtils.getTextHeight("###", UITheme.FONT_SM);
        double msgXOrigin = GeneralSettings.getScreenWidth() / 2.0 - msgW / 2.0;
        double msgYOrigin = GeneralSettings.getScreenHeight() / 2.0 - 80;
        double msgYOTextHeightOffset = msgYOrigin - msgH;
        double paddingLR = 16;
        double paddingTB = 22;

        double boxX = msgXOrigin - paddingLR;
        double boxY = msgYOrigin - paddingTB - msgH;
        double boxW = msgW + paddingLR * 2;
        double boxH = msgH + paddingTB;

        double msgTimerMultiplier = msgH / fontH ;

        UIUtils.drawPixelBox(gc, boxX, boxY, boxW, boxH - fontH / 2 + 3);
        gc.setFill(UITheme.TEXT_WHITE);
        gc.fillText(message, msgXOrigin, msgYOTextHeightOffset );

        messageCounter++;
        if (messageCounter > 120 * msgTimerMultiplier) {
            messageCounter = 0;
            messageOn = false;
        }
    }
}
