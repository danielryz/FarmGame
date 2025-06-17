package com.farmgame.game_save;

public class SavedPlant {
    public String typeName;
    public float timeRemaining;
    public boolean isWatered;
    public boolean isAutoWatered;
    public float fertilizerTime;
    public String color;

    public SavedPlant() {}

    public SavedPlant(String typeName, float timeRemaining, boolean isWatered, String color, boolean isAutoWatered, float fertilizerTime) {
        this.typeName = typeName;
        this.timeRemaining = timeRemaining;
        this.isWatered = isWatered;
        this.color = color;
        this.isAutoWatered = isAutoWatered;
        this.fertilizerTime = fertilizerTime;
    }
}
