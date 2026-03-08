package com.gvi.project;

import com.gvi.project.models.objects.*;
import com.gvi.project.models.questions.TopicArea;

public class AssetSetter {

	GamePanel gp;
	public AssetSetter(GamePanel gp) {
		this.gp = gp;
	}

	public void setObject() {
		// Boots
		gp.obj.add(0, new OBJ_Boots());
		gp.obj.get(0).worldX = 38 * gp.generalSettings.tileSize;
		gp.obj.get(0).worldY = 15 * gp.generalSettings.tileSize;

		// Crystals 
		gp.obj.add(1, new OBJ_QuizStation(TopicArea.SQL_GRUNDLAGEN));
		gp.obj.get(1).worldX = 2 * gp.generalSettings.tileSize;
		gp.obj.get(1).worldY = 5 * gp.generalSettings.tileSize;

		gp.obj.add(2, new OBJ_QuizStation(TopicArea.SELECT_ABFRAGEN));
		gp.obj.get(2).worldX = 24 * gp.generalSettings.tileSize;
		gp.obj.get(2).worldY = 5 * gp.generalSettings.tileSize;

		gp.obj.add(3, new OBJ_QuizStation(TopicArea.NORMALISIERUNG));
		gp.obj.get(3).worldX = 24 * gp.generalSettings.tileSize;
		gp.obj.get(3).worldY = 23 * gp.generalSettings.tileSize;

		gp.obj.add(4, new OBJ_QuizStation(TopicArea.ER_MODELLIERUNG));
		gp.obj.get(4).worldX = 37 * gp.generalSettings.tileSize;
		gp.obj.get(4).worldY = 24 * gp.generalSettings.tileSize;

		gp.obj.add(5, new OBJ_QuizStation(TopicArea.DDL_DML));
		gp.obj.get(5).worldX = 11 * gp.generalSettings.tileSize;
		gp.obj.get(5).worldY = 24 * gp.generalSettings.tileSize;

		gp.obj.add(6, new OBJ_QuizStation(TopicArea.JOINS_SUBQUERIES));
		gp.obj.get(6).worldX = 36 * gp.generalSettings.tileSize;
		gp.obj.get(6).worldY = 6 * gp.generalSettings.tileSize;

		gp.obj.add(7, new OBJ_MapChangeTrigger("down"));
		gp.obj.get(7).worldX = 4 * gp.generalSettings.tileSize;
		gp.obj.get(7).worldY = 10 * gp.generalSettings.tileSize;

		gp.obj.add(8, new OBJ_MapChangeTrigger("left"));
		gp.obj.get(8).worldX = 0 * gp.generalSettings.tileSize;
		gp.obj.get(8).worldY = 6 * gp.generalSettings.tileSize;

		gp.obj.add(9, new OBJ_MapChangeTrigger("right"));
		gp.obj.get(9).worldX = 9 * gp.generalSettings.tileSize;
		gp.obj.get(9).worldY = 6 * gp.generalSettings.tileSize;

		gp.obj.add(10, new OBJ_MapChangeTrigger("up"));
		gp.obj.get(10).worldX = 4 * gp.generalSettings.tileSize;
		gp.obj.get(10).worldY = 1 * gp.generalSettings.tileSize;

		gp.obj.add(11, new OBJ_Door());
		gp.obj.get(11).worldX = 4 * gp.generalSettings.tileSize;
		gp.obj.get(11).worldY = 3 * gp.generalSettings.tileSize;
	}
}
