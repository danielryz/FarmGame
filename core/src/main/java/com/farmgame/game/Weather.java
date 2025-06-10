package com.farmgame.game;

import com.badlogic.gdx.graphics.Color;
import java.util.Random;

public class Weather {

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
}
