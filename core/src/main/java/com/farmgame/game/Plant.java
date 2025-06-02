package com.farmgame.game;

public class Plant {
    private final PlantType type;
    private float currentGrowthTime;
    private boolean watered;

    public Plant(PlantType type) {
        this.type = type;
        this.currentGrowthTime = 0f;
        this.watered = false;
    }

    public void water(){
        watered = true;
    }

    public void update(float delta){
        if (currentGrowthTime < type.getGrowthTime()){
            float speedMultiplier = watered ? 1.5f : 1.0f;
            currentGrowthTime += delta * speedMultiplier;
            if (currentGrowthTime > type.getGrowthTime()){
                currentGrowthTime = type.getGrowthTime();
            }
        }
        System.out.println("Growth time: " + currentGrowthTime + "/" + type.getGrowthTime());
    }

    public void resetWatered() {
        watered = false;
    }

    public boolean isReadyToHarvest(){
        return currentGrowthTime >= type.getGrowthTime();
    }

    public String getPlantName() {
        return type.getName();
    }

    public PlantType getType() {
        return type;
    }

    public float getGrowthPercent() {
        return currentGrowthTime / type.getGrowthTime();
    }

    public enum GrowthStage {
        PLANTED,
        GROWING,
        READY
    }

    public GrowthStage getStage() {
        float percent = getGrowthPercent();
        if (percent < 0.33f) return GrowthStage.PLANTED;
        else if (percent < 0.99f) return GrowthStage.GROWING;
        else return GrowthStage.READY;
    }

}
