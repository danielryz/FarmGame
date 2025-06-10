package com.farmgame.game;

public class Animal {

    public enum ProductState {
        NOT_FED, PRODUCTION, READY
    }

    private final AnimalType type;
    private ProductState productState;
    private float timeToNextProduct;


    public Animal(AnimalType type) {
        this.type = type;
        this.timeToNextProduct = 0f;
        this.productState = ProductState.NOT_FED;
    }

    public void update(float delta){
        if(productState == ProductState.PRODUCTION){
            timeToNextProduct -= delta;
            if(timeToNextProduct <= 0f){
                timeToNextProduct = 0f;
                productState = ProductState.READY;
            }
        }
    }

    public boolean fed(String plantName){
        if(productState != ProductState.NOT_FED){
            System.out.println("Nie można nakarmić.");
            return false;
        }

        if (type.getFeedSet().contains(plantName)) {
            productState = ProductState.PRODUCTION;
            timeToNextProduct = type.getProductTime();
            System.out.println("Nakarmiono zwierzę rośliną: " + plantName);
            return true;
        }
        return false;
    }

    public boolean collectProduct(){
        if(productState == ProductState.READY){
            productState = ProductState.NOT_FED;
            System.out.println("Zebrano produkt.");
            return true;
        }
        return false;
    }

    public AnimalType getType() {
        return type;
    }

    public ProductState getProductState() {
        return productState;
    }

    public float getTimeToNextProduct() {
        return timeToNextProduct;
    }

    public void setProductState(ProductState productState) {
        this.productState = productState;
    }

    public void setTimeToNextProduct(float timeToNextProduct) {
        this.timeToNextProduct = timeToNextProduct;
    }
}
