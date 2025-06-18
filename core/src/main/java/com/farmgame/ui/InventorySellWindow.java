package com.farmgame.ui;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.farmgame.game.MessageManager;
import com.farmgame.player.Inventory;
import com.farmgame.player.InventoryItem;
import com.farmgame.player.Player;

public class InventorySellWindow extends Window {

    private final Runnable onSellCallback;
    private final Player player;

    private final Table contentTable;
    private final Label capacityLabel;
    public InventorySellWindow(String title, Skin skin, Player player, Runnable onSellCallback) {
        super(title, skin);

        this.player = player;
        this.onSellCallback = onSellCallback;

        this.setModal(true);
        this.setMovable(true);
        this.setKeepWithinStage(true);
        this.setSize(500, 600);

        contentTable = new Table();
        contentTable.defaults().pad(5);

        capacityLabel = new Label("", skin);
        contentTable.add(capacityLabel).left().colspan(2).row();

        rebuildItemList(player);

        ScrollPane scrollPane = new ScrollPane(contentTable, skin);
        scrollPane.setFadeScrollBars(false);

        TextButton upgradeButton = new TextButton("Ulepsz", skin);
        TextButton closeButton = new TextButton("Zamknij", skin);
        closeButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                remove();
            }
        });
        upgradeButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                StorageUpgradeWindow window = new StorageUpgradeWindow(
                    "Ulepsz magazyn",
                    skin,
                    player,
                    () -> {
                        refreshInventory();
                        if (onSellCallback != null) onSellCallback.run();
                    }
                );
                getStage().addActor(window);
                window.setPosition(
                    (getStage().getWidth() - window.getWidth()) / 2f,
                    (getStage().getHeight() - window.getHeight()) / 2f
                );
            }
        });
        this.add(scrollPane).expand().fill().row();
        Table bottom = new Table();
        bottom.add(upgradeButton).pad(10);
        bottom.add(closeButton).pad(10);
        this.add(bottom);
    }

    private void rebuildItemList(Player player){
        contentTable.clearChildren();

        Inventory inventory = player.getPlayerInventory();

        capacityLabel.setText("Pojemność: " + inventory.getUsedCapacity() + "/" + inventory.getCapacity());
        contentTable.add(capacityLabel).left().colspan(2).row();

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
                    MessageManager.warning("Brak przedmiotów w Magazynie!");
                }
            }
        });
        return sellButton;
    }

    public void refreshInventory() {
        rebuildItemList(player);
    }
}
