package com.farmgame.player;

import java.util.ArrayList;
import java.util.List;

public class Inventory {

    private List<InventoryItem> items;
    private int capacity;

    public Inventory() {
        this(100);
    }
    public Inventory(int capacity) {
        this.capacity = capacity;
        items = new ArrayList<>();
    }

    public boolean addItem(InventoryItem newItem) {
        if (getUsedCapacity() + newItem.getQuantity() > capacity) {
            return false;
        }
        for (InventoryItem item : items) {
            if (item.getName().equalsIgnoreCase(newItem.getName())){
                item.incrementQuantity(newItem.getQuantity());
                return true;
            }
        }
        items.add(newItem);
        return true;
    }

    public void removeItem(String itemName, int quantityToRemove) {
        for (int i = 0; i < items.size(); i++){
            InventoryItem item = items.get(i);
            if (item.getName().equalsIgnoreCase(itemName)){
                item.decrementQuantity(quantityToRemove);
                if (item.getQuantity() <= 0) items.remove(i);
                break;
            }
        }
    }

    public List<InventoryItem> getItems() {
        return items;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public int getUsedCapacity() {
        int used = 0;
        for (InventoryItem item : items) {
            used += item.getQuantity();
        }
        return used;
    }

    public int getQuantity(String itemName) {
        for (InventoryItem item : items) {
            if (item.getName().equalsIgnoreCase(itemName)) return item.getQuantity();
        }
        return 0;
    }


    public void clearInventory() {
        items.clear();
    }
}
