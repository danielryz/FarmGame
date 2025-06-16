package com.farmgame;

import com.badlogic.gdx.Game;
import com.farmgame.game.DifficultyManager;
import com.farmgame.screen.MenuScreen;

public class FarmGame extends Game {
    private DifficultyManager difficultyManager;

    @Override
    public void create() {
        difficultyManager = new DifficultyManager();
        setScreen(new MenuScreen(this));
    }

    public DifficultyManager getDifficultyManager() {
        return difficultyManager;
    }
}
