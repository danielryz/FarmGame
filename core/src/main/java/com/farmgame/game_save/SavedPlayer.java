package com.farmgame.game_save;

import java.util.ArrayList;

public class SavedPlayer {
    public String name;
    public int money;
    public int level;
    public int exp;
    public int inventoryCapacity;
    public ArrayList<SavedInventoryItem> inventory;

    public SavedPlayer() {
        inventory = new ArrayList<>();
    }

    public SavedPlayer(String name, int money, int level, int exp, int inventoryCapacity, ArrayList<SavedInventoryItem> inventory) {
        this.name = name;
        this.money = money;
        this.level = level;
        this.exp = exp;
        this.inventoryCapacity = inventoryCapacity;
        this.inventory = inventory != null ? inventory : new ArrayList<>();
    }
}
