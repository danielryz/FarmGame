package com.farmgame.game_save;

public class SavedPlot {
    public boolean isBlocked;
    public String state; // BLOCKED, EMPTY, PLANTED, GROWTH, READY_TO_HARVEST
    public SavedPlant plant;

    public SavedPlot() {}

    public SavedPlot(boolean isBlocked, String state, SavedPlant plant) {
        this.isBlocked = isBlocked;
        this.state = state;
        this.plant = plant;
    }
}
