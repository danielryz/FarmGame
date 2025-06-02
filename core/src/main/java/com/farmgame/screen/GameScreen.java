package com.farmgame.screen;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.farmgame.game.*;

public class GameScreen implements Screen {
    private final Farm farm;
    private final int TILE_SIZE = 32;
    private final ShapeRenderer shapeRenderer;
    private final SpriteBatch batch;

    private final Stage stage;
    private final Skin skin;
    private PlantType selectedPlant = null;
    private enum Action {
        PLANT,
        WATER,
        HARVEST
    }
    private Action currentAction = Action.PLANT;



    public GameScreen() {
        this.farm = new Farm(10, 10);
        this.shapeRenderer = new ShapeRenderer();
        this.batch = new SpriteBatch();

        skin = new Skin(Gdx.files.internal("assets/uiskin.json"));
        stage = new Stage(new ScreenViewport(), batch);
        Gdx.input.setInputProcessor(stage);

        Table root = new Table();
        root.setFillParent(true);
        root.top().right();
        stage.addActor(root);

        Table buttonTable = new Table();
        ScrollPane scrollPane = new ScrollPane(buttonTable, skin);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setScrollingDisabled(false, true);
        scrollPane.setScrollbarsOnTop(true);
        scrollPane.setForceScroll(false, true);

        for (PlantType type : PlantDatabase.getAll()) {
            TextButton button = new TextButton(type.getName(), skin);
            button.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    selectedPlant = type;
                    currentAction = Action.PLANT;
                }
            });
            buttonTable.add(button).pad(2).row();
        }

        TextButton waterButton = new TextButton("Podlej", skin);
        waterButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                currentAction = Action.WATER;
            }
        });

        TextButton harvestButton = new TextButton("Zbierz", skin);
        harvestButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                currentAction = Action.HARVEST;
            }
        });

        root.add(scrollPane).width(150).pad(10);
        root.row();
        root.add(waterButton).pad(5);
        root.row();
        root.add(harvestButton).pad(5);

        InputAdapter gameInput = new InputAdapter() {
            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                int x = screenX / TILE_SIZE;
                int y = (Gdx.graphics.getHeight() - screenY) / TILE_SIZE;
                Plot plot = farm.getPlot(x, y);
                if (plot == null) return false;

                switch (currentAction) {
                    case PLANT -> {
                        if (selectedPlant != null && plot.getState() == Plot.State.EMPTY) {
                            plot.plant(new Plant(selectedPlant));
                        }
                    }
                    case WATER -> plot.water();
                    case HARVEST -> plot.harvest();
                }
                return true;
            }
        };

        Gdx.input.setInputProcessor(new InputMultiplexer(stage, gameInput));
    }

    @Override
    public void render(float delta) {
        farmUpdate(delta);

        Gdx.gl.glClearColor(0.3f, 0.5f, 0.3f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (int x = 0; x < farm.getWidth(); x++) {
            for (int y = 0; y < farm.getHeight(); y++) {
                Plot plot = farm.getPlot(x, y);
                switch (plot.getState()) {
                    case EMPTY -> shapeRenderer.setColor(Color.BROWN);
                    case PLANTED -> shapeRenderer.setColor(Color.SKY);
                    case GROWTH -> shapeRenderer.setColor(Color.FOREST);
                    case READY_TO_HARVEST -> shapeRenderer.setColor(Color.GOLD);
                }
                shapeRenderer.rect(x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE - 2, TILE_SIZE - 2);
            }
        }
        shapeRenderer.end();

        stage.act(delta);
        stage.draw();
    }

    private void farmUpdate(float delta) {
        for (int x = 0; x < farm.getWidth(); x++) {
            for (int y = 0; y < farm.getHeight(); y++) {
                farm.getPlot(x, y).update(delta);
            }
        }
    }


    @Override public void resize(int width, int height) {}
    @Override public void show() {}
    @Override public void hide() {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void dispose() {
        shapeRenderer.dispose();
        stage.dispose();
        batch.dispose();
        skin.dispose();
    }
}
