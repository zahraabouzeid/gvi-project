package com.gvi.project.helper;

import com.gvi.project.GamePanel;
import com.gvi.project.models.data_objects.ConditionObject;
import com.gvi.project.models.data_objects.ConditionsObject;


public class ConditionsHelper {

	public static ConditionResult conditionsAreMet(GamePanel gp, ConditionsObject conditions) {

		StringBuilder messageBuilder = new StringBuilder();
		boolean overallResult;

		if ("and".equals(conditions.and_or)) {
			overallResult = true;

			for (ConditionObject cObj : conditions.conditionObjects) {
				ConditionResult result = checkCondition(gp, cObj);

				if (!result.success) {
					overallResult = false;
					messageBuilder.append(result.message).append("\n");
				}
			}
		} else if ("or".equals(conditions.and_or)) {
			overallResult = false;

			for (ConditionObject cObj : conditions.conditionObjects) {
				ConditionResult result = checkCondition(gp, cObj);

				if (result.success) {
					return new ConditionResult(true, "");
				} else {
					messageBuilder.append(result.message).append("\n");
				}
			}
		} else {
			return new ConditionResult(false, "Invalid condition type");
		}

		return new ConditionResult(overallResult, messageBuilder.toString().trim());
	}

	private static ConditionResult checkCondition(GamePanel gp, ConditionObject cObj) {

		return switch (cObj.type) {
			case "item" -> checkForItems(gp, cObj);
			case "statistic" -> checkForStatistic(gp, cObj);
			default -> new ConditionResult(false, "Unknown condition type");
		};
	}

	private static ConditionResult checkForItems(GamePanel gp, ConditionObject cObj) {

		int playerValue = gp.player.playerItems.getOrDefault(cObj.compareWith, 0);

		boolean result = compareValues(playerValue, cObj.value, cObj.comparator);

		if (!result) {
			return new ConditionResult(false,
					"Your are missing %s x%d".formatted(cObj.compareWith, cObj.value));
		}

		return new ConditionResult(true, "");
	}

	private static ConditionResult checkForStatistic(GamePanel gp, ConditionObject cObj) {

		return switch (cObj.compareWith) {
			case "score" -> {
				boolean result = compareValues(gp.player.score, cObj.value, cObj.comparator);

				if (!result) {
					yield new ConditionResult(false,
							"Needs a score of %s or higher".formatted(cObj.value));
				}

				yield new ConditionResult(true, "");
			}
			default -> new ConditionResult(false, "Unknown statistic");
		};
	}

	private static boolean compareValues(int val1, int val2, String comparator) {
		return switch (comparator) {
			case "==" -> val1 == val2;
			case ">=" -> val1 >= val2;
			case "<=" -> val1 <= val2;
			case "<" -> val1 < val2;
			case ">" -> val1 > val2;
			default -> false;
		};
	}


}
