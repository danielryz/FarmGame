package com.farmgame.game;

import java.util.*;

public class PlantDatabase {
    private static final List<PlantType> plantTypes = new ArrayList<>();

    static {
        plantTypes.add(new PlantType("Carrot", 20f));
        plantTypes.add(new PlantType("Potato", 30f));
        plantTypes.add(new PlantType("Wheat", 40f));
        plantTypes.add(new PlantType("Tomato", 25f));
        plantTypes.add(new PlantType("Corn", 35f));
        plantTypes.add(new PlantType("Onion", 22f));
        plantTypes.add(new PlantType("Garlic", 28f));
        plantTypes.add(new PlantType("Lettuce", 18f));
        plantTypes.add(new PlantType("Cabbage", 26f));
        plantTypes.add(new PlantType("Peas", 24f));
        plantTypes.add(new PlantType("Pumpkin", 45f));
        plantTypes.add(new PlantType("Strawberry", 32f));
        plantTypes.add(new PlantType("Blueberry", 38f));
        plantTypes.add(new PlantType("Chili", 29f));
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
}
