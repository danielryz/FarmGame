package com.farmgame.ui;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.farmgame.quest.Quest;
import com.farmgame.quest.QuestManager;
import com.farmgame.player.Player;

public class QuestWindow extends Window {
    private final QuestManager questManager;
    private final Player player;
    private final Runnable onClaim;
    private final Table contentTable;

    public QuestWindow(String title, Skin skin, QuestManager questManager, Player player, Runnable onClaim) {
        super(title, skin);
        this.questManager = questManager;
        this.player = player;
        this.onClaim = onClaim;

        this.setModal(true);
        this.setMovable(true);
        this.setKeepWithinStage(true);
        this.setSize(400, 300);

        contentTable = new Table();
        contentTable.defaults().pad(5);
        rebuild();

        ScrollPane scroll = new ScrollPane(contentTable, skin);
        scroll.setFadeScrollBars(false);

        TextButton closeBtn = new TextButton("Zamknij", skin);
        closeBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                remove();
            }
        });

        this.add(scroll).expand().fill().row();
        this.add(closeBtn).pad(10);
    }

    private void rebuild() {
        contentTable.clearChildren();
        for (Quest q : questManager.getQuests()) {
            Label label = new Label(q.getName() + " - Dostarcz " + q.getRequiredQuantity() + " x " + q.getItemName() +
                " (Nagroda: " + q.getRewardMoney() + "$, " + q.getRewardExp() + " exp)", getSkin());
            label.setWrap(true);
            if (!q.isClaimed() && q.isCompleted(player)) {
                TextButton claim = new TextButton("Odbierz", getSkin());
                claim.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        if (q.claim(player)) {
                            if (onClaim != null) onClaim.run();
                            rebuild();
                        }
                    }
                });
                contentTable.add(label).growX();
                contentTable.add(claim).right().row();
            } else {
                String status = q.isClaimed() ? "Zakończone" : "W trakcie";
                Label statusLabel = new Label(status, getSkin());
                contentTable.add(label).growX();
                contentTable.add(statusLabel).right().row();
            }
        }
    }
}
