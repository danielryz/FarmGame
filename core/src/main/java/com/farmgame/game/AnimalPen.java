package com.farmgame.game;

import com.farmgame.player.Player;

public class AnimalPen {

    public enum State {
        EMPTY, OCCUPIED
    }

    private final int x;
    private final int y;

    private State state;
    private Animal currentAnimal;

    public AnimalPen(int x, int y) {
        this.x = x;
        this.y = y;
        this.state = State.EMPTY;
        this.currentAnimal = null;
    }

    public State getState() {
        return state;
    }

    public Animal getCurrentAnimal() {
        return currentAnimal;
    }

   public boolean placeAnimal(Animal animal, Player player){
        if(state == State.EMPTY){
            this.currentAnimal = animal;
            this.state = State.OCCUPIED;
            player.addMoney(-animal.getType().getCost());
            player.addExp(5);
            return true;
        }
        System.out.println("Zagroda jest zajęta!");
        return false;
   }

   public void removeAnimal(){
       this.currentAnimal = null;
       this.state = State.EMPTY;
   }

   public void update(float delta){
        if(currentAnimal != null){
            currentAnimal.update(delta);
        }
   }

   public int getX() {
        return x;
    }

    public int getY(){
        return y;
    }
}

