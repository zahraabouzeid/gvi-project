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
	public boolean f3Pressed = false;  // Debug: Add iron keys
	public boolean f4Pressed = false;  // Debug: Add gold keys
	public boolean f5Pressed = false;  // Debug: Add copper keys
	public boolean f6Pressed = false;  // Debug: Next map
	public boolean f7Pressed = false;  // Cheat/Debug: Win with Bronze or previous map
	public boolean f8Pressed = false;  // Cheat/Debug: Win with Silver or heal
	public boolean f9Pressed = false;  // Cheat/Debug: Win with Gold or complete quiz
	public boolean f10Pressed = false; // Cheat/Debug: Win with Gold Perfect or test score
	public boolean f11Pressed = false; // Dev: Reset all rewards or test score
	public boolean f12Pressed = false; // Debug: Test score
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
			
			// Debug and cheat keys
			if (code == KeyCode.F3) f3Pressed = true;
			if (code == KeyCode.F4) f4Pressed = true;
			if (code == KeyCode.F5) f5Pressed = true;
			if (code == KeyCode.F6) f6Pressed = true;
			if (code == KeyCode.F7) f7Pressed = true;
			if (code == KeyCode.F8) f8Pressed = true;
			if (code == KeyCode.F9) f9Pressed = true;
			if (code == KeyCode.F10) f10Pressed = true;
			if (code == KeyCode.F11) f11Pressed = true;
			if (code == KeyCode.F12) f12Pressed = true;
		});

		// Handle text input (including uppercase) with onKeyTyped event
		node.setOnKeyTyped(e -> {
			String text = e.getCharacter();
			if (text != null && !text.isEmpty()) {
				char c = text.charAt(0);
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
			if (code == KeyCode.F3) f3Pressed = false;
			if (code == KeyCode.F4) f4Pressed = false;
			if (code == KeyCode.F5) f5Pressed = false;
			if (code == KeyCode.F6) f6Pressed = false;
			if (code == KeyCode.F7) f7Pressed = false;
			if (code == KeyCode.F8) f8Pressed = false;
			if (code == KeyCode.F9) f9Pressed = false;
			if (code == KeyCode.F10) f10Pressed = false;
			if (code == KeyCode.F11) f11Pressed = false;
			if (code == KeyCode.F12) f12Pressed = false;
		});

		node.setFocusTraversable(true);
		node.requestFocus();
	}
}
