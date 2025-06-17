package com.farmgame.ui;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.farmgame.game.AnimalPen;
import com.farmgame.game.MessageManager;
import com.farmgame.player.Player;

public class PenUpgradeWindow extends Window {
    private static final int BASE_UPGRADE_COST = 200;
    private static final int EXP_REWARD = 5;

    public PenUpgradeWindow(String title, Skin skin, Player player, AnimalPen pen, Runnable onUpgrade) {
        super(title, skin);
        this.setModal(true);
        this.setMovable(true);
        this.setKeepWithinStage(true);
        this.setSize(300, 200);

        int nextCapacity = pen.getCapacity() + 1;
        int upgradeCost = BASE_UPGRADE_COST * pen.getCapacity();

        String infoText;
        if (pen.isMaxCapacity()) {
            infoText = "Osiągnięto maksymalną pojemność";
        } else {
            infoText = "Zwiększ pojemność do " + nextCapacity +
                " za " + upgradeCost + "$ (+" + EXP_REWARD + " exp)";
        }

        Label infoLabel = new Label(infoText, skin);
        infoLabel.setWrap(true);

        TextButton upgradeButton = new TextButton("Ulepsz", skin);
        TextButton cancelButton = new TextButton("Anuluj", skin);

        upgradeButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (pen.isMaxCapacity()) {
                    MessageManager.info("Zagroda ma już maksymalną pojemność!");
                    return;
                }

                if (player.getMoney() >= upgradeCost) {
                    pen.increaseCapacity();
                    player.addMoney(-upgradeCost);
                    player.addExp(EXP_REWARD);
                    if (onUpgrade != null) {
                        onUpgrade.run();
                    }
                    MessageManager.info("Zagroda ulepszona!");
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
        table.add(infoLabel).growX().pad(10).row();
        table.add(upgradeButton).pad(10);
        table.add(cancelButton).pad(10);

        this.add(table).expand().fill();
    }
}
