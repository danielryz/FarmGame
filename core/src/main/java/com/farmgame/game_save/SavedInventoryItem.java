package com.farmgame.game_save;

public class SavedInventoryItem {
    public String name;
    public int quantity;
    public int sellPrice;

    public SavedInventoryItem() {}

    public SavedInventoryItem(String name, int quantity, int sellPrice) {
        this.name = name;
        this.quantity = quantity;
        this.sellPrice = sellPrice;
    }
}
