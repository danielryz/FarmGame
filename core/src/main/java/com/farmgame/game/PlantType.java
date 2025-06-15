package com.farmgame.game;

import com.badlogic.gdx.graphics.Color;

public class PlantType {
    private final String name;
    private final float growthTime;
    private final Color color;
    private final int seedPrice;
    private final int sellPrice;
    private final int requiredLevel;

    public PlantType(String name, float growthTime, Color color, int seedPrice, int sellPrice, int requiredLevel) {
        this.name = name;
        this.growthTime = growthTime;
        this.color = color;
        this.seedPrice = seedPrice;
        this.sellPrice = sellPrice;
        this.requiredLevel = requiredLevel;
    }

    public String getName() {
        return name;
    }

    public float getGrowthTime() {
        return growthTime;
    }

    public Color getColor() {
        return color;
    }

    public int getSeedPrice() {
        return seedPrice;
    }

    public int getSellPrice() {
        return sellPrice;
    }

    public int getRequiredLevel() {
        return requiredLevel;
    }

    public static PlantType getByName(String name) {
        return PlantDatabase.getByName(name);
    }

    @Override
    public String toString() {
        return name;
    }
}
