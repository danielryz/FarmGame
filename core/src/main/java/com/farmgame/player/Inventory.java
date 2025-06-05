package com.farmgame.player;

import java.util.ArrayList;
import java.util.List;

public class Inventory {

    private List<InventoryItem> items;

    public Inventory() {
        items = new ArrayList<>();
    }

    public void addItem(InventoryItem newItem) {
        for (InventoryItem item : items) {
            if (item.getName().equalsIgnoreCase(newItem.getName())){
                item.incrementQuantity(newItem.getQuantity());
                return;
            }
        }
        items.add(newItem);
    }

    public void removeItem(String itemName, int quantityToRemove) {
        for (int i = 0; i < items.size(); i++){
            InventoryItem item = items.get(i);
            if (item.getName().equalsIgnoreCase(itemName)){
                item.decrementQuantity(quantityToRemove);
                if (item.getQuantity() <= 0) items.remove(i);
            }
            break;
        }
    }

    public List<InventoryItem> getItems() {
        return items;
    }

    public int getQuantity(String itemName) {
        for (InventoryItem item : items) {
            if (item.getName().equalsIgnoreCase(itemName)) return item.getQuantity();
        }
        return 0;
    }


}
