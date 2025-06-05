package com.farmgame.ui;


import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.farmgame.game.Animal;
import com.farmgame.game.AnimalDatabase;
import com.farmgame.game.AnimalPen;
import com.farmgame.game.AnimalType;
import com.farmgame.player.Player;

public class AnimalSelectionWindow extends Window {

    public AnimalSelectionWindow(String tile, Skin skin, Player player, AnimalPen animalPen){
        super(tile, skin);

        this.setModal(true);
        this.setMovable(true);
        this.setKeepWithinStage(true);
        this.setSize(300, 400);

        Table contentTable = new Table();
        contentTable.defaults().pad(5);

        for (AnimalType type : AnimalDatabase.getAll()) {

            String infoText = String.format("%s\nKoszt: %d\nCzas produkcji %s: %.1fs\nZysk ze sprzedaży %s: %d",
                type.getName(), type.getCost(), type.getProductName(),type.getProductTime(), type.getProductName(), type.getSellPrice());

            Label animalInfoLabel = new Label(infoText, skin);
            animalInfoLabel.setWrap(true);

            TextButton buyButton = new TextButton("Kup", skin);
            buyButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    if (animalPen.getState() == AnimalPen.State.EMPTY) {
                        if (player.getMoney() >= type.getCost()) {
                            Animal newAnimal = new Animal(type);
                            animalPen.placeAnimal(newAnimal, player);

                            remove();
                        } else {
                            System.out.println("Za mało pieniędzy na kupno: " + type.getName());
                        }
                    } else {
                        System.out.println("Zagroda jest już zajęta!");
                    }
                }
            });

            contentTable.add(animalInfoLabel).growX().left();
            contentTable.add(buyButton).right().row();
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
