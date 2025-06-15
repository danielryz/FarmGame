package com.farmgame.game;

import com.badlogic.gdx.graphics.Color;

import java.util.*;
import java.util.stream.Collectors;

public class PlantDatabase {
    private static final List<PlantType> plantTypes = new ArrayList<>();

    static {
        plantTypes.add(new PlantType("Trawa", 15f, new Color(0f, 0.2f, 0f, 1f), 10, 12, 1));
        plantTypes.add(new PlantType("Marchew", 20f, Color.ORANGE, 15, 20, 2));
        plantTypes.add(new PlantType("Ziemniak", 30f, Color.BROWN, 10, 16, 3));
        plantTypes.add(new PlantType("Pszenica", 40f, Color.TAN, 5, 14, 4));
        plantTypes.add(new PlantType("Pomidor", 25f, Color.RED, 20, 24, 5));
        plantTypes.add(new PlantType("Kukurydza", 35f, Color.YELLOW, 10, 17, 6));
        plantTypes.add(new PlantType("Cebula", 22f, Color.LIGHT_GRAY, 30, 34, 7));
        plantTypes.add(new PlantType("Czosnek", 28f, Color.SKY, 32, 36, 8));
        plantTypes.add(new PlantType("Sałata", 18f, Color.LIME, 25, 29, 9));
        plantTypes.add(new PlantType("Kapusta", 26f, Color.FOREST, 24, 29, 10));
        plantTypes.add(new PlantType("Groszek", 24f, Color.GREEN, 26, 32, 11));
        plantTypes.add(new PlantType("Dynia", 45f, Color.ORANGE.cpy().lerp(Color.BROWN, 0.3f), 36, 44, 12));
        plantTypes.add(new PlantType("Truskawka", 32f, Color.SCARLET, 32, 40, 13));
        plantTypes.add(new PlantType("Jagoda", 38f, Color.ROYAL, 30, 38, 14));
        plantTypes.add(new PlantType("Chili", 29f, Color.FIREBRICK, 100, 120, 15));
    }

    public static List<PlantType> getAll() {
        return plantTypes;
    }

    public static PlantType getByName(String name) {
        return plantTypes.stream()
            .filter(p -> p.getName().equalsIgnoreCase(name))
            .findFirst()
            .orElse(null);
    }

    public static List<String> getByLevel(int level) {
        return plantTypes.stream()
            .filter(a -> a.getRequiredLevel() == level)
            .map(PlantType::getName)
            .collect(Collectors.toList());
    }
}
