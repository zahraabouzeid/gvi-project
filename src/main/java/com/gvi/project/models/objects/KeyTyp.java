package com.gvi.project.models.objects;

public enum KeyTyp {
    COPPER("key_copper"),
    IRON("key_iron"),
    GOLDEN("key_gold");

    private final String spriteGroupID;

    KeyTyp (String spriteGroupID){
        this.spriteGroupID = spriteGroupID;
    }

    public String getSpriteGroupID(){
        return spriteGroupID;
    }

    public String getName(){
        String output = "";
        output = spriteGroupID.substring(spriteGroupID.indexOf("_") + 1);
        return output;
    }
}