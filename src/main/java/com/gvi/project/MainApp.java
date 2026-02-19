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

		BorderPane root = new BorderPane();
		root.setCenter(gp.canvas);

		mainStage.setScene(new Scene(root));
		mainStage.setResizable(false);

		mainStage.setOnShown(e -> centerStage(mainStage));
		mainStage.show();
	}

	private void centerStage(Stage stage) {
		Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
		stage.setX((bounds.getWidth() - stage.getWidth()) / 2);
		stage.setY((bounds.getHeight() - stage.getHeight()) / 2);
	}
}
