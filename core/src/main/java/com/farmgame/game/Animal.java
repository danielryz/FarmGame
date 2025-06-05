package com.farmgame.game;

public class Animal {
    private final AnimalType type;
    private boolean isFed;
    private float timeToNextProduct;


    public Animal(AnimalType type) {
        this.type = type;
        this.isFed = false;
        this.timeToNextProduct = 0f;
    }

    public void update(float delta){
        if(isFed){
            timeToNextProduct -= delta;
            if(timeToNextProduct <= 0f){
                isFed = false;
                timeToNextProduct = 0f;
            }
        }
    }

    public boolean feed(String plantName){
        if(type.getFeedSet().contains(plantName)){
            isFed = true;
            timeToNextProduct = type.getProductTime();
            return true;
        }
        return false;
    }

    public AnimalType getType() {
        return type;
    }

    public boolean isFed() {
        return isFed;
    }

    public float getTimeToNextProduct() {
        return timeToNextProduct;
    }

}
