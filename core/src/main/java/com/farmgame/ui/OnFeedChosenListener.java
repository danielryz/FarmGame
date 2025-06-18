package com.farmgame.ui;

import com.farmgame.game.PlantType;

public interface OnFeedChosenListener {
    void onChosen(PlantType chosenPlantType, int quantity);
}
