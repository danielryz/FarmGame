package com.farmgame.quest;

import java.util.ArrayList;
import java.util.List;

public class QuestDatabase {
    private static final List<Quest> quests = new ArrayList<>();

    static {
        quests.add(new Quest("Dostarcz Marchew", "Marchew", 5, 50, 10));
        quests.add(new Quest("Dostarcz Ziemniaki", "Ziemniak", 8, 60, 15));
        quests.add(new Quest("Dostarcz Mleko", "Mleko", 3, 80, 20));
        quests.add(new Quest("Dostarcz Jajka", "Jajko", 10, 100, 25));
        quests.add(new Quest("Dostarcz Wełnę", "Wełna", 4, 90, 22));
    }

    public static List<Quest> getAll() {
        return quests;
    }
}
