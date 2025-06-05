package com.farmgame.game;

import java.util.Set;

public class AnimalType {
    private final String name;
    private final int cost;
    private final Set<String> feedSet;
    private final String productName;
    private final float productTime;
    private final int sellPrice;

    public AnimalType(String name, int cost, Set<String> feedSet, String productName, float productTime, int sellPrice) {
        this.name = name;
        this.cost = cost;
        this.feedSet = feedSet;
        this.productName = productName;
        this.productTime = productTime;
        this.sellPrice = sellPrice;
    }
    public String getName() {
        return name;
    }
    public int getCost() {
        return cost;
    }
    public Set<String> getFeedSet() {
        return feedSet;
    }
    public String getProductName() {
        return productName;
    }
    public float getProductTime() {
        return productTime;
    }
    public int getSellPrice() {
        return sellPrice;
    }
}
