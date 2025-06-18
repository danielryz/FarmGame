package com.farmgame.ui;


import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.farmgame.game.*;
import com.farmgame.player.Player;

public class AnimalSelectionWindow extends Window {

    private final Runnable onBuyCallback;
    private final Player player;
    private final DifficultyManager difficultyManager;

    public AnimalSelectionWindow(String title, Skin skin, Player player, AnimalPen animalPen, Runnable onBuyCallback){
        this(title, skin, player, animalPen, onBuyCallback, new DifficultyManager());
    }

    public AnimalSelectionWindow(String title, Skin skin, Player player, AnimalPen animalPen, Runnable onBuyCallback, float difficultyMultiplier){
        this(title, skin, player, animalPen, onBuyCallback, createDifficultyManager(difficultyMultiplier));
    }

    private static DifficultyManager createDifficultyManager(float multiplier) {
        DifficultyManager manager = new DifficultyManager();
        manager.setDifficultyMultiplier(multiplier);
        return manager;
    }

    public AnimalSelectionWindow(String title, Skin skin, Player player, AnimalPen animalPen, Runnable onBuyCallback, DifficultyManager difficultyManager){
        super(title, skin);
        this.player = player;
        this.onBuyCallback = onBuyCallback;
        this.difficultyManager = difficultyManager;

        this.setModal(true);
        this.setMovable(true);
        this.setKeepWithinStage(true);
        this.setSize(400, 800);

        Table contentTable = new Table();
        contentTable.defaults().pad(5);

        for (AnimalType type : AnimalDatabase.getAll()) {
            Table rowTable = new Table();
            rowTable.defaults().pad(5);

            Pixmap pixmap = new Pixmap(16, 16, Pixmap.Format.RGBA8888);
            pixmap.setColor(type.getColor());
            pixmap.fill();
            Texture texture = new Texture(pixmap);
            pixmap.dispose();

            Image colorBox = new Image(texture);

            int adjustedCost = (int)(type.getCost() / difficultyManager.getMoneyMultiplier());
            int adjustedSellPrice = (int)(type.getSellPrice() / difficultyManager.getMoneyMultiplier());
            float adjustedProductTime = type.getProductTime() * difficultyManager.getTimeMultiplier();
            String infoText = String.format("%s\nKoszt: %d\nCzas produkcji %s: %.1fs\nZysk ze sprzedaży %s: %d",
                type.getName(), adjustedCost, type.getProductName(), adjustedProductTime, type.getProductName(), adjustedSellPrice);

            Label animalInfoLabel = new Label(infoText, skin);
            animalInfoLabel.setWrap(true);

            TextButton buyButton = new TextButton("Kup", skin);

            if (player.getLevel() < type.getRequiredLevel()) {
                Image overlay = new Image(skin.getDrawable("white"));
                overlay.setColor(new Color(0.2f, 0.2f, 0.2f, 0.9f));

                Stack lockedRowStack = new Stack();

                Table content = new Table();
                content.add(colorBox).size(16).padRight(5);
                content.add(animalInfoLabel).growX().left();
                content.add(buyButton).right();

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
            }
            else {
                buyButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        if (!animalPen.isFull()) {
                            if (!animalPen.canAcceptType(type)) {
                                MessageManager.warning("Zagroda przyjmuje tylko: " + animalPen.getAllowedType().getName());
                                return;
                            }
                            int adjustedCost = (int)(type.getCost() / difficultyManager.getMoneyMultiplier());
                            if (player.getMoney() >= adjustedCost) {
                                Animal newAnimal = new Animal(type, difficultyManager);
                                animalPen.placeAnimal(newAnimal, player, difficultyManager);

                                if (onBuyCallback != null) {
                                    onBuyCallback.run();
                                }

                                remove();
                            } else {
                                MessageManager.warning("Za mało pieniędzy na kupno: " + type.getName());
                            }
                        } else {
                            MessageManager.warning("Zagroda jest pełna!");
                        }
                    }
                });
                rowTable.add(colorBox).size(16).padRight(5);
                rowTable.add(animalInfoLabel).growX().left();
                rowTable.add(buyButton).right();
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
