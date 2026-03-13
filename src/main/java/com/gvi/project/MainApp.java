package com.gvi.project;

import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class MainApp extends Application {

	public static void main(String[] args) {
		launch();
	}

	@Override
	public void start(Stage mainStage) {
		mainStage.setTitle("GVI Project");
		GamePanel gp = new GamePanel();

		// Bildschirm-Informationen abrufen
		Screen primaryScreen = Screen.getPrimary();
		Rectangle2D bounds = primaryScreen.getVisualBounds();
		
		// WICHTIG: DPI-Skalierung des Bildschirms berücksichtigen (z.B. 100%, 125%, 150%, 200%)
		double outputScaleX = primaryScreen.getOutputScaleX();
		double outputScaleY = primaryScreen.getOutputScaleY();
		
		// Tatsächliche Desktop-Größe (logische Pixel, bereits DPI-skaliert von JavaFX)
		double desktopWidth = bounds.getWidth();
		double desktopHeight = bounds.getHeight();
		
		int gameWidth = gp.generalSettings.screenWidth;
		int gameHeight = gp.generalSettings.screenHeight;

		BorderPane root = new BorderPane();
		root.setCenter(gp.canvas);
		
		// Skalierungsfaktor berechnen unter Berücksichtigung der DPI-Skalierung
		// Bei 200% Windows-Skalierung wird outputScale = 2.0 sein
		double scaleX = desktopWidth / gameWidth;
		double scaleY = desktopHeight / gameHeight;
		double scale = Math.min(scaleX, scaleY); // Verwende den kleineren Faktor für Aspect-Ratio
		
		// Skalierte Spielgröße berechnen
		double scaledGameWidth = gameWidth * scale;
		double scaledGameHeight = gameHeight * scale;
		
		// Root mit der skalierten Größe setzen (nicht skalieren!)
		root.setPrefSize(scaledGameWidth, scaledGameHeight);
		root.setMinSize(scaledGameWidth, scaledGameHeight);
		root.setMaxSize(scaledGameWidth, scaledGameHeight);
		
		// Canvas direkt skalieren stattdessen
		gp.canvas.setScaleX(scale);
		gp.canvas.setScaleY(scale);

		// Scene mit Desktop-Größe für Vollbild, schwarzer Hintergrund für Letterboxing
		Scene scene = new Scene(root, desktopWidth, desktopHeight);
		scene.setFill(javafx.scene.paint.Color.BLACK);
		
		mainStage.setScene(scene);
		mainStage.setResizable(false);
		mainStage.setFullScreen(true);
		mainStage.setFullScreenExitHint("Drücke ESC zum Verlassen des Vollbildmodus");
		mainStage.setFullScreenExitKeyCombination(javafx.scene.input.KeyCombination.valueOf("ESCAPE"));

		// Debug-Ausgabe um Skalierungsprobleme zu erkennen
		System.out.println("=== Bildschirm-Skalierung Debug ===");
		System.out.println("Screen Output Scale X: " + outputScaleX);
		System.out.println("Screen Output Scale Y: " + outputScaleY);
		System.out.println("Desktop Width: " + desktopWidth + " px");
		System.out.println("Desktop Height: " + desktopHeight + " px");
		System.out.println("Game Width: " + gameWidth + " px");
		System.out.println("Game Height: " + gameHeight + " px");
		System.out.println("Calculated Scale: " + scale);
		System.out.println("Scaled Game Width: " + scaledGameWidth + " px");
		System.out.println("Scaled Game Height: " + scaledGameHeight + " px");
		System.out.println("===================================");

		mainStage.show();
	}

	private void centerStage(Stage stage) {
		Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
		stage.setX((bounds.getWidth() - stage.getWidth()) / 2);
		stage.setY((bounds.getHeight() - stage.getHeight()) / 2);
	}
}
