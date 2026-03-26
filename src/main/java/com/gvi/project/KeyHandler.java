package com.gvi.project;

import javafx.scene.Node;
import javafx.scene.input.KeyCode;

public class KeyHandler {

	public boolean upPressed, downPressed, leftPressed, rightPressed;
	public boolean fPressed;
	public boolean escPressed;
	public boolean enterPressed;
	public boolean backspacePressed;
	public boolean delPressed;
	public boolean tabPressed;
	public boolean f2Pressed = false;
	public boolean f7Pressed = false;  // Cheat: Win with Bronze
	public boolean f8Pressed = false;  // Cheat: Win with Silver
	public boolean f9Pressed = false;  // Cheat: Win with Gold
	public boolean f10Pressed = false; // Cheat: Win with Gold Perfect
	public int numberPressed = -1;
	public String typedCharacter = "";
	private boolean movementLocked = false;

	private KeyCode lastKeyCode;

	public void lockMovement() {
		movementLocked = true;
	}

	public void unlockMovement() {
		movementLocked = false;
	}

	public boolean isMovementLocked() {
		return movementLocked;
	}

	public void setupKeyListeners(Node node) {
		node.setOnKeyPressed(e -> {
			KeyCode code = e.getCode();

			if (movementLocked && code != lastKeyCode) {
				unlockMovement();
			}

			lastKeyCode = code;

			if (code == KeyCode.W || code == KeyCode.UP) upPressed = true;
			if (code == KeyCode.A || code == KeyCode.LEFT) leftPressed = true;
			if (code == KeyCode.S || code == KeyCode.DOWN) downPressed = true;
			if (code == KeyCode.D || code == KeyCode.RIGHT) rightPressed = true;
			if (code == KeyCode.F) fPressed = true;
			if (code == KeyCode.ESCAPE) escPressed = true;
			if (code == KeyCode.ENTER) enterPressed = true;
			if (code == KeyCode.BACK_SPACE) backspacePressed = true;
			if (code == KeyCode.DELETE) delPressed = true;
			if (code == KeyCode.TAB) tabPressed = true;
			if (code == KeyCode.DIGIT1 || code == KeyCode.NUMPAD1) numberPressed = 1;
			if (code == KeyCode.DIGIT2 || code == KeyCode.NUMPAD2) numberPressed = 2;
			if (code == KeyCode.DIGIT3 || code == KeyCode.NUMPAD3) numberPressed = 3;
			if (code == KeyCode.DIGIT4 || code == KeyCode.NUMPAD4) numberPressed = 4;
			if (code == KeyCode.DIGIT5 || code == KeyCode.NUMPAD5) numberPressed = 5;
			if (code == KeyCode.DIGIT6 || code == KeyCode.NUMPAD6) numberPressed = 6;
			if (code == KeyCode.DIGIT7 || code == KeyCode.NUMPAD7) numberPressed = 7;
			if (code == KeyCode.DIGIT8 || code == KeyCode.NUMPAD8) numberPressed = 8;
			if (code == KeyCode.DIGIT9 || code == KeyCode.NUMPAD9) numberPressed = 9;
			
			// Cheat keys for testing reward system
			if (code == KeyCode.F7) f7Pressed = true;
			if (code == KeyCode.F8) f8Pressed = true;
			if (code == KeyCode.F9) f9Pressed = true;
			if (code == KeyCode.F10) f10Pressed = true;
		});

		// Handle text input (including uppercase) with onKeyTyped event
		node.setOnKeyTyped(e -> {
			String text = e.getCharacter();
			if (text != null && !text.isEmpty()) {
				char c = text.charAt(0);
				// Allow letters (upper and lowercase), digits, space, dash, underscore
				if (Character.isLetterOrDigit(c) || c == ' ' || c == '-' || c == '_') {
					typedCharacter = text;
				}
			}
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
			if (code == KeyCode.BACK_SPACE) backspacePressed = false;
			if (code == KeyCode.DELETE) delPressed = false;
			if (code == KeyCode.TAB) tabPressed = false;
			if (code == KeyCode.F2) f2Pressed = !f2Pressed;
			if (code == KeyCode.F7) f7Pressed = false;
			if (code == KeyCode.F8) f8Pressed = false;
			if (code == KeyCode.F9) f9Pressed = false;
			if (code == KeyCode.F10) f10Pressed = false;

			if (!upPressed && !leftPressed && !downPressed && !rightPressed){
				unlockMovement();
			}
		});

		node.setFocusTraversable(true);
		node.requestFocus();
	}
}
