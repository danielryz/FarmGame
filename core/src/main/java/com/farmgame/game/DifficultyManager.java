package com.farmgame.game;

public class DifficultyManager {
    private float difficultyMultiplier = 1.0f;
    private DifficultyLevel currentLevel = DifficultyLevel.EASY;

    public enum DifficultyLevel {
        EASY(1.0f, "Łatwy"),
        MEDIUM(0.75f, "Średni"),
        HARD(0.25f, "Trudny");

        private final float multiplier;
        private final String displayName;

        DifficultyLevel(float multiplier, String displayName) {
            this.multiplier = multiplier;
            this.displayName = displayName;
        }

        public float getMultiplier() { return multiplier; }
        public String getDisplayName() { return displayName; }
    }

    public void setDifficulty(DifficultyLevel level) {
        this.currentLevel = level;
        this.difficultyMultiplier = level.getMultiplier();
    }

    public float getMoneyMultiplier() {
        return difficultyMultiplier;
    }

    public float getTimeMultiplier() {
        return 1.0f / difficultyMultiplier;
    }

    public float getDifficultyMultiplier() {
        return difficultyMultiplier;
    }

    public DifficultyLevel getCurrentLevel() {
        return currentLevel;
    }

    public void setDifficultyMultiplier(float multiplier) {
        this.difficultyMultiplier = multiplier;

        if (multiplier >= 1.0f) {
            this.currentLevel = DifficultyLevel.EASY;
        } else if (multiplier >= 0.5f) {
            this.currentLevel = DifficultyLevel.MEDIUM;
        } else {
            this.currentLevel = DifficultyLevel.HARD;
        }
    }
}
