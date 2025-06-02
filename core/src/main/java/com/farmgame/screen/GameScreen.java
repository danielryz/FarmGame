package com.farmgame.screen;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.farmgame.game.Farm;
import com.farmgame.game.Plot;

public class GameScreen implements Screen {
    private final Farm farm;
    private final int TILE_SIZE = 32;
    private final ShapeRenderer shapeRenderer;

    public GameScreen() {
        this.farm = new Farm(10, 10);
        this.shapeRenderer = new ShapeRenderer();

        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                int x = screenX / TILE_SIZE;
                int y = (Gdx.graphics.getHeight() - screenY) / TILE_SIZE;
                Plot plot = farm.getPlot(x, y);
                if (plot != null) plot.nextState();
                return true;
            }
        });
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.3f, 0.5f, 0.3f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (int x = 0; x < farm.getWidth(); x++) {
            for (int y = 0; y < farm.getHeight(); y++) {
                Plot plot = farm.getPlot(x, y);
                switch (plot.getState()) {
                    case EMPTY -> shapeRenderer.setColor(Color.BROWN);
                    case PLANTED -> shapeRenderer.setColor(Color.GREEN);
                    case GROWTH -> shapeRenderer.setColor(Color.YELLOW);
                    case READY_TO_HARVEST -> shapeRenderer.setColor(Color.GOLD);
                }
                shapeRenderer.rect(x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE - 2, TILE_SIZE - 2);
            }
        }
        shapeRenderer.end();
    }

    @Override public void resize(int width, int height) {}
    @Override public void show() {}
    @Override public void hide() {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void dispose() {
        shapeRenderer.dispose();
    }
}
