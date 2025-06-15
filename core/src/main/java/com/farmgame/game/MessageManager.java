package com.farmgame.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Align;
import java.util.ArrayList;
import java.util.List;

public class MessageManager {
    private static final float SPACING = 5f;
    private static final float START_Y = Gdx.graphics.getHeight() - 50f;
    private static final List<Label> activeMessages = new ArrayList<>();
    private static Skin skin;
    private static Stage stage;

    public static void initialize(Skin skin, Stage stage) {
        MessageManager.skin = skin;
        MessageManager.stage = stage;
    }
    public static void showMessage(String text, Color color) {
        if (stage == null) {
            Gdx.app.error("MessageManager", "Nie zainicjalizowano Stage!");
            return;
        }

        if (activeMessages.size() >= 5) {
            Label oldest = activeMessages.remove(0);
            oldest.remove();
        }

        Label message = new Label(text, skin);
        message.setAlignment(Align.center);
        message.setColor(color);

        message.addAction(Actions.sequence(
            Actions.alpha(0),
            Actions.fadeIn(0.3f),
            Actions.delay(5),
            Actions.fadeOut(0.3f),
            Actions.run(() -> removeMessage(message))
        ));

        float textWidth = message.getPrefWidth();
        float screenCenterX = Gdx.graphics.getWidth() / 2f - textWidth / 2f;

        message.setPosition(screenCenterX, START_Y - message.getHeight());
        activeMessages.add(message);
        stage.addActor(message);

        for (Label msg : activeMessages) {
            msg.addAction(Actions.moveTo(msg.getX(), msg.getY() - message.getHeight() - SPACING, 0.3f));
        }
    }

    private static void removeMessage(Label message) {
        activeMessages.remove(message);
    }

    public static void info(String text) {
        showMessage(text, Color.WHITE);
    }

    public static void success(String text) {
        showMessage(text, Color.GOLD);
    }

    public static void warning(String text) {
        showMessage(text, Color.ORANGE);
    }

    public static void error(String text) {
        showMessage(text, Color.RED);
    }
}
