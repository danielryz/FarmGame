package com.farmgame.player;


public class Player {
    private final String name;
    private int money;
    private int level;
    private int exp;
    private int expToNextLevel;
    private final Inventory inventory;

    public Player(String name) {
        this.name = name;
        this.money = 99999;
        this.level = 1;
        this.exp = 0;
        this.expToNextLevel = 10;
        this.inventory = new Inventory();
    }

    public void addExp(int amount) {
        this.exp += amount;
        checkLevelUp();
    }

    private void checkLevelUp() {
        while (exp >= expToNextLevel) {
            level++;
            expToNextLevel += (int) (expToNextLevel * 1.5);
            System.out.println("Awansowałeś na poziom " + level + "!");
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
}
