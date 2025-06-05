package com.farmgame.ui;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.farmgame.game.PlantDatabase;
import com.farmgame.game.PlantType;

public class PlantSelectionWindow extends Window {

    private final OnPlantChosenListener chosenListener;

    public PlantSelectionWindow(String tile, Skin skin, OnPlantChosenListener chosenListener){
        super(tile, skin);

        this.chosenListener = chosenListener;
        this.setModal(true);
        this.setMovable(true);
        this.setKeepWithinStage(true);
        this.setSize(500, 600);

        Table contentTable = new Table();
        contentTable.defaults().pad(5);

        for (PlantType type : PlantDatabase.getAll()) {
            Pixmap pixmap = new Pixmap(16, 16, Pixmap.Format.RGBA8888);
            pixmap.setColor(type.getColor());
            pixmap.fill();
            Texture texture = new Texture(pixmap);
            pixmap.dispose();

            Image colorBox = new Image(texture);

            String infoText =  String.format("%s\nKoszt: %d  $ | Sprzedaż: %d $\nCzas wzrostu: %.1fs",
                type.getName(), type.getSeedPrice(), type.getSellPrice(), type.getGrowthTime());
            Label plantInfoLabel = new Label(infoText, skin);
            plantInfoLabel.setWrap(true);

            TextButton chooseButton = new TextButton("Wybierz", skin);
            chooseButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    chosenListener.onChosen(type);
                    remove();
                }
            });

            contentTable.add(colorBox).size(16).padRight(5);
            contentTable.add(plantInfoLabel).growX().left();
            contentTable.add(chooseButton).right().row();
            contentTable.row();
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
