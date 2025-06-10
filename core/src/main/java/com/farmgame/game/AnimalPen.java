package com.farmgame.game;

import com.farmgame.player.Player;

public class AnimalPen {

    public enum State {
        BLOCKED, EMPTY, OCCUPIED
    }

    private final int x;
    private final int y;

    private State state;
    private Animal currentAnimal;

    public AnimalPen(int x, int y) {
        this.x = x;
        this.y = y;
        this.state = State.BLOCKED;
        this.currentAnimal = null;
    }

    public State getState() {
        return state;
    }

    public Animal getCurrentAnimal() {
        return currentAnimal;
    }

    public void unlock() {
        if (state == State.BLOCKED) {
            state = State.EMPTY;
        }
    }

    public void block() {
        if (state != State.BLOCKED) {
            state = State.BLOCKED;
        }
    }


    public boolean placeAnimal(Animal animal, Player player){
        if(state == State.EMPTY){
            this.currentAnimal = animal;
            this.state = State.OCCUPIED;
            player.addMoney(-animal.getType().getCost());
            player.addExp(5);
            return true;
        } else {
            System.out.println("Zagroda jest zajęta!");
            return false;
        }
   }

   public void removeAnimal(){
        if (state == State.OCCUPIED) {
            this.currentAnimal = null;
            this.state = State.EMPTY;
        }
   }

   public void update(float delta){
        if(currentAnimal != null){
            currentAnimal.update(delta);
        }
   }

    public void setCurrentAnimal(Animal animal) {
        this.currentAnimal = animal;
    }

    public void setState(State state) {
        this.state = state;
    }

    public boolean isBlocked(){
        return state == State.BLOCKED;
   }

   public int getX() {
        return x;
    }

    public int getY(){
        return y;
    }
}

