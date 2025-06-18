package com.farmgame.quest;

import com.farmgame.player.Player;

import java.util.ArrayList;
import java.util.List;

public class QuestManager {
    private final List<Quest> quests = new ArrayList<>();

    public void addQuest(Quest quest) {
        quests.add(quest);
    }

    public List<Quest> getQuests() {
        return quests;
    }

    public void clear() {
        quests.clear();
    }

    public void loadFromDatabase() {
        quests.clear();
        for (Quest q : QuestDatabase.getAll()) {
            quests.add(new Quest(q.getName(), q.getItemName(), q.getRequiredQuantity(), q.getRewardMoney(), q.getRewardExp()));
        }
    }

    public void checkQuests(Player player) {
    }
}
