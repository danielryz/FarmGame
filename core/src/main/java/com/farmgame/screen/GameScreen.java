package com.farmgame.screen;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.farmgame.FarmGame;
import com.farmgame.game.*;
import com.farmgame.game_save.GameState;
import com.farmgame.player.InventoryItem;
import com.farmgame.player.Player;
import com.farmgame.game_save.SaveManager;
import com.farmgame.ui.*;


import java.util.ArrayList;

public class GameScreen implements Screen {
    private final Farm farm;
    private final int TILE_SIZE = 64;
    private final int PEN_SIZE = 128;
    private final int X_OFFSET = 64;
    private final int Y_OFFSET = 128;
    private final ShapeRenderer shapeRenderer;
    private final SpriteBatch batch;
    private final BitmapFont font = new BitmapFont();

    private SaveManager saveManager;

    private final Stage stage;
    private final Skin skin;
    private PlantType selectedPlant = null;
    private TextButton selectedButton = null;
    private final TextButton waterButton;
    private final TextButton harvestButton;
    private final TextButton feedButton;
    private final TextButton fertilizeButton;
    private final TextButton buyFertilizerButton;
    private final TextButton buyWateringButton;
    private InventorySellWindow currentInventoryWindow = null;
    private enum Action {
        PLANT,
        WATER,
        HARVEST,
        FEED,
        FERTILIZE
    }
    private Action currentAction = Action.PLANT;
    private final Label moneyLabel;
    private final OrthographicCamera camera;
    private final Viewport gameViewport;
    private final GameClock gameClock;
    private final Weather weather;
    private final Label clockLabel;
    private final Label weatherLabel;
    private final Player player;
    private final Label playerNameLabel;
    private final Label playerLevelLabel;
    private final Label playerExpLabel;
    private final Label expToNextLevelLabel;

    private Label messageLabel;
    private float messageTimer = 0;
    private final float MESSAGE_DISPLAY_TIME = 3f;

    private final int penOffsetX;

    private int lastProcessedX = -1;
    private int lastProcessedY = -1;
    private boolean isDragging = false;

    private int currentSaveSlot = 1;
    private FarmGame game;

    public GameScreen() {
        this(null, "FarmGame", 1.0f, 0);
    }

