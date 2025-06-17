package com.farmgame.game_save;

import com.farmgame.game.Weather;

public class SavedFarm {
    public int width;
    public int height;
    public int penWidth;
    public int penHeight;
    public SavedPlot[][] plots;
    public Weather.SavedAnimalPen[][] animalPens;
    public boolean hasWateringSystem;

    public SavedFarm() {}

    public SavedFarm(int width, int height, int penWidth, int penHeight,
                     SavedPlot[][] plots, Weather.SavedAnimalPen[][] animalPens, boolean hasWateringSystem) {
        this.width = width;
        this.height = height;
        this.penWidth = penWidth;
        this.penHeight = penHeight;
        this.plots = plots;
        this.animalPens = animalPens;
        this.hasWateringSystem = hasWateringSystem;
    }
}
