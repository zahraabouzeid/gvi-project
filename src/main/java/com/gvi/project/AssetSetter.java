package com.gvi.project;

import com.gvi.project.models.objects.*;
import com.gvi.project.models.questions.TopicArea;

public class AssetSetter {

	GamePanel gp;
	public AssetSetter(GamePanel gp) {
		this.gp = gp;
	}

	public void setObject() {
		// Doors
		gp.obj[0] = new OBJ_Door();
		gp.obj[0].worldX = 10 * gp.tileSize;
		gp.obj[0].worldY = 11 * gp.tileSize;

		gp.obj[1] = new OBJ_Door();
		gp.obj[1].worldX = 8 * gp.tileSize;
		gp.obj[1].worldY = 28 * gp.tileSize;

		gp.obj[2] = new OBJ_Door();
		gp.obj[2].worldX = 12 * gp.tileSize;
		gp.obj[2].worldY = 22 * gp.tileSize;

		// Treasure
		gp.obj[3] = new OBJ_Chest();
		gp.obj[3].worldX = 10 * gp.tileSize;
		gp.obj[3].worldY = 7 * gp.tileSize;

		// Boots
		gp.obj[4] = new OBJ_Boots();
		gp.obj[4].worldX = 37 * gp.tileSize;
		gp.obj[4].worldY = 42 * gp.tileSize;

		// Crystals 
		gp.obj[5] = new OBJ_QuizStation(TopicArea.SQL_GRUNDLAGEN);
		gp.obj[5].worldX = 37 * gp.tileSize;
		gp.obj[5].worldY = 9 * gp.tileSize;

		gp.obj[6] = new OBJ_QuizStation(TopicArea.SELECT_ABFRAGEN);
		gp.obj[6].worldX = 23 * gp.tileSize;
		gp.obj[6].worldY = 21 * gp.tileSize;

		gp.obj[7] = new OBJ_QuizStation(TopicArea.NORMALISIERUNG);
		gp.obj[7].worldX = 11 * gp.tileSize;
		gp.obj[7].worldY = 31 * gp.tileSize;

		gp.obj[8] = new OBJ_QuizStation(TopicArea.ER_MODELLIERUNG);
		gp.obj[8].worldX = 36 * gp.tileSize;
		gp.obj[8].worldY = 32 * gp.tileSize;

		gp.obj[9] = new OBJ_QuizStation(TopicArea.DDL_DML);
		gp.obj[9].worldX = 22 * gp.tileSize;
		gp.obj[9].worldY = 39 * gp.tileSize;

		gp.obj[10] = new OBJ_QuizStation(TopicArea.JOINS_SUBQUERIES);
		gp.obj[10].worldX = 35 * gp.tileSize;
		gp.obj[10].worldY = 39 * gp.tileSize;
	}
}
