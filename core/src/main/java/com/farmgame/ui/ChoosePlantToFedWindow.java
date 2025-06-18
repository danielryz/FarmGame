package com.farmgame.ui;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.farmgame.game.AnimalType;
import com.farmgame.game.PlantDatabase;
import com.farmgame.game.PlantType;
import com.farmgame.player.Inventory;
import com.farmgame.player.InventoryItem;
import com.farmgame.player.Player;

import java.util.ArrayList;
import java.util.List;

public class ChoosePlantToFedWindow extends Window {

    private final OnFeedChosenListener chosenListener;

    public ChoosePlantToFedWindow(String title, Skin skin, Player player, AnimalType animalType, OnFeedChosenListener chosenListener) {
        super(title, skin);

        this.chosenListener = chosenListener;
        this.setModal(true);
        this.setMovable(true);
        this.setKeepWithinStage(true);
        this.setSize(400, 500);

        Table contentTable = new Table();
        contentTable.defaults().pad(5);

        Inventory inventory = player.getPlayerInventory();
        List<PlantType> availablePlants = new ArrayList<>();

        for (InventoryItem item : inventory.getItems()) {
            String plantName = item.getName();
            if (animalType.getFeedSet().contains(plantName)) {
                PlantType plantType = PlantDatabase.getByName(plantName);
                if (plantType != null) {
                    availablePlants.add(plantType);
                }
            }
        }

        if (availablePlants.isEmpty()) {
            Label noPlantsLabel = new Label("Brak odpowiednich roślin w magazynie dla tego zwierzęcia.", skin);
            noPlantsLabel.setWrap(true);
            contentTable.add(noPlantsLabel).pad(10).row();
        } else {
            for (PlantType type : availablePlants) {
                Pixmap pixmap = new Pixmap(16, 16, Pixmap.Format.RGBA8888);
                pixmap.setColor(type.getColor());
                pixmap.fill();
                Texture texture = new Texture(pixmap);
                pixmap.dispose();

                Image colorBox = new Image(texture);

                int quantity = inventory.getQuantity(type.getName());
                String infoText = String.format("%s (Ilość: %d)\nWartość: %d $",
                        type.getName(), quantity, type.getSellPrice());

                Label plantInfoLabel = new Label(infoText, skin);
                plantInfoLabel.setWrap(true);

                TextButton chooseButton = new TextButton("Wybierz", skin);
                chooseButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        final TextField qtyField = new TextField("1", skin);
                        Dialog quantityDialog = new Dialog("Ilość", skin) {
                            @Override
                            protected void result(Object obj) {
                                if (Boolean.TRUE.equals(obj)) {
                                    int qty = 1;
                                    try {
                                        qty = Integer.parseInt(qtyField.getText());
                                    } catch (NumberFormatException ignored) {}
                                    if (qty < 1) qty = 1;
                                    if (qty > quantity) qty = quantity;
                                    chosenListener.onChosen(type, qty);
                                    ChoosePlantToFedWindow.this.remove();
                                }
                            }
                        };
                        quantityDialog.getContentTable().add(new Label("Ilość (max " + quantity + ")", skin)).row();
                        quantityDialog.getContentTable().add(qtyField).width(100);
                        quantityDialog.button("OK", true);
                        quantityDialog.button("Anuluj", false);
                        quantityDialog.show(getStage());
                    }
                });

                contentTable.add(colorBox).size(16).padRight(5);
                contentTable.add(plantInfoLabel).growX().left();
                contentTable.add(chooseButton).right().row();
                contentTable.row();
            }
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
