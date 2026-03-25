package com.gvi.project.helper;

import com.gvi.project.GamePanel;
import com.gvi.project.models.data_objects.ConditionObject;
import com.gvi.project.models.data_objects.ConditionsObject;


public class ConditionsHelper {

	public static ConditionResult conditionsAreMet(GamePanel gp, ConditionsObject conditions) {

		boolean overallResult;
		ConditionResult overallResultObject = new ConditionResult();

		if ("and".equals(conditions.and_or)) {
			overallResult = true;

			for (ConditionObject cObj : conditions.conditionObjects) {
				ConditionResult result = checkCondition(gp, cObj);

				if (!result.success) {
					overallResult = false;
					overallResultObject.addStringMessage(result.getMessage());
				}
			}
		} else if ("or".equals(conditions.and_or)) {
			overallResult = false;

			for (ConditionObject cObj : conditions.conditionObjects) {
				ConditionResult result = checkCondition(gp, cObj);

				if (result.success) {
					return new ConditionResult(true, "");
				} else {
					overallResultObject.addStringMessage(result.getMessage());
				}
			}
		} else {
			return overallResultObject;
		}

		overallResultObject.success = overallResult;
		return overallResultObject;
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
					cObj.messageOnFailure);
		}

		return new ConditionResult(true, "");
	}

	private static ConditionResult checkForStatistic(GamePanel gp, ConditionObject cObj) {

		return switch (cObj.compareWith) {
			case "score" -> {
				boolean result = compareValues(gp.player.score, cObj.value, cObj.comparator);

				if (!result) {
					yield new ConditionResult(false,
							cObj.messageOnFailure);
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
