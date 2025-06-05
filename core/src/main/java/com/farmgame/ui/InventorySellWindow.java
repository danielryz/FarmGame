package com.farmgame.ui;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.farmgame.player.Inventory;
import com.farmgame.player.InventoryItem;
import com.farmgame.player.Player;

public class InventorySellWindow extends Window {

    private final Runnable onSellCallback;

    private final Table contentTable;
    public InventorySellWindow(String tile, Skin skin, Player player, Runnable onSellCallback) {
        super(tile, skin);

        this.onSellCallback = onSellCallback;

        this.setModal(true);
        this.setMovable(true);
        this.setKeepWithinStage(true);
        this.setSize(500, 600);

        contentTable = new Table();
        contentTable.defaults().pad(5);

        rebuildItemList(player);

        ScrollPane scrollPane = new ScrollPane(contentTable, skin);
        scrollPane.setFadeScrollBars(false);


        TextButton closeButton = new TextButton("Zamknij", skin);
        closeButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                remove();
            }
        });

        this.add(scrollPane).expand().fill().row();
        this.add(closeButton).pad(10);
    }

    private void rebuildItemList(Player player){
        contentTable.clearChildren();

        Inventory inventory = player.getPlayerInventory();

        for(InventoryItem item : inventory.getItems()){
            String itemInfoText = String.format(
                "%s (Ilość: %d) • Cena: %d $",
                item.getName(),
                item.getQuantity(),
                item.getSellPrice()
            );

            Label itemInfoLabel = new Label(itemInfoText, getSkin());
            itemInfoLabel.setWrap(true);

            TextButton sellButton = getSellButton(player, item, inventory);

            contentTable.add(itemInfoLabel).growX().left();
            contentTable.add(sellButton).right().row();
        }
    }

    private TextButton getSellButton(Player player, InventoryItem item, Inventory inventory) {
        TextButton sellButton = new TextButton("Sprzedaj", getSkin());
        sellButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if(item.getQuantity() > 0){
                    item.decrementQuantity(1);
                    player.addMoney(item.getSellPrice());
                    player.addExp(1);

                    if(onSellCallback != null) onSellCallback.run();

                    if(item.getQuantity() <= 0){
                        inventory.removeItem(item.getName(), 0);
                    }

                    rebuildItemList(player);
                } else {
                    System.out.println("Brak przedmiotów w Magazynie!");
                }
            }
        });
        return sellButton;
    }
}
