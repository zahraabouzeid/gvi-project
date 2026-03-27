package com.gvi.project.helper;

import java.util.ArrayList;
import java.util.List;

public class ConditionResult {
	public boolean success;
	private final List<String> messages = new ArrayList<>();

	public ConditionResult() {}

	public ConditionResult(boolean success, String message) {
		this.success = success;
		this.messages.add(message);
	}

	public void addStringMessage(String message) {
		if(message.isEmpty()) return;
		this.messages.add("- " + message);
	}

	public String getMessage() {
		return String.join("\n", messages);
	}
}