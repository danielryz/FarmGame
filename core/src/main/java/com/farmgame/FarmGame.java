package com.farmgame;

import com.badlogic.gdx.Game;
import com.farmgame.screen.GameScreen;

public class FarmGame extends Game {
    @Override
    public void create() {
        setScreen(new GameScreen());
    }
}
