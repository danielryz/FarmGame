package com.farmgame.ui;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.farmgame.game.MessageManager;
import com.farmgame.player.Player;

public class StorageUpgradeWindow extends Window {
    private static final int BASE_UPGRADE_COST = 100;
    private static final int CAPACITY_INCREMENT = 50;
    private static final int EXP_REWARD = 5;

    public StorageUpgradeWindow(String title, Skin skin, Player player, Runnable onUpgrade) {
        super(title, skin);
        this.setModal(true);
        this.setMovable(true);
        this.setKeepWithinStage(true);
        this.setSize(300, 200);

        int nextCapacity = player.getInventoryCapacity() + CAPACITY_INCREMENT;
        int upgradeCost = BASE_UPGRADE_COST * (player.getInventoryCapacity() / CAPACITY_INCREMENT);

        Label infoLabel = new Label("Zwiększ pojemność do " + nextCapacity + " za " + upgradeCost + "$ (+" + EXP_REWARD + " exp)", skin);
        infoLabel.setWrap(true);

        TextButton upgradeButton = new TextButton("Ulepsz", skin);
        TextButton cancelButton = new TextButton("Anuluj", skin);

        upgradeButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (player.getMoney() >= upgradeCost) {
                    player.increaseInventoryCapacity(CAPACITY_INCREMENT);
                    player.addMoney(-upgradeCost);
                    player.addExp(EXP_REWARD);
                    if (onUpgrade != null) onUpgrade.run();
                    MessageManager.info("Magazyn ulepszony!");
                    remove();
                } else {
                    MessageManager.warning("Za mało pieniędzy na ulepszenie!");
                }
            }
        });

        cancelButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                remove();
            }
        });

        Table table = new Table();
        table.add(infoLabel).colspan(2).growX().pad(10).row();
        table.add(upgradeButton).pad(10);
        table.add(cancelButton).pad(10);

        this.add(table).expand().fill();
    }
}
