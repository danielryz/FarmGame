package com.farmgame.player;

public class InventoryItem {
    private String name;
    private int quantity;
    private int sellPrice;

    public InventoryItem(String name, int quantity, int sellPrice) {
        this.name = name;
        this.quantity = quantity;
        this.sellPrice = sellPrice;
    }

    public String getName() {
        return name;
    }
    public int getQuantity() {
        return quantity;
    }
    public int getSellPrice() {
        return sellPrice;
    }

    public void setName(String name){
        this.name = name;
    }
    public void setSellPrice(int sellPrice){
        this.sellPrice = sellPrice;
    }
    public void incrementQuantity(int amount){
        this.quantity += amount;
    }

    public void decrementQuantity(int amount){
        this.quantity -= amount;
        if(this.quantity <= 0) this.quantity = 0;
    }

    public void setQuantity(int quantity){
        this.quantity = quantity;
    }
}
