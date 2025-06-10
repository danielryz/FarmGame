package com.farmgame.game_save;

import java.util.ArrayList;

public class SavedPlayer {
    public String name;
    public int money;
    public int level;
    public int exp;
    public ArrayList<SavedInventoryItem> inventory;

    public SavedPlayer() {
        inventory = new ArrayList<>();
    }

    public SavedPlayer(String name, int money, int level, int exp, ArrayList<SavedInventoryItem> inventory) {
        this.name = name;
        this.money = money;
        this.level = level;
        this.exp = exp;
        this.inventory = inventory != null ? inventory : new ArrayList<>();
    }
}
