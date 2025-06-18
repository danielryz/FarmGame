package com.farmgame.game;

import com.badlogic.gdx.scenes.scene2d.ui.Table;

public class Animal {

    public enum ProductState {
        NOT_FED, PRODUCTION, READY
    }

    private final AnimalType type;
    private ProductState productState;
    private float timeToNextProduct;
    private DifficultyManager difficultyManager;
    private int productCount;


    public Animal(AnimalType type) {
        this(type, new DifficultyManager());
    }

    public Animal(AnimalType type, DifficultyManager difficultyManager) {
        this.type = type;
        this.timeToNextProduct = 0f;
        this.productState = ProductState.NOT_FED;
        this.difficultyManager = difficultyManager;
        this.productCount = 0;
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

    public boolean fed(String plantName, int quantity){
        if(productState != ProductState.NOT_FED || quantity <= 0){
            System.out.println("Nie można nakarmić.");
            return false;
        }

        if (type.getFeedSet().contains(plantName)) {
            productState = ProductState.PRODUCTION;
            productCount = quantity;
            timeToNextProduct = (type.getProductTime() * difficultyManager.getTimeMultiplier()) / quantity;
            System.out.println("Nakarmiono zwierzę rośliną: " + plantName + " x" + quantity);
            return true;
        }
        return false;
    }

    public int collectProduct(){
        if(productState == ProductState.READY){
            productState = ProductState.NOT_FED;
            int produced = productCount > 0 ? productCount : 1;
            productCount = 0;
            System.out.println("Zebrano produkt x" + produced);
            return produced;
        }
        return 0;
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
    public int getProductCount() {
        return productCount;
    }

    public void setProductCount(int productCount) {
        this.productCount = productCount;
    }
}
