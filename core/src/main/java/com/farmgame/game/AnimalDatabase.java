package com.farmgame.game;

import java.util.*;

public class AnimalDatabase {
    private static final Map<String, AnimalType> ANIMAL_TYPE_MAP = new HashMap<>();

    static {
        AnimalType cow = new AnimalType("Krowa", 200, Set.of("Trawa", "Pszenica", "Sałata", "Kapusta"), "Mleko", 40f, 40);
        AnimalType chicken = new AnimalType("Kura", 100, Set.of("Pszenica", "Kukurydza"), "Jajko", 20f, 30);
        AnimalType sheep = new AnimalType("Owca", 300, Set.of("Trawa"), "Wełna", 50f, 50);

        ANIMAL_TYPE_MAP.put(cow.getName(), cow);
        ANIMAL_TYPE_MAP.put(chicken.getName(), chicken);
        ANIMAL_TYPE_MAP.put(sheep.getName(), sheep);
    }

    public static List<AnimalType> getAll() {
        return new ArrayList<>(ANIMAL_TYPE_MAP.values());
    }

    public static AnimalType getByName(String name) {
        return ANIMAL_TYPE_MAP.getOrDefault(name, null);
    }
}
