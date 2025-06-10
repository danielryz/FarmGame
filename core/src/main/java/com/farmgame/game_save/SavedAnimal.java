package com.farmgame.game_save;

public class SavedAnimal {
    public String typeName;
    public String productState; // NOT_FED, PRODUCTION, READY
    public float timeToNextProduct;
    public String color; // jako hex string

    public SavedAnimal() {}

    public SavedAnimal(String typeName, String productState, float timeToNextProduct, String color) {
        this.typeName = typeName;
        this.productState = productState;
        this.timeToNextProduct = timeToNextProduct;
        this.color = color;
    }
}
