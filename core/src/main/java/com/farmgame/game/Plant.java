package com.farmgame.game;

public class Plant {
    private final PlantType type;
    private float currentGrowthTime;
    private boolean watered;
    private boolean autoWatered;
    private boolean fertilized;
    private float fertilizerTimer;
    private DifficultyManager difficultyManager;

    public Plant(PlantType type) {
        this(type, new DifficultyManager());
    }

    public Plant(PlantType type, DifficultyManager difficultyManager) {
        this.type = type;
        this.currentGrowthTime = 0f;
        this.watered = false;
        this.autoWatered = false;
        this.fertilized = false;
        this.fertilizerTimer = 0f;
        this.difficultyManager = difficultyManager;
    }

    public void water(){
        watered = true;
    }

    public boolean isWatered() {
        return watered;
    }

    public boolean isAutoWatered() { return autoWatered; }

    public float getFertilizerTimer() { return fertilizerTimer; }

    public void setAutoWatered(boolean autoWatered) { this.autoWatered = autoWatered; }

    public void applyFertilizer(float duration) { if (duration > fertilizerTimer) fertilizerTimer = duration; }

    public boolean isFertilized() { return fertilized; }


    public void update(float delta) {
        float adjustedGrowthTime = type.getGrowthTime() * difficultyManager.getTimeMultiplier();
        if (currentGrowthTime < adjustedGrowthTime) {
            float wateringMultiplier = (watered || autoWatered) ? 1.5f : 1.0f;
            float fertilizerMultiplier = fertilizerTimer > 0f ? 1.5f : 1.0f;

            currentGrowthTime += delta * wateringMultiplier * fertilizerMultiplier;

            if (fertilizerTimer > 0f) {
                fertilizerTimer -= delta;
                if (fertilizerTimer < 0f) fertilizerTimer = 0f;
            }

            if (currentGrowthTime > adjustedGrowthTime) {
                currentGrowthTime = adjustedGrowthTime;
            }
        }
    }

    public void resetWatered() {
        watered = false;
        fertilized = false;
    }

    public boolean isReadyToHarvest(){
        float adjustedGrowthTime = type.getGrowthTime() * difficultyManager.getTimeMultiplier();
        return currentGrowthTime >= adjustedGrowthTime;
    }

    public String getPlantName() {
        return type.getName();
    }

    public PlantType getType() {
        return type;
    }

    public float getGrowthPercent() {
        float adjustedGrowthTime = type.getGrowthTime() * difficultyManager.getTimeMultiplier();
        return currentGrowthTime / adjustedGrowthTime;
    }

    public float getTimeRemaining() {
        float adjustedGrowthTime = type.getGrowthTime() * difficultyManager.getTimeMultiplier();
        return Math.max(0, adjustedGrowthTime - currentGrowthTime);
    }

    public void setTimeRemaining(float timeRemaining) {
        currentGrowthTime = timeRemaining;
        float adjustedGrowthTime = type.getGrowthTime() * difficultyManager.getTimeMultiplier();
        currentGrowthTime = adjustedGrowthTime - timeRemaining;
        if (currentGrowthTime < 0f) {
            currentGrowthTime = 0f;
        } else if (currentGrowthTime > adjustedGrowthTime) {
            currentGrowthTime = adjustedGrowthTime;
        }
    }

    public void setWatered(boolean isWatered) {
        this.watered = isWatered;
    }
    public void setFertilized(boolean isFertilized) {
        this.fertilized = isFertilized;
    }

    public enum GrowthStage {
        PLANTED,
        GROWING,
        READY
    }

    public float getFertilizerTimeRemaining() {
        return fertilizerTimer;
    }

    public GrowthStage getStage() {
        float percent = getGrowthPercent();
        if (percent < 0.33f) return GrowthStage.PLANTED;
        else if (percent < 1.00f) return GrowthStage.GROWING;
        else return GrowthStage.READY;
    }

}
