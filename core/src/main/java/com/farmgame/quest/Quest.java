package com.farmgame.quest;

import com.farmgame.player.Player;

public class Quest {
    private final String name;
    private final String itemName;
    private final int requiredQuantity;
    private final int rewardMoney;
    private final int rewardExp;
    private boolean claimed;

    public Quest(String name, String itemName, int requiredQuantity, int rewardMoney, int rewardExp) {
        this.name = name;
        this.itemName = itemName;
        this.requiredQuantity = requiredQuantity;
        this.rewardMoney = rewardMoney;
        this.rewardExp = rewardExp;
        this.claimed = false;
    }

    public String getName() {
        return name;
    }

    public String getItemName() {
        return itemName;
    }

    public int getRequiredQuantity() {
        return requiredQuantity;
    }

    public int getRewardMoney() {
        return rewardMoney;
    }

    public int getRewardExp() {
        return rewardExp;
    }

    public boolean isClaimed() {
        return claimed;
    }

    public boolean isCompleted(Player player) {
        return player.getPlayerInventory().getQuantity(itemName) >= requiredQuantity;
    }

    public boolean claim(Player player) {
        if (claimed || !isCompleted(player)) return false;
        player.getPlayerInventory().removeItem(itemName, requiredQuantity);
        player.addMoney(rewardMoney);
        player.addExp(rewardExp);
        claimed = true;
        return true;
    }

    public void setClaimed(boolean claimed) {
        this.claimed = claimed;
    }
}
