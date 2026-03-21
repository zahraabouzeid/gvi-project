package com.gvi.project.helper;

import com.gvi.project.GamePanel;
import com.gvi.project.models.data_objects.ConditionObject;
import com.gvi.project.models.data_objects.ConditionsObject;


public class ConditionsHelper {
	private static boolean meet = false;
	public static boolean conditionsAreMeet(GamePanel gp, ConditionsObject conditions){

		switch (conditions.and_or){
			case "and":
				meet = true;
				for(ConditionObject cObj: conditions.conditionObjects){
					if (meet){
						checkCondition(gp, cObj);
					}
				}
				break;
			case "or":
				meet = false;
				for(ConditionObject cObj: conditions.conditionObjects){
					if (!meet){
						checkCondition(gp, cObj);
					}
				}
				break;
			default:
				meet = false;
		}

		return meet;
	}

	private static void checkCondition(GamePanel gp, ConditionObject cObj ){

		switch (cObj.type) {
			case "item":
				checkForItems(gp, cObj);
				break;
			case "statistic":
				checkForStatistic(gp, cObj);
				break;
			default: meet = false;
		}
	}

	private static void checkForItems(GamePanel gp, ConditionObject cObj){
		boolean itemsMeet = true;
		switch (cObj.compareWith) {
			case "key_copper":
			case "key_gold":
			case "key_iron":
				if (!gp.player.playerItems.containsKey(cObj.compareWith)) {
					gp.ui.openMessage("You have no keys of any kind");
					itemsMeet = false;
					break;
				}

				if (!compareValues(gp.player.playerItems.get(cObj.compareWith), cObj.value, cObj.comparator)) {
					gp.ui.openMessage("Needs %d key of each kind".formatted(cObj.value));
					itemsMeet = false;
				}
				break;
			default:
				itemsMeet = false;
		}
		meet = itemsMeet;
	}

	private static void checkForStatistic(GamePanel gp, ConditionObject cObj){
		boolean statisticMeet = true;
		switch (cObj.compareWith) {
			case "score":
				if(!compareValues(gp.player.score, cObj.value, cObj.comparator)){
					gp.ui.openMessage("Needs a score of %s or higher".formatted(cObj.value));
					statisticMeet = false;
				}
				break;
			default:
				statisticMeet = false;
		}

		meet = statisticMeet;
	}

	private static boolean compareValues(int val1, int val2, String comparator){
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
