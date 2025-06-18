package com.farmgame.game;

import com.badlogic.gdx.graphics.Color;
import com.farmgame.game_save.SavedAnimal;
import com.farmgame.game_save.SavedInventoryItem;

import java.util.Random;

public class Weather {

    public void setCurrentWeather(String currentWeather) {
        for (Condition condition : Condition.values()) {
            if (condition.getDisplayName().equalsIgnoreCase(currentWeather)) {
                this.currentCondition = condition;
                return;
            }
        }
        System.out.println("Nie znaleziono warunku: " + currentWeather);
        this.currentCondition = Condition.SUNNY;
        System.out.println("Ustawiono warunek: " + this.currentCondition.getDisplayName());
    }

    public void setTimeUntilChange(float timeUntilChange) {
        this.timeUntilChange = timeUntilChange;
    }

    public enum Condition {
        SUNNY(1.0f, new Color(1f, 1f, 1f, 1f), "Słonecznie"),
        CLOUDY(0.8f, new Color(0.8f, 0.8f, 0.8f, 1f), "Pochmurno"),
        RAINY(1.5f, new Color(0.6f, 0.6f, 0.8f, 1f), "Deszczowo"),
        STORMY(0.5f, new Color(0.4f, 0.4f, 0.5f, 1f), "Burzowo");

        private final float growthMultiplier;
        private final Color ambientColor;
        private final String displayName;

        Condition(float growthMultiplier, Color ambientColor, String displayName) {
            this.growthMultiplier = growthMultiplier;
            this.ambientColor = ambientColor;
            this.displayName = displayName;
        }

        public float getGrowthMultiplier() {
            return growthMultiplier;
        }

        public Color getAmbientColor() {
            return ambientColor;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    private Condition currentCondition;
    private float timeUntilChange;
    private final Random random;
    private final float minDuration = 60f;
    private final float maxDuration = 180f;

    public Weather() {
        random = new Random();
        setRandomWeather();
    }

    public void update(float delta) {
        timeUntilChange -= delta;
        if (timeUntilChange <= 0) {
            setRandomWeather();
        }
    }

    private void setRandomWeather() {
        Condition[] conditions = Condition.values();
        currentCondition = conditions[random.nextInt(conditions.length)];
        timeUntilChange = minDuration + random.nextFloat() * (maxDuration - minDuration);
    }

    public Condition getCurrentCondition() {
        return currentCondition;
    }

    public float getGrowthMultiplier() {
        return currentCondition.getGrowthMultiplier();
    }

    public Color getAmbientColor() {
        return currentCondition.getAmbientColor();
    }


    public String getDisplayName() {
        return currentCondition.getDisplayName();
    }

    public float getTimeUntilChange() {
        return timeUntilChange;
    }

    public String getCurrentWeatherName(){
        return currentCondition.getDisplayName();
    }

    public static class SavedAnimalPen {
        public boolean isBlocked;
        public String state; // BLOCKED, EMPTY, OCCUPIED
        public int capacity;
        public SavedAnimal[] animals;
        public SavedInventoryItem[] feedStock;

        public SavedAnimalPen() {}

        public SavedAnimalPen(boolean isBlocked, String state, int capacity, SavedAnimal[] animals, SavedInventoryItem[] feedStock) {
            this.isBlocked = isBlocked;
            this.state = state;
            this.capacity = capacity;
            this.animals = animals;
            this.feedStock = feedStock;
        }
    }
}
