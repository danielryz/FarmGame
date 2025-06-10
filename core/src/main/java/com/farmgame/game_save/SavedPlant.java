package com.farmgame.game_save;

public class SavedPlant {
    public String typeName;
    public float timeRemaining;
    public boolean isWatered;
    public String color; // jako hex string

    public SavedPlant() {}

    public SavedPlant(String typeName, float timeRemaining, boolean isWatered, String color) {
        this.typeName = typeName;
        this.timeRemaining = timeRemaining;
        this.isWatered = isWatered;
        this.color = color;
    }
}
