package com.gvi.project.models.objects;

import com.gvi.project.GamePanel;
import com.gvi.project.GameState;
import com.gvi.project.models.entities.Player;
import com.gvi.project.models.sprite_sheets.SpriteSheet;

public class OBJ_EndScreenTrigger extends SuperObject {

    public OBJ_EndScreenTrigger() {
        initImageLoad();
        setCollisionBox();
    }

    private void initImageLoad(){
        SpriteSheet sheet = new SpriteSheet("/sprites/tilemaps/damp-dungeons/Tiles/light_samples");
        this.sprite = sheet.getSprite("yellow","top_to_bottom");
        this.sprite.imageHeight = 2;
    }

    private void setCollisionBox(){
        this.collisionBox.setHeight(16 * 3);
        this.collisionBox.setWidth(16 * 3 * 2);
    }

    @Override
    public void onStep(Player player, GamePanel gp, int objIndex) {
    }
}


