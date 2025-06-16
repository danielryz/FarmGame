package com.farmgame.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.farmgame.FarmGame;
import com.farmgame.game_save.SaveManager;

public class MenuScreen implements Screen {
    private final FarmGame game;
    private Stage stage;
    private Skin skin;
    private SpriteBatch batch;
    private OrthographicCamera camera;
    private SaveManager saveManager;
    private float difficultyMultiplier = 1.0f;
    private String playerName = "FarmGame";

    public MenuScreen(FarmGame game) {
        this.game = game;
        this.batch = new SpriteBatch();
        this.camera = new OrthographicCamera();
        this.saveManager = new SaveManager();

        // Load UI skin
        skin = new Skin(Gdx.files.internal("assets/uiskin.json"));
        stage = new Stage(new ScreenViewport(), batch);
        Gdx.input.setInputProcessor(stage);

        createUI();
    }

    private void createUI() {
        Table mainTable = new Table();
        mainTable.setFillParent(true);
        stage.addActor(mainTable);

        Label titleLabel = new Label("Farm Game", skin);
        titleLabel.setAlignment(Align.center);

        TextButton newGameButton = new TextButton("Nowa Gra", skin);
        newGameButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                showNewGameDialog();
            }
        });

        Table loadGameTable = new Table();
        loadGameTable.defaults().pad(5).fillX().expandX();

        Label loadGameLabel = new Label("Wczytaj Grę:", skin);
        loadGameTable.add(loadGameLabel).colspan(2).align(Align.left).row();

        for (int i = 1; i <= 5; i++) {
            final int slotNumber = i;
            TextButton loadButton = new TextButton("Slot " + i, skin);
            boolean saveExists = saveManager.saveExists(slotNumber);
            if (!saveExists) {
                loadButton.setDisabled(true);
                loadButton.setText("Slot " + i + " (Pusty)");
            }

            loadButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    loadGame(slotNumber);
                }
            });

            loadGameTable.add(loadButton).expandX().fillX();

            if (i % 2 == 0) {
                loadGameTable.row();
            }
        }
        mainTable.add(titleLabel).colspan(2).pad(20).row();
        mainTable.add(newGameButton).colspan(2).pad(10).fillX().width(300).row();
        mainTable.add(loadGameTable).colspan(2).pad(10).fillX().width(300).row();
    }

    private void showNewGameDialog() {
        Dialog dialog = new Dialog("Nowa Gra", skin);

        Table contentTable = new Table();
        contentTable.pad(20);

        contentTable.add(new Label("Nazwa Farmy:", skin)).left().padRight(10);
        TextField nameField = new TextField(playerName, skin);
        contentTable.add(nameField).fillX().expandX().row();
        contentTable.add(new Label("Poziom Trudności:", skin)).left().padRight(10).padTop(20);

        ButtonGroup<TextButton> difficultyGroup = new ButtonGroup<>();
        difficultyGroup.setMinCheckCount(1);
        difficultyGroup.setMaxCheckCount(1);

        Table difficultyTable = new Table();

        TextButton easyButton = new TextButton("Łatwy", skin, "toggle");
        easyButton.setChecked(true); // Default selection
        easyButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (easyButton.isChecked()) {
                    difficultyMultiplier = 1.0f;
                    game.getDifficultyManager().setDifficultyMultiplier(difficultyMultiplier);
                }
            }
        });

        TextButton mediumButton = new TextButton("Średni", skin, "toggle");
        mediumButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (mediumButton.isChecked()) {
                    difficultyMultiplier = 0.75f;
                    game.getDifficultyManager().setDifficultyMultiplier(difficultyMultiplier);
                }
            }
        });

        TextButton hardButton = new TextButton("Trudny", skin, "toggle");
        hardButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (hardButton.isChecked()) {
                    difficultyMultiplier = 0.25f;
                    game.getDifficultyManager().setDifficultyMultiplier(difficultyMultiplier);
                }
            }
        });

        difficultyGroup.add(easyButton);
        difficultyGroup.add(mediumButton);
        difficultyGroup.add(hardButton);

        difficultyTable.add(easyButton).pad(5);
        difficultyTable.add(mediumButton).pad(5);
        difficultyTable.add(hardButton).pad(5);

        contentTable.add(difficultyTable).left().row();

        Label difficultyDescription = new Label(
            "Łatwy: Normalne ceny i czas produkcji\n" +
            "Średni: 75% pieniędzy, dłuższy czas produkcji\n" +
            "Trudny: 25% pieniędzy, znacznie dłuższy czas produkcji",
            skin
        );
        difficultyDescription.setWrap(true);
        contentTable.add(difficultyDescription).colspan(2).width(400).padTop(20).row();

        dialog.getContentTable().add(contentTable).expand().fill();

        TextButton startButton = new TextButton("Rozpocznij", skin);
        TextButton cancelButton = new TextButton("Anuluj", skin);

        dialog.button(startButton);
        dialog.button(cancelButton);

        startButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                playerName = nameField.getText();
                if (playerName == null || playerName.trim().isEmpty()) {
                    playerName = "FarmGame"; // Default name if empty
                }
                startNewGame();
                dialog.hide();
            }
        });

        cancelButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                dialog.hide();
            }
        });

        dialog.show(stage);
    }

    private void startNewGame() {
        game.getDifficultyManager().setDifficultyMultiplier(difficultyMultiplier);

        game.setScreen(new GameScreen(game, playerName, difficultyMultiplier, 0)); // 0 means no save slot (new game)
    }

    private void loadGame(int slotNumber) {
        game.setScreen(new GameScreen(game, null, 0, slotNumber)); // null name and 0 difficulty means load from save
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void hide() {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
        stage.dispose();
        batch.dispose();
        skin.dispose();
    }
}