    public GameScreen(FarmGame game, String playerName, float difficultyMultiplier, int saveSlot) {
        this.game = game;
        this.shapeRenderer = new ShapeRenderer();
        this.batch = new SpriteBatch();
        this.camera = new OrthographicCamera();
        this.gameViewport = new ScreenViewport(camera);
        this.gameClock = new GameClock();
        this.weather = new Weather();

        if (game != null) {
            saveManager = new SaveManager(game.getDifficultyManager());
        } else {
            saveManager = new SaveManager();
            saveManager.setDifficultyMultiplier(difficultyMultiplier);
        }

        this.farm = new Farm(10, 10, 5, 5, saveManager.getDifficultyManager());

        if (saveSlot > 0 && saveSlot <= 5) {
            this.currentSaveSlot = saveSlot;
        }

        skin = new Skin(Gdx.files.internal("assets/uiskin.json"));

        messageLabel = new Label("", skin);
        messageLabel.setAlignment(Align.center);
        messageLabel.setFontScale(1.2f);
        messageLabel.setColor(Color.WHITE);

        if (saveSlot > 0 && saveManager.saveExists(saveSlot)) {
            this.player = new Player("Temp");
        } else {
            this.player = new Player(playerName);

            int startingMoney = (int)(player.getMoney() * saveManager.getDifficultyMultiplier());
            this.player.setMoney(startingMoney);
        }

        playerNameLabel = new Label("Imie: " + player.getName(), skin);
        playerLevelLabel = new Label("Poziom: " + player.getLevel(), skin);
        playerExpLabel = new Label("Exp: " + player.getExp(), skin);
        expToNextLevelLabel = new Label("Exp do kolejnego poziomu: " + player.getExpToNextLevel(), skin);

        if (saveSlot > 0 && saveManager.saveExists(saveSlot)) {
            loadGame(saveSlot);
        }

        this.penOffsetX = farm.getWidth() * TILE_SIZE + 64 + X_OFFSET;

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("assets/arial.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 16;
        BitmapFont font16 = generator.generateFont(parameter);
        generator.dispose();

        stage = new Stage(new ScreenViewport(), batch);

        MessageManager.initialize(skin, stage);

        // Główna struktura interfejsu
        Table mainTable = new Table();
        mainTable.setFillParent(true);
        stage.addActor(mainTable);

        TextButton saveButton = new TextButton("Zapisz", skin);
        TextButton loadButton = new TextButton("Wczytaj", skin);

        saveButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                showSaveSlotDialog();
            }
        });

        loadButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                showLoadSlotDialog();
            }
        });

        // Inicjalizacja komponentów
        this.moneyLabel = new Label("Pieniądze: " + player.getMoney(), skin);
        this.clockLabel = new Label("", skin);
        this.weatherLabel = new Label("", skin);

        // Prawa strona z menu
        Table rightSideMenu = new Table();
        rightSideMenu.top();
        rightSideMenu.pad(10);

        Table leftSideMenu = new Table();
        leftSideMenu.top().pad(10).left();

        leftSideMenu.add(playerNameLabel).left().row();
        leftSideMenu.add(playerLevelLabel).left().row();
        leftSideMenu.add(playerExpLabel).left().row();
        leftSideMenu.add(expToNextLevelLabel).left().row();

        // Górny pasek (czas, pogoda i pieniądze)
        Table topBar = new Table();
        Table timeWeatherTable = new Table();
        timeWeatherTable.add(clockLabel).left().row();
        timeWeatherTable.add(weatherLabel).left();
        topBar.add(timeWeatherTable).left().expandX();
        topBar.add(moneyLabel);
        rightSideMenu.add(topBar).fillX().expandX().padBottom(10).row();

        // Kontener na przyciski i akcje
        Table sidebar = new Table();
        sidebar.defaults().pad(4).left();

        // Sekcja akcji
        sidebar.add(new Label("Akcje:", skin)).left().padTop(10).row();
        //Okno wyboru rośliny
        TextButton openPlantChooserButton = getPlantChooserButton();
        // Okno Magazynu
        TextButton openSellWindowButton = getInventoryButton();
        sidebar.add(openPlantChooserButton).expandX().fillX().padTop(10).row();
        sidebar.add(openSellWindowButton).expandX().fillX().padTop(10).row();

        feedButton = new TextButton("Nakarm", skin);
        waterButton = new TextButton("Podlej", skin);
        harvestButton = new TextButton("Zbierz", skin);
        fertilizeButton = new TextButton("Nawóz", skin);
        buyFertilizerButton = new TextButton("Kup nawóz (20$)", skin);
        buyWateringButton = new TextButton("Kup nawadnianie (5000$)", skin);

        if (farm.hasWateringSystem()){
            waterButton.setDisabled(true);
        }
        TextButton sellAllButton = getSellAllButton();

        sidebar.add(sellAllButton).expandX().fillX().padTop(10).row();
        sidebar.add(feedButton).expandX().fillX().row();
        sidebar.add(waterButton).expandX().fillX().row();
        sidebar.add(harvestButton).expandX().fillX().row();
        sidebar.add(fertilizeButton).expandX().fillX().row();
        sidebar.add(buyFertilizerButton).expandX().fillX().row();
        sidebar.add(buyWateringButton).expandX().fillX().row();
        sidebar.add(saveButton).expandX().fillX().padTop(10).row();
        sidebar.add(loadButton).expandX().fillX().padTop(10).row();

        feedButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                currentAction = Action.FEED;
                if (selectedButton != null) {
                    selectedButton.setColor(Color.WHITE);
                }
                selectedButton = feedButton;
                feedButton.setColor(Color.CYAN);
                waterButton.setColor(Color.WHITE);
                harvestButton.setColor(Color.WHITE);
            }
        });

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

        fertilizeButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                currentAction = Action.FERTILIZE;
                if (selectedButton != null) selectedButton.setColor(Color.WHITE);
                selectedButton = fertilizeButton;
                fertilizeButton.setColor(Color.CYAN);
                waterButton.setColor(Color.WHITE);
                harvestButton.setColor(Color.WHITE);
                feedButton.setColor(Color.WHITE);
            }
        });

        buyFertilizerButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                int cost = 20;
                if (player.getMoney() >= cost) {
                    player.addMoney(-cost);
                    player.getPlayerInventory().addItem(new InventoryItem("Fertilizer", 1, 0));
                    updatePlayerStatus();
                    MessageManager.info("Kupiono nawóz");
                } else {
                    MessageManager.warning("Za mało pieniędzy na nawóz!");
                }
            }
        });

        buyWateringButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                int cost = 5000;
                if (farm.hasWateringSystem()) {
                    MessageManager.info("System nawadniania już aktywny");
                    return;
                }
                if (player.getMoney() >= cost) {
                    player.addMoney(-cost);
                    farm.purchaseWateringSystem();
                    waterButton.setDisabled(true);
                    if (currentAction == Action.WATER) {
                        currentAction = Action.PLANT;
                        waterButton.setColor(Color.WHITE);
                    }
                    updatePlayerStatus();
                    MessageManager.info("Zakupiono system nawadniania");
                } else {
                    MessageManager.warning("Za mało pieniędzy na system nawadniania!");
                }
            }
        });

        // Dodanie scrollowania do menu
        ScrollPane scrollPane = new ScrollPane(sidebar, skin);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setScrollingDisabled(true, false);

        rightSideMenu.add(scrollPane).expand().fill().top();

        Table messageTable = new Table();
        messageTable.setBackground(skin.newDrawable("white", new Color(0, 0, 0, 0.7f)));
        messageTable.add(messageLabel).pad(10).expandX().fillX();

        // Dodanie obszarów do głównej tabeli
        mainTable.add(leftSideMenu).width(200).padLeft(10).fill().top();
        mainTable.add().expand().fill();
        mainTable.add(rightSideMenu).width(500).fill().top();

        mainTable.row();
        mainTable.add(messageTable).colspan(3).expandX().fillX().height(50).padBottom(20);

        // Konfiguracja obsługi klikania
        InputAdapter gameInput = new InputAdapter() {
            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                lastProcessedX = -1;
                lastProcessedY = -1;
                isDragging = true;

                Vector3 worldCoords = camera.unproject(new Vector3(screenX, screenY, 0));

                if (worldCoords.x < penOffsetX) {
                    int gridX = (int) ((worldCoords.x - X_OFFSET)/ TILE_SIZE);
                    int gridY = (int) ((worldCoords.y - Y_OFFSET) / TILE_SIZE);
                    if (gridX >= 0 && gridY >= 0 && gridX < farm.getWidth() && gridY < farm.getHeight()) {
                        handlePlotClick(gridX, gridY);
                    }
                    lastProcessedX = gridX;
                    lastProcessedY = gridY;
                } else {
                    float penWorldX = worldCoords.x - penOffsetX;
                    float penWorldY = worldCoords.y - Y_OFFSET;
                    int penX = (int) (penWorldX / PEN_SIZE);
                    int penY = (int) (penWorldY / PEN_SIZE);

                    if (penX >= 0 && penX < farm.getPenWidth() && penY >= 0 && penY < farm.getPenHeight()){
                        handlePenClick(penX, penY);
                    }
                }
                return true;
            }

            // Obsługa kliknij i przytrzymaj
            @Override
            public boolean touchDragged(int screenX, int screenY, int pointer) {
                if (!isDragging || (currentAction != Action.PLANT && currentAction != Action.HARVEST && currentAction != Action.WATER && currentAction != Action.FERTILIZE)) {
                    return false;
                }

                Vector3 worldCoords = camera.unproject(new Vector3(screenX, screenY, 0));
                if (worldCoords.x < penOffsetX) {
                    int gridX = (int) ((worldCoords.x - X_OFFSET) / TILE_SIZE);
                    int gridY = (int) ((worldCoords.y - Y_OFFSET) / TILE_SIZE);

                    if (gridX != lastProcessedX || gridY != lastProcessedY) {
                        if (gridX >= 0 && gridY >= 0 && gridX < farm.getWidth() && gridY < farm.getHeight()) {
                            handlePlotClick(gridX, gridY);
                        }

                        lastProcessedX = gridX;
                        lastProcessedY = gridY;
                    }
                }
                return true;
            }

            @Override
            public boolean touchUp(int screenX, int screenY, int pointer, int button) {
                isDragging = false;
                return true;
            }
        };

        Gdx.input.setInputProcessor(new InputMultiplexer(stage, gameInput));
    }

    private void saveGame() {
        try {
            String currentActionStr = switch (currentAction) {
                case PLANT -> "PLANT";
                case WATER -> "WATER";
                case HARVEST -> "HARVEST";
                case FEED -> "FEED";
                case FERTILIZE -> "FERTILIZE";
            };

            saveManager.saveGame(player, farm, gameClock, weather, selectedPlant, currentActionStr, currentSaveSlot);
            showMessage("Gra zapisana w slocie " + currentSaveSlot + "!", Color.GREEN);
        } catch (Exception e) {
            showMessage("Błąd podczas zapisywania!", Color.RED);
            Gdx.app.error("GameScreen", "Błąd zapisu: " + e.getMessage());
        }
    }

    private void showMessage(String text, Color color) {
        messageLabel.setText(text);
        messageLabel.setColor(color);
        messageTimer = MESSAGE_DISPLAY_TIME;
    }

    private void showSaveSlotDialog() {
        Dialog dialog = new Dialog("Wybierz slot zapisu", skin);

        Table contentTable = new Table();
        contentTable.pad(20);

        contentTable.add(new Label("Wybierz slot do zapisania gry:", skin)).colspan(2).left().padBottom(20).row();

        ButtonGroup<TextButton> slotGroup = new ButtonGroup<>();
        slotGroup.setMinCheckCount(1);
        slotGroup.setMaxCheckCount(1);

        Table slotTable = new Table();

        for (int i = 1; i <= 5; i++) {
            final int slotNumber = i;
            TextButton slotButton = new TextButton("Slot " + i, skin, "toggle");

            boolean saveExists = saveManager.saveExists(slotNumber);
            if (saveExists) {
                slotButton.setText("Slot " + i + " (Zajęty)");
            }

            if (slotNumber == currentSaveSlot) {
                slotButton.setChecked(true);
            }

            slotButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    if (slotButton.isChecked()) {
                        currentSaveSlot = slotNumber;
                    }
                }
            });

            slotGroup.add(slotButton);
            slotTable.add(slotButton).pad(5);

            if (i % 3 == 0) {
                slotTable.row();
            }
        }

        contentTable.add(slotTable).colspan(2).row();

        dialog.getContentTable().add(contentTable).expand().fill();

        TextButton saveButton = new TextButton("Zapisz", skin);
        TextButton cancelButton = new TextButton("Anuluj", skin);

        dialog.button(saveButton);
        dialog.button(cancelButton);

        saveButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                saveGame();
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

    private void showLoadSlotDialog() {
        Dialog dialog = new Dialog("Wybierz slot wczytania", skin);

        Table contentTable = new Table();
        contentTable.pad(20);

        contentTable.add(new Label("Wybierz slot do wczytania gry:", skin)).colspan(2).left().padBottom(20).row();

        ButtonGroup<TextButton> slotGroup = new ButtonGroup<>();
        slotGroup.setMinCheckCount(1);
        slotGroup.setMaxCheckCount(1);

        Table slotTable = new Table();
        boolean anySlotAvailable = false;

        for (int i = 1; i <= 5; i++) {
            final int slotNumber = i;
            TextButton slotButton = new TextButton("Slot " + i, skin, "toggle");

            boolean saveExists = saveManager.saveExists(slotNumber);
            if (!saveExists) {
                slotButton.setText("Slot " + i + " (Pusty)");
                slotButton.setDisabled(true);
            } else {
                anySlotAvailable = true;
                if (slotNumber == currentSaveSlot) {
                    slotButton.setChecked(true);
                }
            }

            slotButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    if (slotButton.isChecked()) {
                        currentSaveSlot = slotNumber;
                    }
                }
            });

            slotGroup.add(slotButton);
            slotTable.add(slotButton).pad(5);

            if (i % 3 == 0) {
                slotTable.row();
            }
        }

        contentTable.add(slotTable).colspan(2).row();

        dialog.getContentTable().add(contentTable).expand().fill();

        TextButton loadButton = new TextButton("Wczytaj", skin);
        TextButton cancelButton = new TextButton("Anuluj", skin);

        dialog.button(loadButton);
        dialog.button(cancelButton);

        if (!anySlotAvailable) {
            loadButton.setDisabled(true);
        }

        loadButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                loadGame();
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

    private void loadGame() {
        loadGame(currentSaveSlot);
    }

    private void loadGame(int slot) {
        try {
            GameState gameState = saveManager.loadGame(slot);
            if (gameState != null) {
                saveManager.applyGameState(gameState, player, farm, gameClock, weather);

                farm.setDifficultyMultiplier(saveManager.getDifficultyMultiplier());

                // Przywróć wybraną roślinę
                if (gameState.selectedPlant != null) {
                    selectedPlant = PlantType.getByName(gameState.selectedPlant);
                }

                // Przywróć akcję
                if (gameState.currentAction != null) {
                    try {
                        currentAction = Action.valueOf(gameState.currentAction);
                    } catch (IllegalArgumentException e) {
                        currentAction = Action.PLANT;
                    }
                }

                // Odśwież UI
                updatePlayerStatus();
                showMessage("Gra wczytana ze slotu " + slot + "!", Color.GREEN);
            } else {
                showMessage("Brak zapisu do wczytania w slocie " + slot + "!", Color.YELLOW);
            }
        } catch (Exception e) {
            showMessage("Błąd podczas wczytywania!", Color.RED);
            Gdx.app.error("GameScreen", "Błąd wczytywania: " + e.getMessage());
        }
    }

    private void handlePlotClick(int x, int y) {
        Plot plot = farm.getPlot(x, y);
        if (plot == null) return;

        if (plot.isBlocked() && hasUnlockedNeighborPlot(x, y)) {
            int price = farm.getPlotPrice(x, y);
            if (player.getMoney() >= price) {
                plot.unlock();
                player.addMoney(-price);
                player.addExp(2);
                updatePlayerStatus();
            }
            else {
                MessageManager.warning("Za mało pieniędzy!");
            }
        }

        switch (currentAction) {
            case PLANT -> {
                if (selectedPlant != null && plot.getState() == Plot.State.EMPTY) {
                    int cost = (int)(selectedPlant.getSeedPrice() / saveManager.getDifficultyMultiplier());
                    if (player.getMoney() >= cost) {
                        player.addMoney(-cost);
                        player.addExp(1);
                        updatePlayerStatus();
                        plot.plant(selectedPlant);

                    } else {
                        MessageManager.error("Za mało pieniędzy!");
                    }
                }
            }
            case WATER -> {
                if (farm.hasWateringSystem()){
                    MessageManager.info("System nawadniania jest aktywny");
                    break;
                }
                Plant plant = plot.getPlant();
                if (plant != null &&
                    !plant.isWatered() &&
                    !plant.isReadyToHarvest() &&
                    (plot.getState() == Plot.State.PLANTED || plot.getState() == Plot.State.GROWTH)) {
                    plot.water();
                    player.addExp(1);
                    updatePlayerStatus();
                } else {
                    MessageManager.showMessage("Nie można podlać, brak rosnącej rośliny lub jest podlana!", Color.BLUE);
                }
            }
            case HARVEST -> {
                if (plot.getState() == Plot.State.READY_TO_HARVEST && plot.getPlant() != null) {
                    PlantType type = plot.getPlant().getType();
                    int baseSellPrice = type.getSellPrice();
                    int adjustedSellPrice = (int)(baseSellPrice / saveManager.getDifficultyMultiplier());

                    InventoryItem newItem = new InventoryItem(
                        type.getName(),
                        1,
                        adjustedSellPrice
                    );

                    player.getPlayerInventory().addItem(newItem);
                    plot.harvest();
                    player.addExp(1);
                    updatePlayerStatus();
                }
            }
            case FERTILIZE -> {
                if (plot.getPlant() != null && plot.getState() != Plot.State.READY_TO_HARVEST) {
                    if (player.getPlayerInventory().getQuantity("Fertilizer") > 0) {
                        plot.applyFertilizer(30f);
                        player.getPlayerInventory().removeItem("Fertilizer", 1);
                        player.addExp(1);
                        updatePlayerStatus();
                        MessageManager.info("Zastosowano nawóz");
                        if (currentInventoryWindow != null && currentInventoryWindow.getStage() != null) {
                            currentInventoryWindow.refreshInventory();
                        }
                    } else {
                        MessageManager.warning("Brak nawozu!");
                    }
                }
            }
        }
    }

    private void handlePenClick(int penX, int penY) {
        AnimalPen pen = farm.getAnimalPen(penX, penY);

        if (pen.isBlocked() && hasUnlockedNeighborPen(penX, penY)) {
            int penPrice = farm.getPenPrice(penX, penY);
            if (player.getMoney() >= penPrice) {
                pen.unlock();
                player.addMoney(-penPrice);
                player.addExp(5);
                updatePlayerStatus();
            } else {
                MessageManager.warning("Za mało pieniędzy na odblokowanie zagrody!");
            }
            return;
        }

        if (!pen.isBlocked()) {
            if (!pen.isFull()) {
                if (currentAction == Action.FEED || currentAction == Action.PLANT || currentAction == Action.WATER || currentAction == Action.HARVEST || currentAction == Action.FERTILIZE) {
                    AnimalSelectionWindow animalSelectionWindow = new AnimalSelectionWindow(
                            "Kup zwierzę", skin, player, pen, () -> updatePlayerStatus(), saveManager.getDifficultyManager()
                    );
                    stage.addActor(animalSelectionWindow);
                    animalSelectionWindow.setPosition(
                            (stage.getWidth() - animalSelectionWindow.getWidth()) / 2f,
                            (stage.getHeight() - animalSelectionWindow.getHeight()) / 2f
                    );
                    return;
                }
            } else if (currentAction != Action.FEED && currentAction != Action.HARVEST) {
                if (pen.isMaxCapacity()) {
                    MessageManager.info("Zagroda jest już w pełni ulepszona!");
                } else {
                    PenUpgradeWindow upgradeWindow = new PenUpgradeWindow(
                            "Ulepsz zagrodę", skin, player, pen, () -> updatePlayerStatus()
                    );
                    stage.addActor(upgradeWindow);
                    upgradeWindow.setPosition(
                            (stage.getWidth() - upgradeWindow.getWidth()) / 2f,
                            (stage.getHeight() - upgradeWindow.getHeight()) / 2f
                    );
                }
                return;
            }
        }

        if (pen.getState() == AnimalPen.State.OCCUPIED && !pen.getAnimals().isEmpty()) {
            Animal animal = pen.getAnimals().get(0);
            for (Animal a : pen.getAnimals()) {
                if (currentAction == Action.FEED && a.getProductState() == Animal.ProductState.NOT_FED) {
                    animal = a;
                    break;
                } else if (currentAction == Action.HARVEST && a.getProductState() == Animal.ProductState.READY) {
                    animal = a;
                    break;
                }
            }
            final Animal selectedAnimal = animal;
            Animal.ProductState productState = selectedAnimal.getProductState();

            switch (currentAction) {
                case FEED -> {
                    if (productState == Animal.ProductState.NOT_FED) {
                        boolean hasAnyPlant = false;
                        for (String fedName : selectedAnimal.getType().getFeedSet()) {
                            if (player.getPlayerInventory().getQuantity(fedName) > 0) {
                                hasAnyPlant = true;
                                break;
                            }
                        }

                        if (hasAnyPlant) {
                            ChoosePlantToFedWindow choosePlantWindow = new ChoosePlantToFedWindow(
                                "Wybierz roślinę do karmienia",
                                skin,
                                player,
                                    selectedAnimal.getType(),
                                    (chosenPlant, qty) -> {
                                    int available = player.getPlayerInventory().getQuantity(chosenPlant.getName());
                                        if (available < qty) {
                                            MessageManager.warning("Brak wystarczającej ilości " + chosenPlant.getName());
                                            return;
                                    }
                                        pen.addFeed(chosenPlant.getName(), qty);
                                        player.getPlayerInventory().removeItem(chosenPlant.getName(), qty);
                                        MessageManager.info("Dodano do zagrody " + qty + " x " + chosenPlant.getName());
                                        player.addExp(qty);
                                        updatePlayerStatus();

                                        if (currentInventoryWindow != null && currentInventoryWindow.getStage() != null) {
                                            currentInventoryWindow.refreshInventory();
                                    }
                                }
                            );

                            stage.addActor(choosePlantWindow);
                            choosePlantWindow.setPosition(
                                (stage.getWidth() - choosePlantWindow.getWidth()) / 2f,
                                (stage.getHeight() - choosePlantWindow.getHeight()) / 2f
                            );
                        } else {
                            MessageManager.warning("Nie posiadasz żadnej odpowiedniej rośliny do karmienia w magazynie!");
                        }
                    } else {
                        MessageManager.warning("Zwierzę nie może być teraz karmione (aktualny stan: " + productState + ")");
                    }
                }


                case HARVEST -> {
                    if (productState == Animal.ProductState.READY) {
                        int collected = selectedAnimal.collectProduct();
                        if (collected > 0) {
                            String productName = selectedAnimal.getType().getProductName();
                            int baseSellPrice = selectedAnimal.getType().getSellPrice();
                            int adjustedSellPrice = (int)(baseSellPrice / saveManager.getDifficultyMultiplier());

                            InventoryItem newItem = new InventoryItem(productName, collected, adjustedSellPrice);
                            player.getPlayerInventory().addItem(newItem);

                            MessageManager.info("Zebrano produkt: " + productName + " x " + collected);
                            player.addExp(collected);
                            updatePlayerStatus();
                            pen.feedAllIfPossible();
                        } else {
                            MessageManager.warning("Nie udało się zebrać produktu.");
                        }
                    } else {
                        MessageManager.warning("Produkt nie jest gotowy do zebrania. Stan zwierzęcia: " + productState);
                    }
                }
                default -> {
                    MessageManager.warning("Ta zagroda jest już zajęta, użyj 'Nakarm' lub 'Zbierz'.");
                }
            }
        }
    }


    private TextButton getPlantChooserButton() {
        TextButton openPlantChooserButton = new TextButton("Wybierz roślinę", skin);
        openPlantChooserButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {

                PlantSelectionWindow plantSelectionWindow = new PlantSelectionWindow("Wybierz roślinę", skin, chosenPlant -> {
                    selectedPlant = chosenPlant;
                    MessageManager.info("Wybrano roślinę: " + chosenPlant.getName());

                    currentAction = Action.PLANT;
                    if (selectedButton != null) selectedButton.setColor(Color.WHITE);
                    selectedButton = null;
                }, player, saveManager.getDifficultyManager());
                stage.addActor(plantSelectionWindow);

                plantSelectionWindow.setPosition(
                    (stage.getWidth() - plantSelectionWindow.getWidth()) / 2f,
                    (stage.getHeight() - plantSelectionWindow.getHeight()) / 2f
                );
            }
        });
        return openPlantChooserButton;
    }

    private TextButton getInventoryButton() {
        TextButton openSellWindowButton = new TextButton("Magazyn", skin);
        openSellWindowButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (currentInventoryWindow != null && currentInventoryWindow.getStage() != null) {
                    currentInventoryWindow.refreshInventory();
                    return;
                }

                InventorySellWindow sellWindow = new InventorySellWindow(
                    "Magazyn",
                    skin,
                    player,
                    ()-> updatePlayerStatus()
                );

                currentInventoryWindow = sellWindow;

                sellWindow.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        if (!sellWindow.isVisible()) {
                            currentInventoryWindow = null;
                        }
                    }
                });

                stage.addActor(sellWindow);

                sellWindow.setPosition(
                    (stage.getWidth() - sellWindow.getWidth()) / 2f,
                    (stage.getHeight() - sellWindow.getHeight()) / 2f
                );
            }
        });
        return openSellWindowButton;
    }

    private TextButton getSellAllButton() {
        TextButton sellAllButton = new TextButton("Sprzedaj wszystko", skin);
        sellAllButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                int earned = 0;
                int count = 0;
                var allItemsCopy = new ArrayList<>(player.getPlayerInventory().getItems());

                for (InventoryItem item : allItemsCopy) {
                    if (item.getQuantity() <= 0) continue;

                    earned += item.getQuantity() * item.getSellPrice();
                    count += item.getQuantity();

                    player.getPlayerInventory().removeItem(item.getName(), item.getQuantity());
                }
                if (count > 0) {
                    player.addExp(count);
                }
                player.addMoney(earned);
                updatePlayerStatus();
            }
        });
        return sellAllButton;
    }

    @Override
    public void render(float delta) {
        farmUpdate(delta);
        gameClock.update(delta);
        weather.update(delta);
        updateClockLabel();
        updateWeatherLabel();

        Color ambientColor = getAmbientColor();
        Gdx.gl.glClearColor(0.3f * ambientColor.r, 0.5f * ambientColor.g, 0.3f * ambientColor.b, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        gameViewport.apply();
        camera.update();
        shapeRenderer.setProjectionMatrix(camera.combined);
        batch.setProjectionMatrix(camera.combined);

        batch.setColor(ambientColor);
        shapeRenderer.setColor(shapeRenderer.getColor().mul(ambientColor));

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        // Rysowanie PLOT
        for (int x = 0; x < farm.getWidth(); x++) {
            for (int y = 0; y < farm.getHeight(); y++) {
                Plot plot = farm.getPlot(x, y);
                if (plot == null) continue;

                Color baseColor = switch (plot.getState()) {
                    case BLOCKED -> Color.GRAY;
                    case EMPTY -> Color.BROWN;
                    case PLANTED -> Color.SKY;
                    case GROWTH -> Color.FOREST;
                    case READY_TO_HARVEST -> Color.GOLD;
                };

                if (!plot.isBlocked() || hasUnlockedNeighborPlot(x, y)) {
                    shapeRenderer.setColor(baseColor);
                    shapeRenderer.rect(x * TILE_SIZE + X_OFFSET, y * TILE_SIZE + Y_OFFSET, TILE_SIZE - 2, TILE_SIZE - 2);
                }

                // Plant
                if (plot.getPlant() != null) {
                    Plant plant = plot.getPlant();
                    float centerX = x * TILE_SIZE + X_OFFSET + TILE_SIZE / 4f;
                    float centerY = y * TILE_SIZE + Y_OFFSET + TILE_SIZE / 4f;
                    float size = TILE_SIZE / 2f;

                    shapeRenderer.setColor(plant.getType().getColor());
                    shapeRenderer.rect(centerX, centerY, size, size);

                    // Kropla wody
                    if (plant.isWatered() || plant.isAutoWatered()) {
                        float waterSize = TILE_SIZE / 4f;
                        float waterX = x * TILE_SIZE + X_OFFSET + TILE_SIZE - waterSize - 4;
                        float waterY = y * TILE_SIZE + Y_OFFSET + 4;

                        shapeRenderer.setColor(Color.CYAN);
                        shapeRenderer.rect(waterX, waterY, waterSize, waterSize);
                    }

                    // Ikona nawozu
                    if (plant.getFertilizerTimer() > 0f) {
                        float fertSize = TILE_SIZE / 4f;
                        float fertX = x * TILE_SIZE + X_OFFSET + 4;
                        float fertY = y * TILE_SIZE + Y_OFFSET + TILE_SIZE - fertSize - 4;

                        shapeRenderer.setColor(Color.MAGENTA);
                        shapeRenderer.rect(fertX, fertY, fertSize, fertSize);
                    }
                }
            }
        }
        // Rysowanie PEN
        for (int px = 0; px < farm.getPenWidth(); px++) {
            for (int py = 0; py < farm.getPenHeight(); py++) {
                AnimalPen pen = farm.getAnimalPen(px, py);
                if (pen == null) continue;

                Color penColor = switch (pen.getState()) {
                    case BLOCKED -> Color.GRAY;
                    case EMPTY -> Color.LIGHT_GRAY;
                    case OCCUPIED -> Color.BROWN;
                };
                float drawX = px * PEN_SIZE + penOffsetX;
                float drawY = py * PEN_SIZE + Y_OFFSET;

                if (!pen.isBlocked() || hasUnlockedNeighborPen(px, py)) {
                    shapeRenderer.setColor(penColor);
                    shapeRenderer.rect(drawX, drawY, PEN_SIZE - 2, PEN_SIZE - 2);
                }
                if (pen.getState() == AnimalPen.State.OCCUPIED && !pen.getAnimals().isEmpty()) {
                    int perRow = (int)Math.ceil(Math.sqrt(pen.getCapacity()));
                    float cell = PEN_SIZE / (float)perRow;

                    for (int i = 0; i < pen.getAnimals().size(); i++) {
                        Animal animal = pen.getAnimals().get(i);
                        int row = i / perRow;
                        int col = i % perRow;
                        float size = cell - 4f;
                        float ax = drawX + col * cell + 2f;
                        float ay = drawY + row * cell + 2f;

                        shapeRenderer.setColor(animal.getType().getColor());
                        shapeRenderer.rect(ax, ay, size, size);

                        if (animal.getProductState() != Animal.ProductState.NOT_FED) {
                            Color squareColor = switch (animal.getProductState()) {
                                case PRODUCTION -> Color.GREEN;
                                case READY -> Color.GOLD;
                                default -> null;
                            };
                            if (squareColor != null) {
                                shapeRenderer.setColor(squareColor);
                                float squareSize = size / 2f;
                                float squareX = ax + size - squareSize;
                                float squareY = ay + size - squareSize;
                                shapeRenderer.rect(squareX, squareY, squareSize, squareSize);
                            }
                        }
                    }
                }
            }
        }
        shapeRenderer.end();

        batch.begin();
        batch.setColor(Color.WHITE);
        // Tekst do plot
        for (int x = 0; x < farm.getWidth(); x++) {
            for (int y = 0; y < farm.getHeight(); y++) {
                Plot plot = farm.getPlot(x, y);
                if (plot == null) continue;

                if (plot.isBlocked() && hasUnlockedNeighborPlot(x, y)) {
                    String plusSign = "+";
                    GlyphLayout plusLayout = new GlyphLayout(font, plusSign);
                    float plusX = x * TILE_SIZE + X_OFFSET + (TILE_SIZE - plusLayout.width) / 2;
                    float plusY = y * TILE_SIZE + Y_OFFSET + (TILE_SIZE + plusLayout.height) / 2;
                    font.setColor(Color.WHITE);
                    font.draw(batch, plusSign, plusX, plusY);

                    String priceText = farm.getPlotPrice(x, y) + "$";
                    GlyphLayout priceLayout = new GlyphLayout(font, priceText);
                    float priceX = x * TILE_SIZE + X_OFFSET + 2;
                    float priceY = y * TILE_SIZE + Y_OFFSET + priceLayout.height + 2;
                    font.getData().setScale(1f);
                    font.draw(batch, priceText, priceX, priceY);
                }

                if (plot.getPlant() != null && plot.getState() != Plot.State.EMPTY) {
                    float timeLeft = plot.getPlant().getTimeRemaining();
                    String timeText = String.format("%.0f", timeLeft);
                    GlyphLayout layout = new GlyphLayout(font, timeText);

                    float textX = x * TILE_SIZE + X_OFFSET + (TILE_SIZE - layout.width) / 2;
                    float textY = y * TILE_SIZE + Y_OFFSET + (TILE_SIZE + layout.height) / 2;

                    font.setColor(Color.BLACK);
                    font.draw(batch, timeText, textX-1, textY);
                    font.draw(batch, timeText, textX+1, textY);
                    font.draw(batch, timeText, textX, textY-1);
                    font.draw(batch, timeText, textX, textY+1);

                    font.setColor(Color.WHITE);
                    font.draw(batch, timeText, textX, textY);
                }
            }
        }
        // Tekst do PEN
        for (int px = 0; px < farm.getPenWidth(); px++) {
            for (int py = 0; py < farm.getPenHeight(); py++) {
                AnimalPen pen = farm.getAnimalPen(px, py);
                if (pen == null) continue;

                float drawX = px * PEN_SIZE + penOffsetX;
                float drawY = py * PEN_SIZE + Y_OFFSET;

                if (pen.isBlocked() && hasUnlockedNeighborPen(px, py)) {
                    String plusSign = "+";
                    GlyphLayout plusLayout = new GlyphLayout(font, plusSign);
                    float plusX = drawX + (PEN_SIZE - plusLayout.width) / 2;
                    float plusY = drawY + (PEN_SIZE + plusLayout.height) / 2;

                    font.setColor(Color.WHITE);
                    font.draw(batch, plusSign, plusX, plusY);

                    int penPrice = farm.getPenPrice(px, py);
                    String penPriceText = penPrice + "$";
                    GlyphLayout priceLayout = new GlyphLayout(font, penPriceText);
                    float priceX = drawX + (PEN_SIZE - priceLayout.width) / 2;
                    float priceY = drawY + 2 + priceLayout.height;

                    font.draw(batch, penPriceText, priceX, priceY);
                }

                if (pen.getState() == AnimalPen.State.OCCUPIED && !pen.getAnimals().isEmpty()) {
                    int perRow = (int)Math.ceil(Math.sqrt(pen.getCapacity()));
                    float cell = PEN_SIZE / (float)perRow;

                    for (int i = 0; i < pen.getAnimals().size(); i++) {
                        Animal animal = pen.getAnimals().get(i);
                        float timeLeft = animal.getTimeToNextProduct();

                        int row = i / perRow;
                        int col = i % perRow;
                        float ax = drawX + col * cell;
                        float ay = drawY + row * cell;

                        String animalName = animal.getType().getName();
                        GlyphLayout layout = new GlyphLayout(font, animalName);

                        float textX = ax + (cell - layout.width) / 2f;
                        float textY = ay + cell - 4;

                        font.setColor(Color.BLACK);
                        font.draw(batch, animalName, textX - 1, textY);
                        font.draw(batch, animalName, textX + 1, textY);
                        font.draw(batch, animalName, textX, textY - 1);
                        font.draw(batch, animalName, textX, textY + 1);
                        font.setColor(Color.WHITE);
                        font.draw(batch, animalName, textX, textY);

                        if (animal.getProductState() == Animal.ProductState.PRODUCTION && timeLeft >= 0f) {
                            String timeText = String.format("%.0f", timeLeft);
                            GlyphLayout timeLayout = new GlyphLayout(font, timeText);

                            float timeX = ax + (cell - timeLayout.width) / 2f;
                            float timeY = ay + cell / 2f;

                            font.setColor(Color.BLACK);
                            font.draw(batch, timeText, timeX - 1, timeY);
                            font.draw(batch, timeText, timeX + 1, timeY);
                            font.draw(batch, timeText, timeX, timeY - 1);
                            font.draw(batch, timeText, timeX, timeY + 1);
                            font.setColor(Color.WHITE);
                            font.draw(batch, timeText, timeX, timeY);
                        } else if (animal.getProductState() == Animal.ProductState.READY) {
                            String readyText = "Do zbioru!";
                            GlyphLayout readyLayout = new GlyphLayout(font, readyText);
                            float readyX = ax + (cell - readyLayout.width) / 2f;
                            float readyY = ay + cell / 2f;

                            font.setColor(Color.BLACK);
                            font.draw(batch, readyText, readyX - 1, readyY);
                            font.draw(batch, readyText, readyX + 1, readyY);
                            font.draw(batch, readyText, readyX, readyY - 1);
                            font.draw(batch, readyText, readyX, readyY + 1);

                            font.setColor(Color.GOLD);
                            font.draw(batch, readyText, readyX, readyY);
                        }
                    }

                }
            }
        }

        batch.end();

        moneyLabel.setText("Pieniądze: " + player.getMoney());

        stage.act(delta);
        stage.draw();

        if (messageTimer > 0) {
            messageTimer -= delta;
            if (messageTimer <= 0) {
                messageLabel.setText("");
            }
        }
    }




    private void farmUpdate(float delta) {
        float growthMultiplier = getGrowthMultiplier();

        for (int x = 0; x < farm.getWidth(); x++) {
            for (int y = 0; y < farm.getHeight(); y++) {
                Plot plot = farm.getPlot(x, y);
                if (plot != null){
                    plot.update(delta * growthMultiplier);
                }
            }
        }
        for (int px = 0; px < farm.getPenWidth(); px++) {
            for (int py = 0; py < farm.getPenHeight(); py++) {
                AnimalPen pen = farm.getAnimalPen(px, py);
                if (pen != null) {
                    pen.update(delta);
                }
            }
        }
    }


    private float getGrowthMultiplier(){
        float timeMultiplier = switch (gameClock.getTimeOfDay()){
            case MORNING -> 1.2f;
            case NOON -> 1.5f;
            case EVENING -> 1.0f;
            case NIGHT -> 0.5f;
        };

        float weatherMultiplier = weather.getGrowthMultiplier();

        if (gameClock.getTimeOfDay() == GameClock.TimeOfDay.NIGHT) {
            return 0.5f;
        }

        return timeMultiplier * weatherMultiplier;
    }

    private void updateClockLabel(){
        StringBuilder timeText = new StringBuilder();
        timeText.append(String.format("Dzień %d (%s) - %s",
            gameClock.getDay(),
            getPolishWeekDay(gameClock.getWeekDay()),
            getPolishTimeOfDay(gameClock.getTimeOfDay())
        ));
        clockLabel.setText(timeText);
    }

    private void updateWeatherLabel() {
        StringBuilder weatherText = new StringBuilder();
        weatherText.append(String.format("Pogoda: %s", weather.getDisplayName()));

        float multiplier = weather.getGrowthMultiplier();
        if (multiplier > 1.0f) {
            weatherText.append(" (Szybszy wzrost)");
        } else if (multiplier < 1.0f) {
            weatherText.append(" (Wolniejszy wzrost)");
        }

        weatherLabel.setText(weatherText);
    }

    private String getPolishWeekDay(GameClock.WeekDay weekDay) {
        return switch (weekDay){
            case MONDAY -> "Poniedziałek";
            case TUESDAY -> "Wtorek";
            case WEDNESDAY -> "Środa";
            case THURSDAY -> "Czwartek";
            case FRIDAY -> "Piątek";
            case SATURDAY -> "Sobota";
            case SUNDAY -> "Niedziela";

        };
    }

    private String getPolishTimeOfDay(GameClock.TimeOfDay timeOfDay) {
        return switch (timeOfDay){
            case MORNING -> "Ranek";
            case NOON -> "Południe";
            case EVENING -> "Wieczór";
            case NIGHT -> "Noc";

        };
    }

    private Color getAmbientColor() {
        Color timeColor = switch (gameClock.getTimeOfDay()) {
            case MORNING -> new Color(1f, 0.9f, 0.8f, 1f);
            case NOON -> new Color(1f, 1f, 1f, 1f);
            case EVENING -> new Color(0.8f, 0.7f, 0.6f, 1f);
            case NIGHT -> new Color(0.4f, 0.4f, 0.5f, 1f);
        };

        Color weatherColor = weather.getAmbientColor();
        return timeColor.mul(weatherColor);
    }

    public void updatePlayerStatus(){
        playerLevelLabel.setText("Poziom: " + player.getLevel());
        playerExpLabel.setText("Exp: " + player.getExp());
        expToNextLevelLabel.setText("Exp do kolejnego poziomu: " + player.getExpToNextLevel());
    }

    private boolean hasUnlockedNeighborPlot(int x, int y) {
        int[][] directions = {{0,1}, {1,0}, {0,-1}, {-1,0}};
        for (int[] dir : directions) {
            int nx = x + dir[0];
            int ny = y + dir[1];
            if (nx >= 0 && nx < farm.getWidth() && ny >= 0 && ny < farm.getHeight()) {
                if (!farm.getPlot(nx, ny).isBlocked()) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean hasUnlockedNeighborPen(int px, int py) {
        int[][] directions = {{0,1}, {1,0}, {0,-1}, {-1,0}};
        for (int[] dir : directions) {
            int nx = px + dir[0];
            int ny = py + dir[1];
            if (nx >= 0 && nx < farm.getPenWidth() && ny >= 0 && ny < farm.getPenHeight()) {
                if (!farm.getAnimalPen(nx, ny).isBlocked()) {
                    return true;
                }
            }
        }
        return false;
    }


    @Override public void resize(int width, int height) {
        gameViewport.update(width, height, true);
        stage.getViewport().update(width, height,true);
    }
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
