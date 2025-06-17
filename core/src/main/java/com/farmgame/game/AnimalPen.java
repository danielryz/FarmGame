package com.farmgame.game;

import com.farmgame.player.Player;
import com.farmgame.game.AnimalType;
import java.util.ArrayList;
import java.util.List;

public class AnimalPen {

    public enum State {
        BLOCKED, EMPTY, OCCUPIED
    }

    private final int x;
    private final int y;

    private State state;
    private List<Animal> animals;
    private int capacity = 1;
    private static final int MAX_CAPACITY = 9;
    private AnimalType allowedType = null;
    private DifficultyManager difficultyManager;

    public AnimalPen(int x, int y) {
        this(x, y, new DifficultyManager());
    }

    public AnimalPen(int x, int y, DifficultyManager difficultyManager) {
        this.x = x;
        this.y = y;
        this.state = State.BLOCKED;
        this.animals = new ArrayList<>();
        this.allowedType = null;
        this.difficultyManager = difficultyManager;
    }

    public State getState() {
        return state;
    }

    public List<Animal> getAnimals() {
        return animals;
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
        if (isBlocked() || isFull()) {
            System.out.println("Zagroda jest pełna!");
            return false;
        }

        if (allowedType != null && !allowedType.equals(animal.getType())) {
            System.out.println("Ta zagroda przyjmuje tylko zwierzęta typu: " + allowedType.getName());
            return false;
        }

        Animal animalWithDifficulty = new Animal(animal.getType(), difficultyManager);
        this.animals.add(animalWithDifficulty);
        this.allowedType = animal.getType();
        this.state = State.OCCUPIED;

        int adjustedCost = (int)(animal.getType().getCost() / difficultyManager.getMoneyMultiplier());
        player.addMoney(-adjustedCost);
        player.addExp(5);
        return true;
    }

    public void removeAnimal(Animal animal){
        if (animals.remove(animal) && animals.isEmpty()) {
            this.state = State.EMPTY;
            this.allowedType = null;
        }
    }

    public void update(float delta){
        for (Animal animal : animals) {
            animal.update(delta);
        }
    }

    public DifficultyManager getDifficultyManager() {
        return difficultyManager;
    }

    public void setDifficultyManager(DifficultyManager difficultyManager) {
        this.difficultyManager = difficultyManager;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public void increaseCapacity() {
        if (this.capacity < MAX_CAPACITY) {
            this.capacity++;
        }
    }

    public boolean isMaxCapacity() {
        return capacity >= MAX_CAPACITY;
    }

    public AnimalType getAllowedType() {
        return allowedType;
    }

    public boolean canAcceptType(AnimalType type) {
        return allowedType == null || allowedType.equals(type);
    }

    public boolean isFull() {
        return animals.size() >= capacity;
    }

    public void setAnimals(List<Animal> animals) {
        this.animals = new ArrayList<>(animals);
        if (this.animals.isEmpty()) {
            this.state = State.EMPTY;
            this.allowedType = null;
        } else {
            this.state = State.OCCUPIED;
            this.allowedType = this.animals.get(0).getType();
        }
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
