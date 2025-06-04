package com.farmgame.game;

import java.util.HashMap;
import java.util.Map;

public class Inventory {
    private final Map<String, Integer> items = new HashMap<>();

    public void addItem(String plantName, int amount) {
        items.put(plantName, items.getOrDefault(plantName, 0) + amount);
    }

    public boolean removeItem(String plantName, int amount) {
        int currentAmount = items.getOrDefault(plantName, 0);
        int updated = currentAmount - amount;
        if (updated <= 0) {
            items.remove(plantName);
        }else {
            items.put(plantName, updated);
            return true;
        }
        return false;
    }

    public int getAmount(String plantName) {
        return items.getOrDefault(plantName, 0);
    }

    public void clearItem() {
        items.clear();
    }

    public Map<String, Integer> getItems() {
        return items;
    }
}
