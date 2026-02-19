package com.gvi.project;

import javafx.scene.Node;
import javafx.scene.input.KeyCode;

public class KeyHandler {

	public boolean upPressed, downPressed, leftPressed, rightPressed;
	public boolean fPressed;
	public boolean escPressed;
	public boolean enterPressed;
	public int numberPressed = -1; 

	public void setupKeyListeners(Node node) {
		node.setOnKeyPressed(e -> {
			KeyCode code = e.getCode();
			if (code == KeyCode.W || code == KeyCode.UP) upPressed = true;
			if (code == KeyCode.A || code == KeyCode.LEFT) leftPressed = true;
			if (code == KeyCode.S || code == KeyCode.DOWN) downPressed = true;
			if (code == KeyCode.D || code == KeyCode.RIGHT) rightPressed = true;
			if (code == KeyCode.F) fPressed = true;
			if (code == KeyCode.ESCAPE) escPressed = true;
			if (code == KeyCode.ENTER) enterPressed = true;
			if (code == KeyCode.DIGIT1 || code == KeyCode.NUMPAD1) numberPressed = 1;
			if (code == KeyCode.DIGIT2 || code == KeyCode.NUMPAD2) numberPressed = 2;
			if (code == KeyCode.DIGIT3 || code == KeyCode.NUMPAD3) numberPressed = 3;
			if (code == KeyCode.DIGIT4 || code == KeyCode.NUMPAD4) numberPressed = 4;
		});

		node.setOnKeyReleased(e -> {
			KeyCode code = e.getCode();
			if (code == KeyCode.W || code == KeyCode.UP) upPressed = false;
			if (code == KeyCode.A || code == KeyCode.LEFT) leftPressed = false;
			if (code == KeyCode.S || code == KeyCode.DOWN) downPressed = false;
			if (code == KeyCode.D || code == KeyCode.RIGHT) rightPressed = false;
			if (code == KeyCode.F) fPressed = false;
			if (code == KeyCode.ESCAPE) escPressed = false;
			if (code == KeyCode.ENTER) enterPressed = false;
		});

		node.setFocusTraversable(true);
		node.requestFocus();
	}
}
