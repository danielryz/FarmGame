package com.farmgame.screen;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.farmgame.game.*;

public class GameScreen implements Screen {
    private final Farm farm;
    private final int TILE_SIZE = 64;
    private final ShapeRenderer shapeRenderer;
    private final SpriteBatch batch;
    private final BitmapFont font = new BitmapFont();

    private final Stage stage;
    private final Skin skin;
    private PlantType selectedPlant = null;
    private TextButton selectedButton = null;
    private final TextButton waterButton;
    private final TextButton harvestButton;
    private enum Action {
        PLANT,
        WATER,
        HARVEST
    }
    private Action currentAction = Action.PLANT;
    private final Inventory inventory = new Inventory();
    private int money = 20;
    private final Label moneyLabel;
    private final Table inventoryTable;

    public GameScreen() {
        this.farm = new Farm(10, 10);
        this.shapeRenderer = new ShapeRenderer();
        this.batch = new SpriteBatch();

        skin = new Skin(Gdx.files.internal("assets/uiskin.json"));
        stage = new Stage(new ScreenViewport(), batch);
        Gdx.input.setInputProcessor(stage);

        Table root = new Table();
        root.setFillParent(true);
        root.pad(10);
        stage.addActor(root);

        this.moneyLabel = new Label("Pieniądze: " + money, skin);
        root.row();
        root.add(moneyLabel).right().top().pad(10);

        Table sidebar = new Table();
        sidebar.defaults().pad(4).left();

        ScrollPane scrollPane = new ScrollPane(sidebar, skin);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setScrollingDisabled(false, true);
        scrollPane.setScrollbarsOnTop(true);
        scrollPane.setForceScroll(false, true);
        scrollPane.setScrollingDisabled(true, false);

        Table plantButtons = new Table();
        sidebar.add(new Label("Rośliny:", skin)).left().row();

        for (PlantType type : PlantDatabase.getAll()) {
            Pixmap pixmap = new Pixmap(16, 16, Pixmap.Format.RGBA8888);
            pixmap.setColor(type.getColor());
            pixmap.fill();
            Texture texture = new Texture(pixmap);
            pixmap.dispose();

            Image colorBox = new Image(texture);
            TextButton button = new TextButton(type.getName(), skin);
            button.getLabel().setAlignment(Align.left);

            Label infoLabel = new Label("Kup: " + type.getSeedPrice() + "$ | Sprzedaj: " + type.getSellPrice()
                + "$" + "\nCzas wzrostu: " + type.getGrowthTime(), skin);
            infoLabel.setFontScale(0.9f);

            button.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    selectedPlant = type;
                    currentAction = Action.PLANT;

                    if (selectedButton != null) selectedButton.setColor(Color.WHITE);
                    selectedButton = button;
                    selectedButton.setColor(Color.CYAN);

                    waterButton.setColor(Color.WHITE);
                    harvestButton.setColor(Color.WHITE);
                }
            });


            Table row = new Table();
            row.add(colorBox).size(16).padRight(5);
            row.add(button).left().expandX().fillX().row();
            row.add().padLeft(21);
            row.add(infoLabel).left().colspan(2).row();

            plantButtons.add(row).expandX().fillX().padBottom(6).row();
        }

        sidebar.add(plantButtons).expandX().fillX().row();

        sidebar.add(new Label("Akcje:", skin)).left().padTop(10).row();

        waterButton = new TextButton("Podlej", skin);
        harvestButton = new TextButton("Zbierz", skin);
        TextButton sellAllButton = getSellAllButton();
        sidebar.add(sellAllButton).expandX().fillX().padTop(10).row();

        waterButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                currentAction = Action.WATER;
                if (selectedButton != null) selectedButton.setColor(Color.WHITE);
                selectedButton = waterButton;
                waterButton.setColor(Color.CYAN);
                harvestButton.setColor(Color.WHITE);
            }
        });

        harvestButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                currentAction = Action.HARVEST;
                if (selectedButton != null) selectedButton.setColor(Color.WHITE);
                selectedButton = harvestButton;
                harvestButton.setColor(Color.CYAN);
                waterButton.setColor(Color.WHITE);
            }
        });

        sidebar.add(waterButton).expandX().fillX().row();
        sidebar.add(harvestButton).expandX().fillX().row();

        sidebar.add(new Label("Magazyn:", skin)).left().padTop(10).row();

        this.inventoryTable = new Table();
        updateInventoryTable(inventoryTable);
        sidebar.add(inventoryTable).expandX().fillX().row();

        root.top().right();
        root.add(scrollPane).width(400).expandY().fillY();

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
                            int cost = selectedPlant.getSeedPrice();
                            if (money >= cost) {
                                money -= cost;
                                plot.plant(new Plant(selectedPlant));
                            } else {
                                System.out.println("Za mało pieniędzy!");
                            }
                        }
                    }
                    case WATER -> plot.water();
                    case HARVEST -> {
                        if (plot.getState() == Plot.State.READY_TO_HARVEST && plot.getPlant() != null) {
                            inventory.addItem(plot.getPlant().getType().getName(), 1);
                            plot.harvest();
                            updateInventoryTable(inventoryTable);
                        }
                    }
                }
                return true;
            }
        };

        Gdx.input.setInputProcessor(new InputMultiplexer(stage, gameInput));
    }

    private TextButton getSellAllButton() {
        TextButton sellAllButton = new TextButton("Sprzedaj wszystko", skin);
        sellAllButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                int earned = 0;

                for (var entry : inventory.getItems().entrySet()) {
                    String plantName = entry.getKey();
                    int count = entry.getValue();

                    PlantType type = PlantDatabase.getByName(plantName);
                    if (type != null) {
                        earned += count * type.getSellPrice();
                    }
                }

                money += earned;
                inventory.clearItem();
                updateInventoryTable(inventoryTable);
            }
        });
        return sellAllButton;
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

                Color baseColor = switch (plot.getState()) {
                    case EMPTY -> Color.BROWN;
                    case PLANTED -> Color.SKY;
                    case GROWTH -> Color.FOREST;
                    case READY_TO_HARVEST -> Color.GOLD;
                };
                shapeRenderer.setColor(baseColor);
                shapeRenderer.rect(x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE - 2, TILE_SIZE - 2);

                Plant plant = plot.getPlant();
                if (plant != null) {
                    float centerX = x * TILE_SIZE + TILE_SIZE / 4f;
                    float centerY = y * TILE_SIZE + TILE_SIZE / 4f;
                    float size = TILE_SIZE / 2f;

                    shapeRenderer.setColor(plant.getType().getColor());
                    shapeRenderer.rect(centerX, centerY, size, size);

                    if (plant.isWatered()) {
                        float waterSize = TILE_SIZE / 4f;
                        float waterX = x * TILE_SIZE + TILE_SIZE - waterSize - 4;
                        float waterY = y * TILE_SIZE + 4;

                        shapeRenderer.setColor(Color.CYAN);
                        shapeRenderer.rect(waterX, waterY, waterSize, waterSize);
                    }
                }
            }
        }
        shapeRenderer.end();

        batch.begin();
        for (int x = 0; x < farm.getWidth(); x++) {
            for (int y = 0; y < farm.getHeight(); y++) {
                Plot plot = farm.getPlot(x, y);
                Plant plant = plot.getPlant();

                if (plant != null && plot.getState() != Plot.State.EMPTY) {
                    float timeLeft = plant.getTimeRemaining();

                    String timeText = String.format("%.0f", timeLeft);
                    float textX = x * TILE_SIZE + TILE_SIZE / 2f - 8;
                    float textY = y * TILE_SIZE + TILE_SIZE / 2f + 5;

                    font.setColor(Color.BLACK);
                    font.draw(batch, timeText, textX, textY);
                }
            }
        }
        batch.end();

        moneyLabel.setText("Pieniądze: " + money);

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

    private void updateInventoryTable(Table inventoryTable) {
        inventoryTable.clear();

        for (var entry : inventory.getItems().entrySet()) {
            String name = entry.getKey();
            int count = entry.getValue();

            if (count <= 0) continue;

            PlantType type = PlantDatabase.getByName(name);
            if (type == null) continue;

            Label itemLabel = new Label(name + " x" + count + " (" + type.getSellPrice() + "$)", skin);
            TextButton sellButton = new TextButton("Sprzedaj", skin);

            sellButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    inventory.removeItem(name, 1);
                    money += type.getSellPrice();
                    updateInventoryTable(inventoryTable);
                }
            });

            Table row = new Table();
            row.add(itemLabel).left().expandX();
            row.add(sellButton).right();
            inventoryTable.add(row).fillX().padBottom(2).row();
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
        font.dispose();
    }
}
