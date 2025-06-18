package com.farmgame.game_save;

import java.util.ArrayList;

public class GameState {
    public SavedPlayer player;
    public SavedFarm farm;
    public SavedGameClock gameClock;
    public SavedWeather weather;
    public String selectedPlant;
    public String currentAction;
    public float difficultyMultiplier = 1.0f;
    public ArrayList<SavedQuest> quests;

    public GameState() {
        player = new SavedPlayer();
        gameClock = new SavedGameClock();
        weather = new SavedWeather();
        selectedPlant = "";
        currentAction = "PLANT";
        quests = new ArrayList<>();
    }

    public GameState(SavedPlayer player, SavedFarm farm, SavedGameClock gameClock, SavedWeather weather, String selectedPlant, String currentAction, ArrayList<SavedQuest> quests) {
        this.player = player;
        this.farm = farm;
        this.gameClock = gameClock;
        this.weather = weather;
        this.selectedPlant = selectedPlant;
        this.currentAction = currentAction;
        this.quests = quests != null ? quests : new ArrayList<>();
    }


}
