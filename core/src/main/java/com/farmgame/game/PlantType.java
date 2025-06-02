package com.farmgame.game;

public class PlantType {
    private final String name;
    private final float growthTime;

    public PlantType(String name, float growthTime) {
        this.name = name;
        this.growthTime = growthTime;
    }

    public String getName() {
        return name;
    }

    public float getGrowthTime() {
        return growthTime;
    }

    @Override
    public String toString() {
        return name;
    }
}
