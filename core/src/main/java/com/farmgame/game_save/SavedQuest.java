package com.farmgame.game_save;

public class SavedQuest {
    public String name;
    public String itemName;
    public int requiredQuantity;
    public int rewardMoney;
    public int rewardExp;
    public boolean claimed;

    public SavedQuest() {}

    public SavedQuest(String name, String itemName, int requiredQuantity, int rewardMoney, int rewardExp, boolean claimed) {
        this.name = name;
        this.itemName = itemName;
        this.requiredQuantity = requiredQuantity;
        this.rewardMoney = rewardMoney;
        this.rewardExp = rewardExp;
        this.claimed = claimed;
    }
}
