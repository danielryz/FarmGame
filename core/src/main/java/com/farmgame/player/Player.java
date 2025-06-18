package com.farmgame.player;


import com.badlogic.gdx.utils.Timer;
import com.farmgame.game.AnimalDatabase;
import com.farmgame.game.MessageManager;
import com.farmgame.game.PlantDatabase;

import java.util.List;

public class Player {
    public static final int BASE_INVENTORY_CAPACITY = 100;
    private String name;
    private int money;
    private int level;
    private int exp;
    private int expToNextLevel;
    private final Inventory inventory;

    public Player(String name) {
        this.name = name;
        this.money = 100000;
        this.level = 1;
        this.exp = 0;
        this.expToNextLevel = 10;
        this.inventory = new Inventory(BASE_INVENTORY_CAPACITY);
    }

    public void addExp(int amount) {
        this.exp += amount;
        checkLevelUp();
    }

    private void checkLevelUp() {
        while (exp >= expToNextLevel) {
            level++;
            expToNextLevel += (int) (expToNextLevel * 1.5);

            List<String> unlockedPlants = PlantDatabase.getByLevel(level);
            List<String> unlockedAnimals = AnimalDatabase.getByLevel(level);


            float delay = 0.5f;
            Timer.schedule(new Timer.Task() {
                @Override
                public void run() {
                    MessageManager.success("Awansowałeś na poziom " + level + "!");
                }
            }, delay);
            delay += 0.5f;

            if(!unlockedAnimals.isEmpty()) {
                String msg = "Odblokowane zwierzęta: " + String.join(", ", unlockedAnimals);
                Timer.schedule(new Timer.Task() {
                    @Override
                    public void run() {
                        MessageManager.success(msg);
                    }
                }, delay);
                delay += 0.5f;
            }

            if(!unlockedPlants.isEmpty()) {
                String msg = "Odblokowane nasiona: " + String.join(", ", unlockedPlants);
                Timer.schedule(new Timer.Task() {
                    @Override
                    public void run() {
                        MessageManager.success(msg);
                    }
                }, delay);
            }
        }
    }


    public String getName() {
        return name;
    }


    public int getMoney() {
        return money;
    }

    public void addMoney(int amount) {
        this.money += amount;
    }

    public int getLevel() {
        return level;
    }

    public int getExp() {
        return exp;
    }

    public int getExpToNextLevel() {
        return expToNextLevel;
    }

    public Inventory getPlayerInventory() {
        return inventory;
    }

    public int getInventoryCapacity() {
        return inventory.getCapacity();
    }

    public int getUsedInventorySpace() {
        return inventory.getUsedCapacity();
    }

    public void increaseInventoryCapacity(int amount) {
        inventory.setCapacity(inventory.getCapacity() + amount);
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public void setExp(int exp) {
        this.exp = exp;
    }

}
