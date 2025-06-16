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
    private DifficultyManager difficultyManager;

    public AnimalPen(int x, int y) {
        this(x, y, new DifficultyManager());
    }

    public AnimalPen(int x, int y, DifficultyManager difficultyManager) {
        this.x = x;
        this.y = y;
        this.state = State.BLOCKED;
        this.currentAnimal = null;
        this.difficultyManager = difficultyManager;
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
        return placeAnimal(animal, player, this.difficultyManager);
    }

    public boolean placeAnimal(Animal animal, Player player, float difficultyMultiplier){
        DifficultyManager tempManager = new DifficultyManager();
        tempManager.setDifficultyMultiplier(difficultyMultiplier);
        return placeAnimal(animal, player, tempManager);
    }

    public boolean placeAnimal(Animal animal, Player player, DifficultyManager difficultyManager){
        if(state == State.EMPTY){
            Animal animalWithDifficulty = new Animal(animal.getType(), difficultyManager);

            this.currentAnimal = animalWithDifficulty;
            this.state = State.OCCUPIED;

            int adjustedCost = (int)(animal.getType().getCost() / difficultyManager.getMoneyMultiplier());
            player.addMoney(-adjustedCost);
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

   public DifficultyManager getDifficultyManager() {
        return difficultyManager;
   }

   public void setDifficultyManager(DifficultyManager difficultyManager) {
        this.difficultyManager = difficultyManager;
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
