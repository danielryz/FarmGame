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
import com.farmgame.game.*;
import com.farmgame.player.Player;

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
    private final Label moneyLabel;
    private final Table inventoryTable;
    private final OrthographicCamera camera;
    private final Viewport gameViewport;
    private final GameClock gameClock;
    private final Label clockLabel;
    private Player player;
    private Label playerNameLabel;
    private Label playerLevelLabel;
    private Label playerExpLabel;
    private Label expToNextLevelLabel;


    public GameScreen() {
        this.farm = new Farm(10, 10);
        this.shapeRenderer = new ShapeRenderer();
        this.batch = new SpriteBatch();
        this.camera = new OrthographicCamera();
        this.gameViewport = new ScreenViewport(camera);
        this.gameClock = new GameClock();
        this.player = new Player("FarmGame");

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("assets/arial.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 16;
        BitmapFont font16 = generator.generateFont(parameter);
        generator.dispose();

        skin = new Skin(Gdx.files.internal("assets/uiskin.json"));
        stage = new Stage(new ScreenViewport(), batch);

        // Główna struktura interfejsu
        Table mainTable = new Table();
        mainTable.setFillParent(true);
        stage.addActor(mainTable);

        // Inicjalizacja komponentów
        this.moneyLabel = new Label("Pieniądze: " + player.getMoney(), skin);
        this.clockLabel = new Label("", skin);

        // Prawa strona z menu
        Table rightSideMenu = new Table();
        rightSideMenu.top();
        rightSideMenu.pad(10);

        Table leftSideMenu = new Table();
        leftSideMenu.top().pad(10).left();

        playerNameLabel = new Label("Imie: " + player.getName(), skin);
        playerLevelLabel = new Label("Poziom: " + player.getLevel(), skin);
        playerExpLabel = new Label("Exp: " + player.getExp(), skin);
        expToNextLevelLabel = new Label("Exp do kolejnego poziomu: " + player.getExpToNextLevel(), skin);

        leftSideMenu.add(playerNameLabel).left().row();
        leftSideMenu.add(playerLevelLabel).left().row();
        leftSideMenu.add(playerExpLabel).left().row();
        leftSideMenu.add(expToNextLevelLabel).left().row();

        // Górny pasek (czas i pieniądze)
        Table topBar = new Table();
        topBar.add(clockLabel).left().expandX();
        topBar.add(moneyLabel).right();
        rightSideMenu.add(topBar).fillX().expandX().padBottom(10).row();

        // Kontener na przyciski i akcje
        Table sidebar = new Table();
        sidebar.defaults().pad(4).left();

        // Sekcja roślin
        sidebar.add(new Label("Rośliny:", skin)).left().row();
        Table plantButtons = new Table();


        for (PlantType type : PlantDatabase.getAll()) {
            Pixmap pixmap = new Pixmap(16, 16, Pixmap.Format.RGBA8888);
            pixmap.setColor(type.getColor());
            pixmap.fill();
            Texture texture = new Texture(pixmap);
            pixmap.dispose();

            Image colorBox = new Image(texture);
            TextButton button = new TextButton(type.getName(), skin);
            button.getLabel().setAlignment(Align.left);

            Label.LabelStyle labelStyle = new Label.LabelStyle();
            labelStyle.font = font16;

            Label infoLabel = new Label("Kup: " + type.getSeedPrice() + "$ | Sprzedaj: " + type.getSellPrice()
                + "$" + "\nCzas wzrostu: " + type.getGrowthTime(), labelStyle);

            button.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    selectedPlant = type;
                    currentAction = Action.PLANT;
                    if (selectedButton != null) selectedButton.setColor(Color.WHITE);
                    selectedButton = button;
                    button.setColor(Color.CYAN);
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

        // Sekcja akcji
        sidebar.add(new Label("Akcje:", skin)).left().padTop(10).row();

        waterButton = new TextButton("Podlej", skin);
        harvestButton = new TextButton("Zbierz", skin);
        TextButton sellAllButton = getSellAllButton();

        sidebar.add(sellAllButton).expandX().fillX().padTop(10).row();
        sidebar.add(waterButton).expandX().fillX().row();
        sidebar.add(harvestButton).expandX().fillX().row();

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

        // Sekcja magazynu
        sidebar.add(new Label("Magazyn:", skin)).left().padTop(10).row();
        this.inventoryTable = new Table();
        updateInventoryTable(inventoryTable);
        sidebar.add(inventoryTable).expandX().fillX().row();

        // Dodanie scrollowania do menu
        ScrollPane scrollPane = new ScrollPane(sidebar, skin);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setScrollingDisabled(true, false);

        rightSideMenu.add(scrollPane).expand().fill().top();
        // Dodanie obszarów do głównej tabeli
        mainTable.add(leftSideMenu).width(200).padLeft(10).fill().top();
        mainTable.add().expand().fill();
        mainTable.add(rightSideMenu).width(400).fill().top();

        // Konfiguracja obsługi wejścia
        InputAdapter gameInput = new InputAdapter() {
            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                Vector3 worldCoords = camera.unproject(new Vector3(screenX, screenY, 0));
                int x = (int) (worldCoords.x / TILE_SIZE);
                int y = (int) (worldCoords.y / TILE_SIZE);

                Plot plot = farm.getPlot(x, y);
                if (plot == null) return false;

                if (plot.isBlocked() && hasUnlockedNeighbor(x, y)) {
                    int price = farm.getPlotPrice(x, y);
                    if (player.getMoney() >= price) {
                        plot.unlock();
                        player.addMoney(-price);
                        player.addExp(2);
                        updatePlayerStatus();
                    }
                    return true;
                }

                switch (currentAction) {
                    case PLANT -> {
                        if (selectedPlant != null && plot.getState() == Plot.State.EMPTY) {
                            int cost = selectedPlant.getSeedPrice();
                            if (player.getMoney() >= cost) {
                                player.addMoney(-cost);
                                player.addExp(1);
                                updatePlayerStatus();
                                plot.plant(new Plant(selectedPlant));

                            } else {
                                System.out.println("Za mało pieniędzy!");
                            }
                        }
                    }
                    case WATER -> {
                        plot.water();
                        player.addExp(1);
                        updatePlayerStatus();
                    }
                    case HARVEST -> {
                        if (plot.getState() == Plot.State.READY_TO_HARVEST && plot.getPlant() != null) {
                            inventory.addItem(plot.getPlant().getType().getName(), 1);
                            plot.harvest();
                            updateInventoryTable(inventoryTable);
                            player.addExp(1);
                            updatePlayerStatus();
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
                int count = 0;
                for (var entry : inventory.getItems().entrySet()) {
                    String plantName = entry.getKey();
                    int quantity = entry.getValue();
                    if (quantity <= 0) continue;

                    PlantType type = PlantDatabase.getByName(plantName);
                    if (type != null) {
                        earned += quantity * type.getSellPrice();
                        count += quantity;
                    }
                }
                if (count > 0) {
                    player.addExp(count);
                }
                player.addMoney(earned);
                inventory.clearItem();
                updateInventoryTable(inventoryTable);
                updatePlayerStatus();
            }
        });
        return sellAllButton;
    }

    @Override
    public void render(float delta) {
        farmUpdate(delta);
        gameClock.update(delta);
        updateClockLabel();

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
        for (int x = 0; x < farm.getWidth(); x++) {
            for (int y = 0; y < farm.getHeight(); y++) {
                Plot plot = farm.getPlot(x, y);

                Color baseColor = switch (plot.getState()) {
                    case BLOCKED -> Color.GRAY;
                    case EMPTY -> Color.BROWN;
                    case PLANTED -> Color.SKY;
                    case GROWTH -> Color.FOREST;
                    case READY_TO_HARVEST -> Color.GOLD;
                };
                shapeRenderer.setColor(baseColor);

                if(!plot.isBlocked() || hasUnlockedNeighbor(x, y)) {
                    shapeRenderer.rect(x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE - 2, TILE_SIZE - 2);
                }

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
        batch.setColor(Color.WHITE);

        batch.begin();
        for (int x = 0; x < farm.getWidth(); x++) {
            for (int y = 0; y < farm.getHeight(); y++) {
                Plot plot = farm.getPlot(x, y);
                Plant plant = plot.getPlant();

                if (plot.isBlocked() && hasUnlockedNeighbor(x, y)) {
                    String plusSign = "+";
                    GlyphLayout plusLayout = new GlyphLayout(font, plusSign);
                    float plusX = x * TILE_SIZE + (TILE_SIZE - plusLayout.width) / 2;
                    float plusY = y * TILE_SIZE + (TILE_SIZE + plusLayout.height) / 2;

                    font.setColor(Color.WHITE);
                    font.draw(batch, plusSign, plusX, plusY);

                    String priceText = farm.getPlotPrice(x, y) + "$";
                    GlyphLayout priceLayout = new GlyphLayout(font, priceText);
                    float priceX = x * TILE_SIZE + 2;
                    float priceY = y * TILE_SIZE + priceLayout.height + 2;

                    font.getData().setScale(1f);
                    font.draw(batch, priceText, priceX, priceY);
                    font.getData().setScale(1.5f);
                }

                if (plant != null && plot.getState() != Plot.State.EMPTY) {
                    float timeLeft = plant.getTimeRemaining();
                    String timeText = String.format("%.0f", timeLeft);

                    GlyphLayout layout = new GlyphLayout(font, timeText);
                    float textX = x * TILE_SIZE + (TILE_SIZE - layout.width) / 2;
                    float textY = y * TILE_SIZE + (TILE_SIZE + layout.height) / 2;


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
        batch.end();

        moneyLabel.setText("Pieniądze: " + player.getMoney());

        stage.act(delta);
        stage.draw();
    }

    private boolean isGrowthPossible(){
        GameClock.TimeOfDay timeOfDay = gameClock.getTimeOfDay();
        return timeOfDay != GameClock.TimeOfDay.NIGHT;
    }

    private float getGrowthMultiplier(){
        return switch (gameClock.getTimeOfDay()){
            case MORNING -> 1.2f;
            case NOON -> 1.5f;
            case EVENING -> 1.0f;
            case NIGHT -> 0.0f;
        };
    }


    private void farmUpdate(float delta) {
        float growthMultiplier = getGrowthMultiplier();
        boolean canGrow = isGrowthPossible();

        for (int x = 0; x < farm.getWidth(); x++) {
            for (int y = 0; y < farm.getHeight(); y++) {
                Plot plot = farm.getPlot(x, y);
                if (canGrow){
                    plot.update(delta * growthMultiplier);
                } else {
                    plot.update(0);
                }
            }
        }
    }

    private void updateClockLabel(){
        StringBuilder timeText = new StringBuilder();
        timeText.append(String.format("Dzień %d (%s) - %s",
            gameClock.getDay(),
            getPolishWeekDay(gameClock.getWeekDay()),
            getPolishTimeOfDay(gameClock.getTimeOfDay())
        ));

        if (!isGrowthPossible()){
            timeText.append(" (Rośliny nie rosną)");
        }
        clockLabel.setText(timeText);
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
        return switch (gameClock.getTimeOfDay()) {
            case MORNING -> new Color(1f, 0.9f, 0.8f, 1f);
            case NOON -> new Color(1f, 1f, 1f, 1f);
            case EVENING -> new Color(0.8f, 0.7f, 0.6f, 1f);
            case NIGHT -> new Color(0.4f, 0.4f, 0.5f, 1f);
        };
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
                    player.addMoney(type.getSellPrice());
                    player.addExp(1);
                    updateInventoryTable(inventoryTable);
                    updatePlayerStatus();
                }
            });

            Table row = new Table();
            row.add(itemLabel).left().expandX();
            row.add(sellButton).right();
            inventoryTable.add(row).fillX().padBottom(2).row();
        }
    }

    private void updatePlayerStatus(){
        playerLevelLabel.setText("Poziom: " + player.getLevel());
        playerExpLabel.setText("Exp: " + player.getExp());
        expToNextLevelLabel.setText("Exp do kolejnego poziomu: " + player.getExpToNextLevel());
    }

    private boolean hasUnlockedNeighbor(int x, int y) {
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
