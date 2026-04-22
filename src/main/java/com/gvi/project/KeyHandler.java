package com.gvi.project;

import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KeyHandler {
	private static final Logger log = LoggerFactory.getLogger(KeyHandler.class);

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
			handleKeyPressed(e.getCode());
		});

		// Text input is separated from key presses so JavaFX can deliver composed
		// characters consistently for the name-entry screen.
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
			handleKeyReleased(e.getCode());
		});

		node.setFocusTraversable(true);
		node.requestFocus();
	}

	private void handleKeyPressed(KeyCode code) {
		if (movementLocked && code != lastKeyCode) {
			// Requiring a fresh key press prevents held movement keys from leaking into
			// menus immediately after a modal interaction closes.
			unlockMovement();
		}

		lastKeyCode = code;
		updateMovementKeys(code, true);
		updateActionKeys(code, true);
		updateNumberPressed(code);
		updateDebugKeys(code, true);
	}

	private void handleKeyReleased(KeyCode code) {
		updateMovementKeys(code, false);
		updateActionKeys(code, false);
		updateDebugKeys(code, false);
	}

	private void updateMovementKeys(KeyCode code, boolean pressed) {
		switch (code) {
			case W, UP -> upPressed = pressed;
			case A, LEFT -> leftPressed = pressed;
			case S, DOWN -> downPressed = pressed;
			case D, RIGHT -> rightPressed = pressed;
			default -> {
			}
		}
	}

	private void updateActionKeys(KeyCode code, boolean pressed) {
		switch (code) {
			case F -> fPressed = pressed;
			case ESCAPE -> escPressed = pressed;
			case ENTER -> enterPressed = pressed;
			case BACK_SPACE -> backspacePressed = pressed;
			case DELETE -> delPressed = pressed;
			case TAB -> tabPressed = pressed;
			default -> {
			}
		}
	}

	private void updateNumberPressed(KeyCode code) {
		switch (code) {
			case DIGIT1, NUMPAD1 -> numberPressed = 1;
			case DIGIT2, NUMPAD2 -> numberPressed = 2;
			case DIGIT3, NUMPAD3 -> numberPressed = 3;
			case DIGIT4, NUMPAD4 -> numberPressed = 4;
			case DIGIT5, NUMPAD5 -> numberPressed = 5;
			case DIGIT6, NUMPAD6 -> numberPressed = 6;
			case DIGIT7, NUMPAD7 -> numberPressed = 7;
			case DIGIT8, NUMPAD8 -> numberPressed = 8;
			case DIGIT9, NUMPAD9 -> numberPressed = 9;
			case DIGIT0, NUMPAD0 -> numberPressed = 10;
			default -> {
			}
		}
	}

	private void updateDebugKeys(KeyCode code, boolean pressed) {
		switch (code) {
			// F2 is a latched toggle, the remaining debug keys are momentary actions.
			case F2 -> {
				if (!pressed) {
					f2Pressed = !f2Pressed;
					log.info("Developer mode {}.", f2Pressed ? "enabled" : "disabled");
				}
			}
			case F3 -> f3Pressed = pressed;
			case F4 -> f4Pressed = pressed;
			case F5 -> f5Pressed = pressed;
			case F6 -> f6Pressed = pressed;
			case F7 -> f7Pressed = pressed;
			case F8 -> f8Pressed = pressed;
			case F9 -> f9Pressed = pressed;
			case F10 -> f10Pressed = pressed;
			case F11 -> f11Pressed = pressed;
			case F12 -> f12Pressed = pressed;
			default -> {
			}
		}
	}
}
