package com.farmgame.game;

import com.badlogic.gdx.graphics.Color;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class AnimalDatabase {
    private static final List<AnimalType> animalTypes = new ArrayList<>();

    static {
        animalTypes.add(new AnimalType("Krowa", 200, Set.of("Trawa", "Pszenica", "Sałata", "Kapusta"), "Mleko", 40f, 40, Color.BLACK, 1));
        animalTypes.add(new AnimalType("Kura", 100, Set.of("Pszenica", "Kukurydza"), "Jajko", 20f, 30, Color.YELLOW, 5));
        animalTypes.add(new AnimalType("Owca", 300, Set.of("Trawa"), "Wełna", 50f, 50, Color.WHITE, 10));
        animalTypes.add(new AnimalType("Świnia", 600, Set.of("Marchew", "Ziemniak", "Pszenica", "Kukurydza", "Sałata", "Kapusta", "Dynia"), "Mięso", 120f, 120, Color.PINK, 15));
    }

    public static List<AnimalType> getAll() {
        return animalTypes;
    }

    public static AnimalType getByName(String name) {
        return animalTypes.stream()
            .filter(a -> a.getName().equalsIgnoreCase(name))
            .findFirst()
            .orElse(null);
    }

    public static List<String> getByLevel(int level) {
        return animalTypes.stream()
            .filter(a -> a.getRequiredLevel() == level)
            .map(AnimalType::getName)
            .collect(Collectors.toList());
    }
}
