package com.farmgame.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import com.farmgame.game.DifficultyManager;
import com.farmgame.game.PlantDatabase;
import com.farmgame.game.PlantType;
import com.farmgame.player.Player;

public class PlantSelectionWindow extends Window {

    private final OnPlantChosenListener chosenListener;
    private final DifficultyManager difficultyManager;

    public PlantSelectionWindow(String title, Skin skin, OnPlantChosenListener chosenListener, Player player){
        this(title, skin, chosenListener, player, new DifficultyManager());
    }

    public PlantSelectionWindow(String title, Skin skin, OnPlantChosenListener chosenListener, Player player, float difficultyMultiplier){
        this(title, skin, chosenListener, player, createDifficultyManager(difficultyMultiplier));
    }

    private static DifficultyManager createDifficultyManager(float multiplier) {
        DifficultyManager manager = new DifficultyManager();
        manager.setDifficultyMultiplier(multiplier);
        return manager;
    }

    public PlantSelectionWindow(String title, Skin skin, OnPlantChosenListener chosenListener, Player player, DifficultyManager difficultyManager){
        super(title, skin);
        this.chosenListener = chosenListener;
        this.difficultyManager = difficultyManager;

        this.setModal(true);
        this.setMovable(true);
        this.setKeepWithinStage(true);
        this.setSize(500, 600);

        Table contentTable = new Table();
        contentTable.defaults().pad(5);

        for (PlantType type : PlantDatabase.getAll()) {

            Table rowTable = new Table();
            rowTable.defaults().pad(5);

            Pixmap pixmap = new Pixmap(16, 16, Pixmap.Format.RGBA8888);
            pixmap.setColor(type.getColor());
            pixmap.fill();
            Image colorBox = new Image(new Texture(pixmap));
            pixmap.dispose();

            int adjustedCost = (int)(type.getSeedPrice() / difficultyManager.getMoneyMultiplier());
            int adjustedSellPrice = (int)(type.getSellPrice() / difficultyManager.getMoneyMultiplier());
            float adjustedGrowthTime = type.getGrowthTime() * difficultyManager.getTimeMultiplier();
            String infoText = String.format("%s\nKoszt: %d $ | Sprzedaż: %d $\nCzas wzrostu: %.1fs",
                type.getName(), adjustedCost, adjustedSellPrice, adjustedGrowthTime);
            Label plantInfoLabel = new Label(infoText, skin);
            plantInfoLabel.setWrap(true);

            TextButton chooseButton = new TextButton("Wybierz", skin);

            if (player.getLevel() < type.getRequiredLevel()) {
                Image overlay = new Image(skin.getDrawable("white"));
                overlay.setColor(new Color(0.2f, 0.2f, 0.2f, 0.9f));

                Stack lockedRowStack = new Stack();

                Table content = new Table();
                content.add(colorBox).size(16).padRight(5);
                content.add(plantInfoLabel).growX().left();
                content.add(chooseButton).right();

                lockedRowStack.add(content);
                lockedRowStack.add(overlay);

                Table lockOverlayTable = new Table();
                lockOverlayTable.setFillParent(true);
                lockOverlayTable.center().pad(5);

                Label lockedLabel = new Label("Dostępne od poziomu: " + type.getRequiredLevel(), skin);
                lockedLabel.setColor(Color.WHITE);
                lockedLabel.setAlignment(Align.center);

                lockOverlayTable.add(lockedLabel).expand().center();

                lockedRowStack.add(lockOverlayTable);

                rowTable.add(lockedRowStack).growX();
            } else {
                chooseButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        chosenListener.onChosen(type);
                        remove();
                    }
                });

                rowTable.add(colorBox).size(16).padRight(5);
                rowTable.add(plantInfoLabel).growX().left();
                rowTable.add(chooseButton).right();
            }

            contentTable.add(rowTable).growX().row();
        }

        TextButton closeButton = new TextButton("Zamknij", skin);
        closeButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                remove();
            }
        });

        ScrollPane scrollPane = new ScrollPane(contentTable, skin);
        scrollPane.setFadeScrollBars(false);

        this.add(scrollPane).expand().fill().row();
        this.add(closeButton).pad(10);
    }
}
