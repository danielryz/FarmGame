package com.farmgame.game_save;

public class SavedWeather {
    public String currentWeather; // nazwa aktualnej pogody
    public float timeUntilChange;

    public SavedWeather() {}

    public SavedWeather(String currentWeather, float timeUntilChange) {
        this.currentWeather = currentWeather;
        this.timeUntilChange = timeUntilChange;
    }
}
